# Katalog Produk (JavaFX + Maven + SQLite)

Aplikasi desktop **Katalog Produk** berbasis **Java 17**, **JavaFX 21**, **Maven**, dan **SQLite** untuk mengelola data produk (tambah, lihat, ubah, dan hapus) lengkap dengan tampilan kartu produk dan gambar bawaan (`roti.png`, `susu.png`, `kopi.png`).

---

## Fitur Utama

- **Tambah Produk (`Create`)**: Menambahkan produk baru (nama, harga, dan pilihan foto) dengan validasi input di model (`Produk`) dan batasan (`CHECK` constraint) pada tabel SQLite.
- **Tampilkan Katalog (`Read`)**: Memuat seluruh data produk dari database SQLite (`data/katalog.db`) dan menampilkannya dalam bentuk kartu di antarmuka JavaFX (`katalog.fxml` & `app.css`).
- **Ubah Produk (`Update`)**: Memilih produk dari kartu untuk dimuat kembali ke form, memperbarui data di database berdasarkan `id`, serta menyediakan opsi pembatalan mode edit.
- **Hapus Produk (`Delete`)**: Menghapus data produk berdasarkan `id` dengan dialog konfirmasi dan memperbarui tampilan katalog secara otomatis.

---

## Teknologi & Dependensi

- **Java**: 17
- **JavaFX**: 21 (`javafx-controls`, `javafx-fxml`, dan `javafx-maven-plugin` `0.0.8`)
- **Database**: SQLite via `sqlite-jdbc` (`3.49.1.0`)
- **Build Tool**: Apache Maven (`pom.xml`)

---

## Struktur Proyek

```text
KatalogProduk/
├── data/
│   └── katalog.db                 # Database SQLite lokal
├── pom.xml                        # Konfigurasi Maven & JavaFX 21
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── controller/
    │   │   │   └── KatalogController.java
    │   │   ├── data/
    │   │   │   └── ProdukRepository.java
    │   │   ├── db/
    │   │   │   └── Database.java
    │   │   ├── main/
    │   │   │   └── MainApp.java
    │   │   └── model/
    │   │       └── Produk.java
    │   └── resources/
    │       ├── image/
    │       │   ├── kopi.png
    │       │   ├── roti.png
    │       │   └── susu.png
    │       └── view/
    │           ├── katalog.fxml
    │           └── styles/
    │               └── app.css
    └── test/
        └── java/