# Dokumentasi Lengkap REST API Flinders Coffee (XAMPP MySQL)
**Base URL:**
- Web POS Kasir (Laptop yang sama): `http://localhost/flinders-api/api.php`
- Emulator Android Studio: `http://10.0.2.2/flinders-api/api.php`
- HP Android Fisik (Satu Wi-Fi): `http://192.168.1.75/flinders-api/api.php`

---

## 1. Modul Autentikasi (`users`)

### Login Pengguna
* **URL:** `POST api.php?action=login`
* **Body (JSON):**
  ```json
  {
    "username": "nadia",
    "password": "owner123"
  }
  ```
* **Response Berhasil:**
  ```json
  {
    "status": "success",
    "data": {
      "id": 1,
      "username": "nadia",
      "email": "owner@flinderscoffee.com",
      "full_name": "Nadia Anindya",
      "role": "owner",
      "phone": "081122334455"
    },
    "message": "Login berhasil"
  }
  ```

### Pendaftaran Akun (Register)
* **URL:** `POST api.php?action=register`
* **Body (JSON):**
  ```json
  {
    "username": "userbaru",
    "email": "user@gmail.com",
    "password": "password123",
    "full_name": "Nama Pengguna",
    "role": "kasir",
    "phone": "081234567890"
  }
  ```

---

## 2. Modul Katalog Menu & HPP (`menus`)

### Ambil Semua Menu
* **URL:** `GET api.php?action=get_menus&active=1&category=Semua`
* **Response Berhasil:** Array 49 menu lengkap dengan harga jual dan HPP.

### Tambah / Edit Menu
* **URL:** `POST api.php?action=save_menu`
* **Body (JSON):**
  ```json
  {
    "id": "MN-50",
    "name": "Caramel Macchiato Spesial",
    "category": "Flavoured Coffee",
    "price": 25000,
    "hpp": 9000,
    "isActive": 1,
    "description": "Espresso sirup vanilla karamel legit",
    "effectiveDate": "18 Nov 2026"
  }
  ```

### Toggle Status Aktif/Nonaktif
* **URL:** `POST api.php?action=toggle_menu`
* **Body (JSON):** `{"id": "MN-01"}`

### Hapus Menu
* **URL:** `POST api.php?action=delete_menu`
* **Body (JSON):** `{"id": "MN-01"}`

### Impor Menu Batch (Spreadsheet / CSV)
* **URL:** `POST api.php?action=import_menus`
* **Body (JSON):**
  ```json
  {
    "items": [
      { "id": "MN-01", "name": "Americano", "category": "Coffee", "price": 18000, "hpp": 6500, "isActive": 1 },
      { "id": "MN-02", "name": "Kopi Susu", "category": "Coffee", "price": 18000, "hpp": 6500, "isActive": 1 }
    ]
  }
  ```

---

## 3. Modul Denah & Status Meja (`tables`)

### Ambil Status 18 Meja
* **URL:** `GET api.php?action=get_tables&room=A` (atau tanpa parameter room untuk semua ruang A, B, C)
* **Response:** Status meja (`AVAILABLE`, `OCCUPIED`, `BOOKED`), kapasitas, dan order aktif.

### Update Status Meja
* **URL:** `POST api.php?action=update_table`
* **Body (JSON):**
  ```json
  {
    "id": "A01",
    "status": "OCCUPIED",
    "order_id": "FL-0001",
    "customer": "Bima Bagaskara"
  }
  ```

### Reset Semua Meja (Kosongkan Semua)
* **URL:** `POST api.php?action=reset_tables`

---

## 4. Modul Transaksi & Penjualan Kasir (`orders`)

### Buat Transaksi Baru
* **URL:** `POST api.php?action=create_order`
* **Body (JSON):**
  ```json
  {
    "id": "FL-20261118-0001",
    "customer": "Bima Bagaskara",
    "order_type": "dine",
    "room": "A",
    "table": 1,
    "items": [
      { "n": "Americano", "p": 18000, "hpp": 6500, "q": 2 },
      { "n": "Kentang Goreng", "p": 15000, "hpp": 5500, "q": 1 }
    ],
    "subtotal": 51000,
    "discount": 0,
    "total": 51000,
    "totalHpp": 18500,
    "paymentMethod": "Tunai",
    "cashReceived": 100000,
    "change": 49000,
    "cashier": "Rani Putri",
    "memberCode": "FLN-482910"
  }
  ```
  *(Catatan: Jika `dine` dengan meja, meja bersangkutan otomatis diset `OCCUPIED`. Jika `memberCode` tertera, stamps loyalty otomatis bertambah).*

