-- ========================================================
-- DATABASE FLINDERS COFFEE UNTUK XAMPP (MYSQL) - VERSI FINAL / FIX
-- Import via phpMyAdmin (http://localhost/phpmyadmin) atau via CLI
-- ========================================================

CREATE DATABASE IF NOT EXISTS `flinders_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `flinders_db`;

DROP TABLE IF EXISTS `shift_closings`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `expenses`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `tables`;
DROP TABLE IF EXISTS `menus`;
DROP TABLE IF EXISTS `members`;
DROP TABLE IF EXISTS `users`;

-- 1. TABEL PENGGUNA SISTEM (USERS / AUTENTIKASI)
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) DEFAULT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `role` ENUM('owner', 'kasir', 'barista', 'member') NOT NULL DEFAULT 'kasir',
  `phone` VARCHAR(25) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data Pengguna Bawaan
INSERT INTO `users` (`username`, `email`, `password`, `full_name`, `role`, `phone`) VALUES
('nadia', 'owner@flinderscoffee.com', 'owner123', 'Nadia Anindya', 'owner', '081122334455'),
('rani', 'kasir@flinderscoffee.com', 'kasir123', 'Rani Putri', 'kasir', '081234567800'),
('barista', 'barista@flinderscoffee.com', 'barista123', 'Andi Barista', 'barista', '081234567899');

-- 2. TABEL KATALOG MENU LENGKAP & HPP (49 MENU OTENTIK FLINDERS COFFEE)
CREATE TABLE `menus` (
  `id` VARCHAR(50) NOT NULL PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50) NOT NULL DEFAULT 'Coffee',
  `price` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `hpp` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `description` TEXT DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `effective_date` VARCHAR(100) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed 49 Menu Fisik Otentik Flinders Coffee
INSERT INTO `menus` (`id`, `name`, `category`, `price`, `hpp`, `is_active`, `description`, `effective_date`) VALUES
-- 1. Coffee
('MN-01', 'Americano', 'Coffee', 18000.00, 6500.00, 1, 'Espresso klasik dengan air mineral segar (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-02', 'Kopi Susu', 'Coffee', 18000.00, 6500.00, 1, 'Espresso double shot dengan creamy milk & aren murni (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-03', 'Flat White', 'Coffee', 20000.00, 7500.00, 1, 'Espresso velvety dengan microfoam steamed milk (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-04', 'Cafe Latte', 'Coffee', 20000.00, 7500.00, 1, 'Espresso lembut berpadu steamed milk halus berbusa (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-05', 'Cappucino', 'Coffee', 20000.00, 7500.00, 1, 'Espresso kaya rasa dengan foam tebal gurih berpadu (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),

-- 2. Flavoured Coffee
('MN-06', 'Butterscotch Latte', 'Flavoured Coffee', 22000.00, 8000.00, 1, 'Espresso berpadu sirup butterscotch gurih karamel (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-07', 'Vanilla Latte', 'Flavoured Coffee', 22000.00, 8000.00, 1, 'Espresso harum ekstrak vanila manis lembut (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-08', 'Hazelnut Latte', 'Flavoured Coffee', 22000.00, 8000.00, 1, 'Espresso beraroma hazelnut panggang gurih (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-09', 'Pandan Latte', 'Flavoured Coffee', 22000.00, 8000.00, 1, 'Espresso dengan aroma wangi pandan alami (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-10', 'Chococino', 'Flavoured Coffee', 22000.00, 8500.00, 1, 'Paduan kopi moka espresso dan cokelat nikmat (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-11', 'Caramel Macchiato', 'Flavoured Coffee', 22000.00, 8500.00, 1, 'Espresso, susu, sirup vanilla & saus karamel lezat (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),

-- 3. Signature
('MN-12', 'Kopi Nera (Gula Aren)', 'Signature', 20000.00, 7500.00, 1, '❤️❤️ Signature kopi aren khas Flinders racikan spesial (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-13', 'Creamy Butterscotch', 'Signature', 23000.00, 9000.00, 1, 'Minuman khas butterscotch creamy kaya tekstur dan manis pas', '18 Nov 2026'),
('MN-14', 'Butterscotch Seasalt', 'Signature', 23000.00, 9000.00, 1, 'Perpaduan manis gurih butterscotch dengan sentuhan seasalt seimbang', '18 Nov 2026'),
('MN-15', 'Mont Blanc', 'Signature', 23000.00, 9000.00, 1, 'Minuman dingin signature lembut dengan rasa dessert pegunungan salju', '18 Nov 2026'),
('MN-16', 'Choco Hazelnut', 'Signature', 22000.00, 8500.00, 1, 'Cokelat pekat berpadu kacang hazelnut gurih nikmat', '18 Nov 2026'),
('MN-17', 'Choco Berry', 'Signature', 22000.00, 8500.00, 1, 'Kombinasi kaya rasa cokelat lumer dan kesegaran rasa berry', '18 Nov 2026'),
('MN-18', 'Creamy Lotus', 'Signature', 22000.00, 8500.00, 1, 'Minuman creamy biskuit Lotus Biscoff legit renyah', '18 Nov 2026'),
('MN-19', 'Crunchy Caramel', 'Signature', 23000.00, 9000.00, 1, '❤️ Karamel manis legit dengan sensasi renyah kriuk istimewa', '18 Nov 2026'),

-- 4. Americano Series
('MN-20', 'Blueberry Americano', 'Americano Series', 20000.00, 7000.00, 1, '❤️ Sensasi Americano segar berpadu buah blueberry manis-asam', '18 Nov 2026'),
('MN-21', 'Peach Americano', 'Americano Series', 20000.00, 7000.00, 1, 'Americano dingin menyegarkan dengan aroma dan rasa buah peach', '18 Nov 2026'),
('MN-22', 'Passion Fruit Americano', 'Americano Series', 20000.00, 7000.00, 1, 'Americano tropis dengan kesegaran markisa alami', '18 Nov 2026'),

-- 5. Non Coffee
('MN-23', 'Matcha Latte', 'Non Coffee', 22000.00, 8500.00, 1, 'Matcha murni Uji Jepang dengan susu segar lembut (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-24', 'Chocolate', 'Non Coffee', 20000.00, 7500.00, 1, '❤️❤️ Cokelat Belgia pekat berpadu fresh milk (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-25', 'Cookies & Cream', 'Non Coffee', 20000.00, 7500.00, 1, 'Minuman susu manis lembut dengan remahan biskuit cokelat gurih', '18 Nov 2026'),
('MN-26', 'Choco Nutella', 'Non Coffee', 20000.00, 8000.00, 1, 'Cokelat susu dengan selai hazelnut cokelat Nutella asli', '18 Nov 2026'),
('MN-27', 'Red Velvet', 'Non Coffee', 20000.00, 7500.00, 1, 'Minuman red velvet lembut beraroma kue manis legit (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-28', 'Vanilla', 'Non Coffee', 20000.00, 7000.00, 1, 'Minuman susu vanila klasik lembut yang menenangkan (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-29', 'Fruity Milkshake', 'Non Coffee', 20000.00, 7500.00, 1, 'Milkshake buah segar lembut dan creamy berbusa halus', '18 Nov 2026'),
('MN-30', 'Fruity Yogurt', 'Non Coffee', 18000.00, 6500.00, 1, '❤️ Yogurt segar pilihan: Blueberry, Strawberry, Lychee, Mango, Passion Fruit', '18 Nov 2026'),

-- 6. Frappe
('MN-31', 'Chocolate Frappe', 'Frappe', 23000.00, 9000.00, 1, 'Blended es cokelat kental dan creamy bertabur garnish', '18 Nov 2026'),
('MN-32', 'Red Velvet Frappe', 'Frappe', 23000.00, 9000.00, 1, 'Frappe red velvet dingin pekat bertabur topping lembut', '18 Nov 2026'),
('MN-33', 'Matcha Frappe', 'Frappe', 23000.00, 9000.00, 1, 'Blended matcha Jepang dingin kaya rasa susu segar', '18 Nov 2026'),
('MN-34', 'Vanilla Frappe', 'Frappe', 23000.00, 9000.00, 1, 'Frappe vanila manis dingin segar dengan tekstur halus', '18 Nov 2026'),
('MN-35', 'Flinderscino', 'Frappe', 23000.00, 9000.00, 1, 'Signature ice-blended khas Flinders Coffee kaya cita rasa', '18 Nov 2026'),

-- 7. Tea
('MN-36', 'Original Tea', 'Tea', 15000.00, 4500.00, 1, 'Seduhan teh murni berkualitas harum dan menyegarkan (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-37', 'Milk Tea', 'Tea', 18000.00, 6000.00, 1, 'Teh aromatik dipadukan dengan susu kental gurih (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-38', 'Thai Tea', 'Tea', 18000.00, 6000.00, 1, 'Teh Thailand otentik dengan aroma rempah manis creamy (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-39', 'Green Tea', 'Tea', 18000.00, 6000.00, 1, 'Teh hijau segar aromatik kaya antioksidan (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-40', 'Lemon Tea', 'Tea', 18000.00, 6000.00, 1, 'Teh segar berpadu sari jeruk lemon asli penghilang dahaga (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),
('MN-41', 'Lychee Tea', 'Tea', 18000.00, 6500.00, 1, 'Teh melati harum disajikan dengan buah leci segar (Dingin 🔵 / Panas 🔴)', '18 Nov 2026'),

-- 8. Meal & Snack
('MN-42', 'Nasi Ayam', 'Meal & Snack', 25000.00, 12000.00, 1, 'Nasi putih hangat dengan olahan ayam bumbu rempah gurih lezat', '18 Nov 2026'),
('MN-43', 'Beef Burger Ori', 'Meal & Snack', 25000.00, 12000.00, 1, 'Burger roti panggang empuk isi patty daging sapi gurih & sayuran segar', '18 Nov 2026'),
('MN-44', 'Mix Platter', 'Meal & Snack', 20000.00, 9500.00, 1, 'Paket camilan lengkap aneka gorengan gurih renyah favorit kumpul', '18 Nov 2026'),
('MN-45', 'Mie Goreng / Rebus', 'Meal & Snack', 15000.00, 6000.00, 1, 'Mie lezat pilihan (Goreng / Rebus) disajikan hangat menggoda', '18 Nov 2026'),
('MN-46', 'Kentang Goreng', 'Meal & Snack', 15000.00, 5500.00, 1, 'Kentang goreng renyah bumbu gurih lezat renyah di luar lembut di dalam', '18 Nov 2026'),
('MN-47', 'Pisang Goreng Ori', 'Meal & Snack', 15000.00, 5500.00, 1, 'Pisang goreng renyah manis alami cocok dinikmati bersama kopi', '18 Nov 2026'),
('MN-48', 'Bakpao Mini', 'Meal & Snack', 10000.00, 3500.00, 1, 'Kukusan bakpao mini empuk lembut beraneka isian manis lezat', '18 Nov 2026'),
('MN-49', 'Roti Bakar', 'Meal & Snack', 10000.00, 3500.00, 1, 'Roti bakar mentega pilihan: Coklat, Keju, Blueberry, Strawberry', '18 Nov 2026');

-- 3. TABEL DENAH MEJA KAFE (FLOOR PLAN - 18 MEJA RUANG A, B, C)
CREATE TABLE `tables` (
  `id` VARCHAR(20) NOT NULL PRIMARY KEY,
  `room_code` VARCHAR(10) NOT NULL,
  `room_name` VARCHAR(50) NOT NULL,
  `table_number` VARCHAR(10) NOT NULL,
  `capacity` INT NOT NULL DEFAULT 4,
  `location_note` VARCHAR(100) DEFAULT NULL,
  `status` ENUM('AVAILABLE', 'OCCUPIED', 'BOOKED') NOT NULL DEFAULT 'AVAILABLE',
  `current_order_id` VARCHAR(50) DEFAULT NULL,
  `customer_name` VARCHAR(100) DEFAULT NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data 18 Meja Flinders Coffee
INSERT INTO `tables` (`id`, `room_code`, `room_name`, `table_number`, `capacity`, `location_note`, `status`) VALUES
('A01', 'A', 'Ruang A', '01', 2, 'Outdoor depan kasir · 2 orang', 'AVAILABLE'),
('A02', 'A', 'Ruang A', '02', 4, 'Outdoor depan kasir · 4 orang', 'AVAILABLE'),
('A03', 'A', 'Ruang A', '03', 4, 'Outdoor smoking area · 4 orang', 'AVAILABLE'),
('A04', 'A', 'Ruang A', '04', 4, 'Outdoor sudut depan kasir · 4 orang', 'AVAILABLE'),
('A05', 'A', 'Ruang A', '05', 2, 'Outdoor samping kasir · 2 orang', 'AVAILABLE'),
('A06', 'A', 'Ruang A', '06', 4, 'Outdoor samping taman · 4 orang', 'AVAILABLE'),
('B01', 'B', 'Ruang B', '01', 4, 'Indoor tengah sofa · 4 orang', 'AVAILABLE'),
('B02', 'B', 'Ruang B', '02', 4, 'Indoor tengah dekat bar · 4 orang', 'AVAILABLE'),
('B03', 'B', 'Ruang B', '03', 2, 'Indoor tengah meja bundar · 2 orang', 'AVAILABLE'),
('B04', 'B', 'Ruang B', '04', 4, 'Indoor tengah AC dingin · 4 orang', 'AVAILABLE'),
('B05', 'B', 'Ruang B', '05', 6, 'Indoor tengah meja panjang · 6 orang', 'AVAILABLE'),
('B06', 'B', 'Ruang B', '06', 4, 'Indoor tengah sudut colokan · 4 orang', 'AVAILABLE'),
('C01', 'C', 'Ruang C', '01', 2, 'Belakang dekat taman · 2 orang', 'AVAILABLE'),
('C02', 'C', 'Ruang C', '02', 4, 'Belakang semi-outdoor · 4 orang', 'AVAILABLE'),
('C03', 'C', 'Ruang C', '03', 2, 'Belakang lorong WC · 2 orang', 'AVAILABLE'),
('C04', 'C', 'Ruang C', '04', 4, 'Belakang sudut tenang · 4 orang', 'AVAILABLE'),
('C05', 'C', 'Ruang C', '05', 4, 'Belakang dekat kolam · 4 orang', 'AVAILABLE'),
('C06', 'C', 'Ruang C', '06', 4, 'Belakang area santai · 4 orang', 'AVAILABLE');

-- 4. TABEL TRANSAKSI PENJUALAN KASIR (ORDERS)
CREATE TABLE `orders` (
  `id` VARCHAR(50) NOT NULL PRIMARY KEY,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `customer_name` VARCHAR(100) DEFAULT 'Pelanggan Umum',
  `order_type` VARCHAR(20) DEFAULT 'dine',
  `room_code` VARCHAR(10) DEFAULT NULL,
  `table_number` INT DEFAULT NULL,
  `table_location` VARCHAR(50) DEFAULT NULL,
  `items_json` LONGTEXT NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `discount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_hpp` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `payment_method` VARCHAR(30) NOT NULL DEFAULT 'Tunai',
  `cash_received` DECIMAL(12,2) DEFAULT 0.00,
  `cash_change` DECIMAL(12,2) DEFAULT 0.00,
  `payment_status` VARCHAR(20) NOT NULL DEFAULT 'Dibayar',
  `cashier_name` VARCHAR(50) DEFAULT 'Rani Putri',
  `member_code` VARCHAR(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TABEL PENGELUARAN OPERASIONAL (EXPENSES)
CREATE TABLE `expenses` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expense_date` DATE NOT NULL DEFAULT (CURRENT_DATE),
  `category` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `recorded_by` VARCHAR(50) DEFAULT 'Rani Putri',
  `receipt_img` VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. TABEL BOOKING & RESERVASI MEJA (BOOKINGS)
CREATE TABLE `bookings` (
  `id` VARCHAR(50) NOT NULL PRIMARY KEY,
  `customer_name` VARCHAR(100) NOT NULL,
  `customer_phone` VARCHAR(25) NOT NULL,
  `booking_date` DATE NOT NULL,
  `booking_time` TIME NOT NULL,
  `duration_hours` INT NOT NULL DEFAULT 2,
  `guest_count` INT NOT NULL DEFAULT 2,
  `room_code` VARCHAR(10) NOT NULL,
  `table_numbers` VARCHAR(50) NOT NULL,
  `dp_amount` DECIMAL(12,2) DEFAULT 0.00,
  `status` ENUM('Menunggu', 'Hadir', 'Selesai', 'Batal') NOT NULL DEFAULT 'Menunggu',
  `notes` TEXT DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. TABEL MEMBER & LOYALTY STAMP (MEMBERS)
CREATE TABLE `members` (
  `member_code` VARCHAR(20) NOT NULL PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `stamps_count` INT NOT NULL DEFAULT 0,
  `total_visits` INT NOT NULL DEFAULT 0,
  `last_stamp_date` DATE DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data Member
INSERT INTO `members` (`member_code`, `name`, `phone`, `email`, `stamps_count`, `total_visits`) VALUES
('FLN-482910', 'Bima Bagaskara', '081234567801', 'bima@gmail.com', 3, 3),
('FLN-482911', 'Aris Munandar', '081234567802', 'aris@gmail.com', 9, 10),
('FLN-482912', 'Dina Salsabila', '081234567803', 'dina@gmail.com', 4, 4),
('FLN-482913', 'Rina Puspita', '081234567804', 'rina@gmail.com', 6, 6);

-- 8. TABEL TUTUP KAS & SHIFT (SHIFT_CLOSINGS)
CREATE TABLE `shift_closings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `shift_date` DATE NOT NULL,
  `opened_at` DATETIME NOT NULL,
  `closed_at` DATETIME NOT NULL,
  `cashier_name` VARCHAR(50) NOT NULL,
  `opening_cash` DECIMAL(12,2) NOT NULL DEFAULT 500000.00,
  `total_cash_sales` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_qris_sales` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_expenses` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `expected_cash` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `counted_cash` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `cash_difference` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `notes` TEXT DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
