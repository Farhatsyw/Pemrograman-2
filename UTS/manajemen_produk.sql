CREATE DATABASE IF NOT EXISTS manajemen_produk
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE manajemen_produk;

CREATE TABLE IF NOT EXISTS produk (
    id          INT             AUTO_INCREMENT PRIMARY KEY,
    nama        VARCHAR(150)    NOT NULL,
    kategori    VARCHAR(100)    NOT NULL,
    harga       DECIMAL(15, 2)  NOT NULL CHECK (harga >= 0),
    stok        INT             NOT NULL DEFAULT 0 CHECK (stok >= 0),
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS log_aktivitas (
    id          INT             AUTO_INCREMENT PRIMARY KEY,
    aksi        ENUM('TAMBAH', 'HAPUS_TERATAS', 'HAPUS_NAMA', 'SEARCH', 'SORT') NOT NULL,
    keterangan  TEXT            NOT NULL,
    nama_produk VARCHAR(150),
    waktu       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO produk (nama, kategori, harga, stok) VALUES
    ('Laptop Asus VivoBook',            'Elektronik',  8500000.00,  5),
    ('Mouse Wireless Logitech',         'Elektronik',   185000.00, 20),
    ('Buku Pemrograman Java',           'Buku',          95000.00, 15),
    ('Keyboard Mechanical',             'Elektronik',   650000.00,  8),
    ('Tas Ransel Laptop',               'Aksesoris',    275000.00, 12),
    ('Flash Drive 64GB',                'Elektronik',   120000.00, 30),
    ('Buku Algoritma & Struktur Data',  'Buku',         110000.00, 10);

DELIMITER $$

CREATE PROCEDURE sp_tambah_produk (
    IN p_nama       VARCHAR(150),
    IN p_kategori   VARCHAR(100),
    IN p_harga      DECIMAL(15,2),
    IN p_stok       INT
)
BEGIN
    -- Validasi harga & stok
    IF p_harga < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Harga tidak boleh negatif!';
    END IF;

    IF p_stok < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Stok tidak boleh negatif!';
    END IF;

    INSERT INTO produk (nama, kategori, harga, stok)
    VALUES (p_nama, p_kategori, p_harga, p_stok);

    INSERT INTO log_aktivitas (aksi, keterangan, nama_produk)
    VALUES ('TAMBAH',
            CONCAT('Produk ditambahkan: ', p_nama,
                   ' | Kategori: ', p_kategori,
                   ' | Harga: ', p_harga,
                   ' | Stok: ', p_stok),
            p_nama);

    SELECT LAST_INSERT_ID() AS id_baru;
END$$

CREATE PROCEDURE sp_hapus_teratas ()
BEGIN
    DECLARE v_id        INT;
    DECLARE v_nama      VARCHAR(150);

    SELECT id, nama
    INTO   v_id, v_nama
    FROM   produk
    ORDER  BY id DESC
    LIMIT  1;

    IF v_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Stack kosong! Tidak ada produk untuk dihapus.';
    END IF;

    DELETE FROM produk WHERE id = v_id;

    INSERT INTO log_aktivitas (aksi, keterangan, nama_produk)
    VALUES ('HAPUS_TERATAS',
            CONCAT('Produk teratas dihapus: ', v_nama),
            v_nama);

    SELECT v_nama AS produk_dihapus;
END$$

CREATE PROCEDURE sp_hapus_by_nama (
    IN p_nama VARCHAR(150)
)
BEGIN
    DECLARE v_id    INT;
    DECLARE v_stok  INT;

    SELECT id, stok
    INTO   v_id, v_stok
    FROM   produk
    WHERE  LOWER(nama) = LOWER(p_nama)
    LIMIT  1;

    IF v_id IS NULL THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Produk tidak ditemukan dalam sistem.';
    END IF;

    IF v_stok = 0 THEN
        SIGNAL SQLSTATE '45002'
            SET MESSAGE_TEXT = 'Stok produk sudah kosong! Tidak dapat dihapus.';
    END IF;

    DELETE FROM produk WHERE id = v_id;

    INSERT INTO log_aktivitas (aksi, keterangan, nama_produk)
    VALUES ('HAPUS_NAMA',
            CONCAT('Produk dihapus berdasarkan nama: ', p_nama),
            p_nama);
END$$

CREATE PROCEDURE sp_search_produk (
    IN p_keyword VARCHAR(150)
)
BEGIN
    INSERT INTO log_aktivitas (aksi, keterangan)
    VALUES ('SEARCH',
            CONCAT('Pencarian keyword=''', p_keyword, ''''));

    SELECT id, nama, kategori, harga, stok
    FROM   produk
    WHERE  LOWER(nama) LIKE CONCAT('%', LOWER(p_keyword), '%')
    ORDER  BY id;
END$$

DELIMITER ;

CREATE OR REPLACE VIEW v_produk_sort_harga AS
    SELECT id, nama, kategori, harga, stok
    FROM   produk
    ORDER  BY harga ASC;

CREATE OR REPLACE VIEW v_produk_sort_kategori AS
    SELECT id, nama, kategori, harga, stok
    FROM   produk
    ORDER  BY kategori ASC, nama ASC;

CREATE OR REPLACE VIEW v_semua_produk AS
    SELECT id, nama, kategori, harga, stok, created_at
    FROM   produk
    ORDER  BY id ASC;

SELECT '=== Isi Tabel Produk ===' AS info;
SELECT * FROM v_semua_produk;

SELECT '=== Sort by Harga ===' AS info;
SELECT * FROM v_produk_sort_harga;

SELECT '=== Sort by Kategori ===' AS info;
SELECT * FROM v_produk_sort_kategori;
