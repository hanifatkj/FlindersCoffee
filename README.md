# Flinders Coffee - Integrated Coffee Shop Management System

> **Tugas Akhir / Proyek Praktikum Integrasi Sistem**  
> **Mahasiswa:** Aaron Todingallo  
> **NIM:** 47124011  
> **Kelas:** 3A - Teknik Komputer dan Jaringan (TKJ)  
> **Institusi:** Politeknik Negeri Ujung Pandang (PoliUPG)  

---

## ☕ Ringkasan Sistem (Project Overview)

**Flinders Coffee Management System** adalah ekosistem aplikasi manajemen kafe terintegrasi yang menghubungkan operasional lantai (kasir), analitik manajemen (owner), dan transaksi pelanggan dalam satu basis data terpusat (*Single Source of Truth*).

Sistem ini terdiri dari 4 komponen utama:
1. **Android Mobile App (`/android-app`)**: Aplikasi Android native (Kotlin) untuk Owner & Customer yang menyajikan dashboard finansial real-time, manajemen meja interaktif, manajemen katalog menu & HPP, serta pelacakan pengeluaran operasional.
2. **Web POS Kasir (`/web-pos`)**: Antarmuka kasir berbasis web (*Point of Sale*) modern untuk memproses pesanan pelanggan, memilih meja, kalkulasi diskon/pajak, dan cetak struk pembayaran.
3. **Backend REST API & Database (`/backend-api`)**: Web service RESTful berbasis PHP Native dengan koneksi PDO MySQL (`flinders_coffee`) yang menangani autentikasi, transaksi, perhitungan laba rugi, dan sinkronisasi status meja.
4. **Postman API Test Suite (`/postman`)**: Kumpulan koleksi pengujian endpoint otomatis untuk validasi status `200 OK`, response body JSON, dan integritas data.

---

## 🏛️ Arsitektur Sistem (System Architecture)

```
                    ┌───────────────────────────────────┐
                    │      Flinders Coffee System       │
                    └─────────────────┬─────────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              │                                               │
    ┌─────────▼─────────┐                           ┌─────────▼─────────┐
    │ Android App       │                           │ Cashier Web POS   │
    │ (Kotlin Native)   │                           │ (HTML/CSS/JS)     │
    │ - Owner Dashboard │                           │ - Order Creation  │
    │ - Table Monitor   │                           │ - Billing & Cart  │
    │ - Menu & HPP      │                           │ - Receipt Print   │
    └─────────┬─────────┘                           └─────────┬─────────┘
              │                                               │
              │ JSON (HTTP REST)                              │ JSON (HTTP REST)
              │                                               │
              └───────────────────────┬───────────────────────┘
                                      │
                        ┌─────────────▼─────────────┐
                        │   Centralized REST API    │
                        │   (PHP 8.x / PDO Engine)  │
                        │      /flinders-api        │
                        └─────────────┬─────────────┘
                                      │
                                      │ SQL Queries (ACID)
                                      │
                        ┌─────────────▼─────────────┐
                        │   MySQL RDBMS Database    │
                        │     (flinders_coffee)     │
                        │   Tables: orders, menus,  │
                        │   expenses, tables, users │
                        └───────────────────────────┘
```

---

## 📂 Struktur Repositori (Repository Structure)

```plaintext
flinders-coffee/
├── README.md                                  # Dokumentasi utama proyek
├── .gitignore                                 # Aturan ignore Git (Android cache, credentials)
│
├── android-app/                               # Proyek Android Studio (Mobile App)
│   ├── app/
│   │   ├── src/main/java/com/flinders/coffee/
│   │   │   ├── data/                         # ApiClient.kt, Repository classes
│   │   │   ├── model/                        # Data classes (MenuItem, TableModel, etc.)
│   │   │   └── ui/                           # Activities, Fragments, ViewHolders
│   │   └── src/main/res/                     # XML layouts, drawables, navigation
│   ├── build.gradle.kts                      # Gradle root build configuration
│   └── gradle/                               # Gradle wrapper binaries
│
├── web-pos/                                   # Antarmuka Web Kasir Laptop (Point of Sale)
│   ├── index.html                            # Tampilan kasir & kasir order board
│   ├── style.css                             # Desain responsive UI
│   ├── app.js                                # Logika keranjang, kalkulasi, REST fetch
│   └── logo.png                              # Brand asset
│
├── backend-api/                               # REST API & Skema Basis Data (XAMPP)
│   ├── api.php                               # Core REST Controller (semua endpoint)
│   ├── database.sql                          # Skema DDL & DML data riil kafe
│   └── API_DOCS.md                           # Rincian kontrak endpoint REST API
│
└── postman/                                   # Pengujian Otomasi API
    └── Flinders_Coffee_API.postman_collection.json # Koleksi Postman siap import
```

---

## 🚀 Fitur Unggulan (Core Features)

### 1. Dashboard Keuangan Riil Owner (Android)
- **Gross Revenue**: Kalkulasi total pendapatan kotor dari pesanan selesai (`status = 'completed'`).
- **HPP (Harga Pokok Penjualan)**: Perhitungan akumulatif dari modal bahan per item menu yang terjual.
- **Gross Margin (Laba Kotor)**: `Gross Margin = Revenue - HPP`.
- **Operating Expenses**: Pencatatan beban biaya harian (gaji, bahan habis pakai, listrik, sewa).
- **Net Profit (Laba Bersih)**: `Net Profit = Gross Margin - Expenses` yang dihitung secara riil dari database.

### 2. Manajemen Meja Interaktif (Floor Plan)
- Status dinamis: `available` (Hijau), `occupied` (Merah), `reserved` (Kuning), `cleaning` (Abu-abu).
- Statistik keterisian meja lantai 1 & 2 secara real-time.
- Pembaruan status meja langsung dari aplikasi mobile ke database.