### Ambil Riwayat Transaksi
* **URL:** `GET api.php?action=get_orders&limit=50&status=Dibayar&date=2026-11-18`

### Update Status Transaksi
* **URL:** `POST api.php?action=update_order_status`
* **Body (JSON):** `{"id": "FL-20261118-0001", "status": "Dibayar"}`

---

## 5. Modul Pengeluaran Operasional (`expenses`)

### Ambil Riwayat Pengeluaran
* **URL:** `GET api.php?action=get_expenses&month=2026-11`

### Tambah Pengeluaran Baru
* **URL:** `POST api.php?action=add_expense`
* **Body (JSON):**
  ```json
  {
    "category": "Kebersihan & perlengkapan",
    "amount": 45000,
    "description": "Beli sabun cuci piring & tisu bar",
    "recorded_by": "Rani Putri",
    "expense_date": "2026-11-18"
  }
  ```

### Hapus Pengeluaran
* **URL:** `POST api.php?action=delete_expense`
* **Body (JSON):** `{"id": 1}`

---

## 6. Modul Reservasi & Booking Meja (`bookings`)

### Ambil Daftar Reservasi
* **URL:** `GET api.php?action=get_bookings&status=Menunggu&date=2026-11-18`

### Buat Reservasi Baru
* **URL:** `POST api.php?action=create_booking`
* **Body (JSON):**
  ```json
  {
    "customer_name": "Rian Ardiansyah",
    "customer_phone": "081299887766",
    "booking_date": "2026-11-18",
    "booking_time": "19:00:00",
    "duration_hours": 2,
    "guest_count": 4,
    "room_code": "B",
    "table_numbers": "01",
    "dp_amount": 50000,
    "notes": "Acara ulang tahun kecil"
  }
  ```

### Update Status Reservasi
* **URL:** `POST api.php?action=update_booking_status`
* **Body (JSON):** `{"id": "BK-20261118-001", "status": "Hadir"}`

---

## 7. Modul Loyalty Member & Stamp (`members`)

### Cari Member
* **URL:** `GET api.php?action=get_members&search=Bima`

### Detail Member & Poin Stamp
* **URL:** `GET api.php?action=get_member&code=FLN-482910`

### Tambah Stamp Kunjungan
* **URL:** `POST api.php?action=add_stamp`
* **Body (JSON):** `{"member_code": "FLN-482910", "count": 2}`

### Tukar Stamp Hadiah
* **URL:** `POST api.php?action=redeem_stamp`
* **Body (JSON):** `{"member_code": "FLN-482910", "count": 10}`

---

## 8. Modul Ringkasan Finansial Realtime (`summary`)

### Ambil Rekapitulasi Laba Bersih & Dashboard
* **URL:** `GET api.php?action=get_summary&period=2026-11` (atau `today=2026-11-18`)
* **Response:**
  ```json
  {
    "status": "success",
    "data": {
      "total_sales": 48600000,
      "total_hpp": 18900000,
      "gross_profit": 29700000,
      "total_expenses": 3200000,
      "net_profit": 26500000,
      "total_orders": 312,
      "table_status": {
        "AVAILABLE": 15,
        "OCCUPIED": 2,
        "BOOKED": 1
      }
    }
  }
  ```

---

## 9. Modul Tutup Kas & Shift Kasir (`shift`)

### Simpan Rekap Tutup Kas
* **URL:** `POST api.php?action=save_closing`
* **Body (JSON):**
  ```json
  {
    "shift_date": "2026-11-18",
    "opened_at": "2026-11-18 08:00:00",
    "closed_at": "2026-11-18 22:00:00",
    "cashier_name": "Rani Putri",
    "opening_cash": 500000,
    "total_cash_sales": 1250000,
    "total_qris_sales": 850000,
    "total_expenses": 120000,
    "expected_cash": 1630000,
    "counted_cash": 1630000,
    "cash_difference": 0,
    "notes": "Shift lancar, kas seimbang"
  }
  ```
