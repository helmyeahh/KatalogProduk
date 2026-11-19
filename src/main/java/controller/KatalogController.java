package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import data.ProdukRepository;
import model.Produk;

public class KatalogController {

    @FXML
    private TextField namaField;
    @FXML
    private TextField hargaField;
    @FXML
    private ComboBox<String> fotoBox;
    @FXML
    private Label pesanLabel;
    @FXML
    private Label jumlahLabel;
    @FXML
    private TilePane katalogPane;

    @FXML
    private Button simpanButton;
    @FXML
    private Button batalButton;

    private final ProdukRepository repository = new ProdukRepository();
    private Long editingProductId = null;

    @FXML
    private void initialize() {
        fotoBox.getItems().add("roti.png");
        fotoBox.getItems().add("susu.png");
        fotoBox.getItems().add("kopi.png");
        fotoBox.setValue("roti.png");
        setupActionButtonsIfMissing();
        resetFormMode();
        try {
            showData();
        } catch (SQLException e) {
            pesanLabel.setText("Gagal membaca database. Lihat Output.");
            e.printStackTrace();
        }
    }

    private void setupActionButtonsIfMissing() {
        if (simpanButton == null) {
            simpanButton = new Button("Simpan");
            simpanButton.setOnAction(e -> saveProduct());
            if (fotoBox.getParent() instanceof Pane parent) {
                parent.getChildren().add(simpanButton);
            }
        } else {
            simpanButton.setOnAction(e -> saveProduct());
        }

        if (batalButton == null) {
            batalButton = new Button("Batal");
            batalButton.setOnAction(e -> {
                resetFormMode();
                pesanLabel.setText("Mode edit dibatalkan.");
            });
            if (simpanButton.getParent() instanceof Pane parent) {
                parent.getChildren().add(batalButton);
            }
        } else {
            batalButton.setOnAction(e -> {
                resetFormMode();
                pesanLabel.setText("Mode edit dibatalkan.");
            });
        }
    }

    private void resetFormMode() {
        editingProductId = null;
        namaField.clear();
        hargaField.clear();
        if (!fotoBox.getItems().isEmpty()) {
            fotoBox.setValue(fotoBox.getItems().get(0));
        }
        if (simpanButton != null) {
            simpanButton.setText("Simpan");
        }
        if (batalButton != null) {
            batalButton.setVisible(false);
            batalButton.setManaged(false);
        }
    }

    private void startEdit(Produk produk) {
        editingProductId = produk.getId();
        namaField.setText(produk.getNama());
        hargaField.setText(String.valueOf(produk.getHarga()));
        fotoBox.setValue(produk.getFoto());

        if (simpanButton != null) {
            simpanButton.setText("Update");
        }
        if (batalButton != null) {
            batalButton.setVisible(true);
            batalButton.setManaged(true);
        }
        pesanLabel.setText("Mengedit produk ID: " + produk.getId());
        namaField.requestFocus();
    }

    private void deleteProduct(Produk produk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus produk \"" + produk.getNama() + "\"?");
        alert.setContentText("ID: " + produk.getId() + " | Harga: Rp" + produk.getHarga());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean terhapus = repository.deleteById(produk.getId());
            if (!terhapus) {
                pesanLabel.setText("Produk tidak ditemukan di database.");
                return;
            }
            if (editingProductId != null && editingProductId == produk.getId()) {
                resetFormMode();
            }
            showData();
            pesanLabel.setText("Produk berhasil dihapus.");
        } catch (SQLException e) {
            pesanLabel.setText("Gagal menghapus produk. Lihat Output.");
            e.printStackTrace();
        }
    }

    private void showData() throws SQLException {
        ArrayList<Produk> daftar = repository.getAll();
        katalogPane.getChildren().clear();
        for (Produk produk : daftar) {
            VBox kartu = createProductCard(produk);
            katalogPane.getChildren().add(kartu);
        }
        jumlahLabel.setText("Jumlah produk: " + daftar.size());
    }

    private VBox createProductCard(Produk produk) {
        ImageView fotoView = new ImageView();
        fotoView.setFitWidth(170);
        fotoView.setFitHeight(110);
        fotoView.setPreserveRatio(true);
        StackPane bingkai = new StackPane();
        bingkai.setPrefSize(180, 120);
        bingkai.setMinSize(180, 120);
        bingkai.setMaxSize(180, 120);

        URL lokasi = getClass().getResource("/image/" + produk.getFoto());
        if (lokasi != null) {
            Image gambar = new Image(lokasi.toExternalForm(),
                    170, 110, true, true);
            if (!gambar.isError()) {
                fotoView.setImage(gambar);
                bingkai.getChildren().add(fotoView);
            } else {
                bingkai.getChildren().add(new Label("Gambar tidak terbaca"));
            }
        } else {
            bingkai.getChildren().add(new Label("Gambar tidak tersedia"));
        }

        Label namaLabel = new Label(produk.getNama());
        namaLabel.setWrapText(true);
        namaLabel.getStyleClass().add("nama-produk");
        Label hargaLabel = new Label("Rp" + produk.getHarga());
        Label idLabel = new Label("ID: " + produk.getId());
        idLabel.getStyleClass().add("muted");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> startEdit(produk));

        Button hapusButton = new Button("Hapus");
        hapusButton.setOnAction(e -> deleteProduct(produk));

        HBox aksiBox = new HBox(8, editButton, hapusButton);

        VBox kartu = new VBox();
        kartu.setSpacing(8);
        kartu.setMinWidth(220);
        kartu.setPrefWidth(220);
        kartu.setMaxWidth(220);
        kartu.getStyleClass().add("kartu-produk");
        kartu.getChildren().add(bingkai);
        kartu.getChildren().add(namaLabel);
        kartu.getChildren().add(hargaLabel);
        kartu.getChildren().add(idLabel);
        kartu.getChildren().add(aksiBox);
        return kartu;
    }

    @FXML
    private void saveProduct() {
        Produk produk;
        try {
            String nama = namaField.getText();
            long harga = Long.parseLong(hargaField.getText().trim());
            String foto = fotoBox.getValue();
            if (editingProductId == null) {
                produk = new Produk(nama, harga, foto);
            } else {
                produk = new Produk(editingProductId, nama, harga, foto);
            }
        } catch (NumberFormatException e) {
            pesanLabel.setText("Harga harus angka bulat, contoh 12000.");
            return;
        } catch (IllegalArgumentException e) {
            pesanLabel.setText(e.getMessage());
            return;
        }

        if (editingProductId == null) {
            try {
                repository.add(produk);
            } catch (SQLException e) {
                pesanLabel.setText("Gagal menyimpan. Lihat Output.");
                e.printStackTrace();
                return;
            }
            resetFormMode();
            try {
                showData();
                pesanLabel.setText("Produk berhasil disimpan.");
            } catch (SQLException e) {
                pesanLabel.setText("Data tersimpan, tetapi katalog gagal dimuat ulang.");
                e.printStackTrace();
            }
        } else {
            try {
                boolean terupdate = repository.update(produk);
                if (!terupdate) {
                    pesanLabel.setText("Produk tidak ditemukan di database.");
                    return;
                }
            } catch (SQLException e) {
                pesanLabel.setText("Gagal memperbarui produk. Lihat Output.");
                e.printStackTrace();
                return;
            }
            resetFormMode();
            try {
                showData();
                pesanLabel.setText("Produk berhasil diperbarui.");
            } catch (SQLException e) {
                pesanLabel.setText("Data terupdate, tetapi katalog gagal dimuat ulang.");
                e.printStackTrace();
            }
        }
    }
}