### 3. Katalog Menu & Analisis Profitabilitas
- Pengaturan status menu: Tersedia (*In Stock*) vs Habis (*Out of Stock*).
- Tambah, edit, dan hapus menu dengan margin keuntungan otomatis (`Margin = Price - HPP`).

### 4. Web POS Kasir
- Pencarian cepat menu berdasarkan kategori (*Coffee, Non-Coffee, Meal & Snack, Frappe*).
- Pilihan nomor meja, catatan pesanan khusus (*Less Sugar, Extra Shot*).
- Kalkulasi otomatis subtotal, pajak, diskon, dan kembalian tunai/QRIS.
- Penyimpanan instan ke tabel `orders` dan `order_items` di MySQL.

---

## 📡 Spesifikasi REST API (Endpoints)

Base URL: `http://localhost/flinders-api/api.php` (atau `http://<IP_KOMPUTER>/flinders-api/api.php`)

| No | Endpoint | HTTP Method | Action Param | Deskripsi |
|:--:|:---------|:-----------:|:-------------|:----------|
| 1 | `/api.php?action=login` | POST | `login` | Autentikasi user (Owner/Kasir/Pelanggan) |
| 2 | `/api.php?action=get_summary` | GET | `get_summary` | Mengambil metrik finansial riil (Sales, HPP, Laba, dll) |
| 3 | `/api.php?action=get_expenses` | GET | `get_expenses` | Mengambil riwayat pengeluaran operasional |
| 4 | `/api.php?action=add_expense` | POST | `add_expense` | Mencatat beban pengeluaran baru |
| 5 | `/api.php?action=delete_expense`| POST | `delete_expense` | Menghapus catatan pengeluaran |
| 6 | `/api.php?action=get_menus` | GET | `get_menus` | Mengambil seluruh katalog menu aktif beserta HPP |
| 7 | `/api.php?action=save_menu` | POST | `save_menu` | Menambah atau mengubah item menu |
| 8 | `/api.php?action=toggle_menu` | POST | `toggle_menu` | Mengubah status ketersediaan menu (0 / 1) |
| 9 | `/api.php?action=get_tables` | GET | `get_tables` | Mengambil denah & status meja restoran |
| 10 | `/api.php?action=update_table` | POST | `update_table`| Memperbarui status meja & pesanan aktif |
| 11 | `/api.php?action=create_order` | POST | `create_order`| Memproses transaksi baru dari kasir |

---

## 🗄️ Skema Basis Data (Database Tables)

Database: `flinders_coffee` (MySQL / MariaDB)

- `users`: Data login, nama lengkap, role (`owner`, `cashier`, `customer`), status aktif.
- `categories`: Kategori menu (*Coffee, Signature, Americano Series, Non Coffee, Frappe, Tea, Meal & Snack*).
- `menus`: Master menu, harga jual, harga pokok (HPP), kategori, dan flag `is_available`.
- `tables`: Master meja (nomor meja, kapasitas kursi, status `available/occupied/reserved/cleaning`).
- `orders`: Master transaksi penjualan (nomor invoice, tanggal, total, metode bayar, status meja).
- `order_items`: Rincian item per transaksi (menu_id, kuantitas, harga satuan, subtotal HPP).
- `expenses`: Catatan beban pengeluaran operasional (kategori, jumlah, tanggal, keterangan).

---

## 🛠️ Panduan Instalasi & Menjalankan Sistem

### 1. Persiapan Database & Backend (XAMPP)
1. Nyalakan modul **Apache** dan **MySQL** di **XAMPP Control Panel**.
2. Buka browser ke `http://localhost/phpmyadmin`.
3. Buat database baru bernama `flinders_coffee`.
4. Import file SQL yang berada di `backend-api/database.sql`.
5. Salin isi folder `backend-api` ke direktori `C:\xampp\htdocs\flinders-api\`.
6. Uji akses via browser: `http://localhost/flinders-api/api.php?action=get_summary`. Jika muncul JSON berisi data finansial, backend sukses berjalan.

### 2. Menjalankan Web POS Kasir
1. Cukup buka file `web-pos/index.html` menggunakan browser Google Chrome / Microsoft Edge.
2. Web POS siap digunakan untuk transaksi kasir.

### 3. Menjalankan Aplikasi Android
1. Buka folder `android-app` menggunakan **Android Studio**.
2. Sesuaikan alamat IP backend pada file:
   `android-app/app/src/main/java/com/flinders/coffee/data/ApiClient.kt`
   - Untuk **Android Emulator**: gunakan `http://10.0.2.2/flinders-api/api.php`
   - Untuk **Device HP Fisik (Wi-Fi)**: gunakan IP lokal laptop (misal: `http://192.168.1.X/flinders-api/api.php`)
3. Jalankan aplikasi via tombol **Run** (`Shift + F10`) ke emulator atau HP fisik Anda.

### 4. Pengujian Menggunakan Postman
1. Buka aplikasi **Postman**.
2. Klik tombol **Import** -> pilih file `postman/Flinders_Coffee_API.postman_collection.json`.
3. Jalankan request seperti **1. Summary Keuangan (Dashboard)** atau **4. Daftar Menu & HPP**.
4. Verifikasi bahwa status menunjukkan **200 OK** dan data JSON tersaji dengan lengkap.

---

## 👨‍💻 Pengembang
- **Nama:** Aaron Todingallo
- **NIM:** 47124011
- **Program Studi:** D3 / D4 Teknik Komputer dan Jaringan
- **Jurusan:** Teknik Elektro
- **Kampus:** Politeknik Negeri Ujung Pandang (PoliUPG)
- **Tahun:** 2026
