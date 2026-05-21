package Pemrograman2;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHelper {

    // ── Konfigurasi koneksi ──────────────────────────────────────
    private static final String URL  = "jdbc:mysql://localhost:3306/manajemen_produk"
                                     + "?useSSL=false&serverTimezone=Asia/Jakarta"
                                     + "&allowPublicKeyRetrieval=true";
    private static final String USER = "root";   // ganti sesuai user MySQL Anda
    private static final String PASS = "";       // ganti sesuai password MySQL Anda
    // ─────────────────────────────────────────────────────────────

    private Connection conn;

    /** Buka koneksi */
    public void connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[DB] Koneksi berhasil ke database manajemen_produk");
        }
    }

    /** Tutup koneksi */
    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  TAMBAH PRODUK  →  INSERT langsung (lebih stabil dari CALL)
    // ─────────────────────────────────────────────────────────────
    public int tambahProduk(Produk p) throws SQLException {
        // Validasi sisi Java sebelum ke DB
        if (p.getHarga() < 0) throw new SQLException("Harga tidak boleh negatif!");
        if (p.getStok()  < 0) throw new SQLException("Stok tidak boleh negatif!");

        String sql = "INSERT INTO produk (nama, kategori, harga, stok) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getKategori());
            ps.setDouble(3, p.getHarga());
            ps.setInt   (4, p.getStok());
            ps.executeUpdate();

            // Catat log
            logAktivitas("TAMBAH",
                "Produk ditambahkan: " + p.getNama()
                + " | Kategori: " + p.getKategori()
                + " | Harga: " + p.getHarga()
                + " | Stok: " + p.getStok(),
                p.getNama());

            // Ambil ID yang baru di-generate
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ─────────────────────────────────────────────────────────────
    //  HAPUS TERATAS  →  simulasi POP stack (hapus id terbesar)
    // ─────────────────────────────────────────────────────────────
    public String hapusTeratas() throws SQLException {
        // Ambil produk teratas dulu
        String selectSql = "SELECT id, nama FROM produk ORDER BY id DESC LIMIT 1";
        String namaDihapus = null;
        int idDihapus = -1;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(selectSql)) {
            if (!rs.next()) throw new SQLException("Stack kosong! Tidak ada produk untuk dihapus.");
            idDihapus  = rs.getInt("id");
            namaDihapus = rs.getString("nama");
        }

        // Hapus
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM produk WHERE id = ?")) {
            ps.setInt(1, idDihapus);
            ps.executeUpdate();
        }

        logAktivitas("HAPUS_TERATAS", "Produk teratas dihapus: " + namaDihapus, namaDihapus);
        return namaDihapus;
    }

    // ─────────────────────────────────────────────────────────────
    //  HAPUS BY NAMA
    // ─────────────────────────────────────────────────────────────
    public void hapusByNama(String nama) throws SQLException {
        // Cek apakah ada dan stok-nya
        String selectSql = "SELECT id, stok FROM produk WHERE LOWER(nama) = LOWER(?) LIMIT 1";
        int idTarget = -1;
        int stokTarget = -1;

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    throw new SQLException("Produk '" + nama + "' tidak ditemukan dalam sistem.");
                idTarget   = rs.getInt("id");
                stokTarget = rs.getInt("stok");
            }
        }

        if (stokTarget == 0)
            throw new SQLException("Stok produk '" + nama + "' sudah kosong! Tidak dapat dihapus.");

        // Hapus
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM produk WHERE id = ?")) {
            ps.setInt(1, idTarget);
            ps.executeUpdate();
        }

        logAktivitas("HAPUS_NAMA", "Produk dihapus berdasarkan nama: " + nama, nama);
    }

    // ─────────────────────────────────────────────────────────────
    //  SEARCH  →  LIKE case-insensitive
    // ─────────────────────────────────────────────────────────────
    public ArrayList<Produk> searchByNama(String keyword) throws SQLException {
        String sql = "SELECT id, nama, kategori, harga, stok FROM produk "
                   + "WHERE LOWER(nama) LIKE LOWER(?) ORDER BY id";
        ArrayList<Produk> hasil = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) hasil.add(mapRow(rs));
            }
        }
        logAktivitas("SEARCH", "Pencarian keyword='" + keyword + "', ditemukan: " + hasil.size(), null);
        return hasil;
    }

    // ─────────────────────────────────────────────────────────────
    //  GET SEMUA PRODUK  (urutan insert)
    // ─────────────────────────────────────────────────────────────
    public ArrayList<Produk> getAllProduk() throws SQLException {
        return queryView("SELECT * FROM v_semua_produk");
    }

    // ─────────────────────────────────────────────────────────────
    //  SORT BY HARGA
    // ─────────────────────────────────────────────────────────────
    public ArrayList<Produk> sortByHarga() throws SQLException {
        return queryView("SELECT * FROM v_produk_sort_harga");
    }

    // ─────────────────────────────────────────────────────────────
    //  SORT BY KATEGORI
    // ─────────────────────────────────────────────────────────────
    public ArrayList<Produk> sortByKategori() throws SQLException {
        return queryView("SELECT * FROM v_produk_sort_kategori");
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPER: query view / SELECT biasa
    // ─────────────────────────────────────────────────────────────
    private ArrayList<Produk> queryView(String sql) throws SQLException {
        ArrayList<Produk> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPER: catat log ke tabel log_aktivitas
    // ─────────────────────────────────────────────────────────────
    private void logAktivitas(String aksi, String keterangan, String namaProduk) {
        String sql = "INSERT INTO log_aktivitas (aksi, keterangan, nama_produk) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aksi);
            ps.setString(2, keterangan);
            ps.setString(3, namaProduk);   // boleh null
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LOG ERROR] " + e.getMessage());
        }
    }

    private Produk mapRow(ResultSet rs) throws SQLException {
        return new Produk(
            rs.getString("nama"),
            rs.getString("kategori"),
            rs.getDouble("harga"),
            rs.getInt("stok")
        );
    }

    // ─────────────────────────────────────────────────────────────
    //  TEST KONEKSI (opsional)
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        DatabaseHelper db = new DatabaseHelper();
        try {
            db.connect();
            System.out.println("Semua produk:");
            for (Produk p : db.getAllProduk()) {
                System.out.println("  " + p);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] " + e.getMessage());
        } finally {
            db.disconnect();
        }
    }
}