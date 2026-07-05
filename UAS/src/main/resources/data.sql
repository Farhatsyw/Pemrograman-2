-- Data awal kamar hotel (INSERT IGNORE agar tidak duplikat saat aplikasi di-restart)
INSERT IGNORE INTO kamar (id, nomor_kamar, tipe_kamar, harga_per_malam, status) VALUES
(1, '101', 'STANDARD', 350000, 'TERSEDIA'),
(2, '102', 'STANDARD', 350000, 'TERSEDIA'),
(3, '103', 'STANDARD', 350000, 'TERISI'),
(4, '201', 'DELUXE',   550000, 'TERSEDIA'),
(5, '202', 'DELUXE',   550000, 'TERSEDIA'),
(6, '203', 'DELUXE',   550000, 'TERISI'),
(7, '301', 'SUITE',    950000, 'TERSEDIA'),
(8, '302', 'SUITE',    950000, 'TERSEDIA');

-- Data reservasi contoh, disesuaikan dengan kamar yang berstatus TERISI di atas (103 & 203)
INSERT IGNORE INTO reservasi (id, nama_tamu, kamar_id, tanggal_checkin, tanggal_checkout, status) VALUES
(1, 'Ahmad Fauzi', 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AKTIF'),
(2, 'Siti Nurhaliza', 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AKTIF');