-- ============================================
-- Script Database: rentcar
-- Aplikasi Penyewaan Mobil - Pertemuan 17
-- Universitas Pamulang
-- ============================================

CREATE DATABASE IF NOT EXISTS rentcar;
USE rentcar;

-- Tabel Admin
CREATE TABLE IF NOT EXISTS tbadmin (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,  -- MD5 hash
    nama VARCHAR(100) NOT NULL
);

-- Tabel Mobil
CREATE TABLE IF NOT EXISTS tbmobil (
    platNomor VARCHAR(15) PRIMARY KEY,
    merk VARCHAR(50) NOT NULL,
    tipe VARCHAR(50) NOT NULL,
    warna VARCHAR(30) NOT NULL,
    tahun INT NOT NULL,
    hargaSewa DOUBLE NOT NULL,       -- harga per hari
    status VARCHAR(20) DEFAULT 'tersedia'  -- tersedia / disewa
);

-- Tabel Customer
CREATE TABLE IF NOT EXISTS tbcustomer (
    idCustomer VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat TEXT,
    noTelp VARCHAR(20),
    noKtp VARCHAR(20)
);

-- Tabel Transaksi Penyewaan
CREATE TABLE IF NOT EXISTS tbtransaksi (
    idTransaksi INT AUTO_INCREMENT PRIMARY KEY,
    idCustomer VARCHAR(20) NOT NULL,
    platNomor VARCHAR(15) NOT NULL,
    tanggalSewa DATE NOT NULL,
    tanggalKembali DATE NOT NULL,
    lamaSewa INT NOT NULL,           -- dalam hari
    status VARCHAR(20) DEFAULT 'aktif',  -- aktif / selesai
    FOREIGN KEY (idCustomer) REFERENCES tbcustomer(idCustomer),
    FOREIGN KEY (platNomor) REFERENCES tbmobil(platNomor)
);

-- ============================================
-- Data Sample
-- ============================================

-- Admin (password: admin123 -> MD5: 0192023a7bbd73250516f069df18b500)
INSERT INTO tbadmin VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'Administrator');

-- Data Mobil
INSERT INTO tbmobil VALUES ('B 1234 ABC', 'Toyota', 'Avanza', 'Putih', 2022, 350000, 'tersedia');
INSERT INTO tbmobil VALUES ('B 5678 DEF', 'Honda', 'Brio', 'Silver', 2021, 280000, 'tersedia');
INSERT INTO tbmobil VALUES ('B 9012 GHI', 'Suzuki', 'Ertiga', 'Hitam', 2023, 400000, 'tersedia');
INSERT INTO tbmobil VALUES ('D 1111 JKL', 'Daihatsu', 'Xenia', 'Merah', 2020, 300000, 'tersedia');
INSERT INTO tbmobil VALUES ('D 2222 MNO', 'Mitsubishi', 'Pajero Sport', 'Putih', 2022, 800000, 'tersedia');

-- Data Customer
INSERT INTO tbcustomer VALUES ('C001', 'Budi Santoso', 'Jl. Merdeka No. 10, Jakarta', '081234567890', '3174010101010001');
INSERT INTO tbcustomer VALUES ('C002', 'Siti Rahayu', 'Jl. Sudirman No. 5, Tangerang', '082345678901', '3603020202020002');
INSERT INTO tbcustomer VALUES ('C003', 'Ahmad Fauzi', 'Jl. Gatot Subroto No. 3, Bekasi', '083456789012', '3275030303030003');
