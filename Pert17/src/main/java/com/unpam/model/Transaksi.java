package com.unpam.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int idTransaksi;
    private String idCustomer, platNomor, namaCustomer, merkMobil, tipe;
    private Date tanggalSewa, tanggalKembali;
    private int lamaSewa; // hari
    private double hargaSewa, totalBayar;
    private String status; // aktif / selesai
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public int getIdTransaksi() { return idTransaksi; }
    public String getIdCustomer() { return idCustomer; }
    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }
    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public String getNamaCustomer() { return namaCustomer; }
    public String getMerkMobil() { return merkMobil; }
    public String getTipe() { return tipe; }
    public Date getTanggalSewa() { return tanggalSewa; }
    public void setTanggalSewa(Date tanggalSewa) { this.tanggalSewa = tanggalSewa; }
    public Date getTanggalKembali() { return tanggalKembali; }
    public void setTanggalKembali(Date tanggalKembali) { this.tanggalKembali = tanggalKembali; }
    public int getLamaSewa() { return lamaSewa; }
    public void setLamaSewa(int lamaSewa) { this.lamaSewa = lamaSewa; }
    public double getHargaSewa() { return hargaSewa; }
    public double getTotalBayar() { return totalBayar; }
    public String getStatus() { return status; }
    public String getPesan() { return pesan; }

    public List<Transaksi> getList() {
        List<Transaksi> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT t.idTransaksi, t.idCustomer, c.nama AS namaCustomer, "
                        + "t.platNomor, m.merk AS merkMobil, m.tipe, "
                        + "t.tanggalSewa, t.tanggalKembali, t.lamaSewa, "
                        + "m.hargaSewa, (t.lamaSewa * m.hargaSewa) AS totalBayar, t.status "
                        + "FROM tbtransaksi t "
                        + "JOIN tbcustomer c ON t.idCustomer = c.idCustomer "
                        + "JOIN tbmobil m ON t.platNomor = m.platNomor "
                        + "ORDER BY t.idTransaksi DESC";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.idTransaksi = rs.getInt("idTransaksi");
                    t.idCustomer = rs.getString("idCustomer");
                    t.namaCustomer = rs.getString("namaCustomer");
                    t.platNomor = rs.getString("platNomor");
                    t.merkMobil = rs.getString("merkMobil");
                    t.tipe = rs.getString("tipe");
                    t.tanggalSewa = rs.getDate("tanggalSewa");
                    t.tanggalKembali = rs.getDate("tanggalKembali");
                    t.lamaSewa = rs.getInt("lamaSewa");
                    t.hargaSewa = rs.getDouble("hargaSewa");
                    t.totalBayar = rs.getDouble("totalBayar");
                    t.status = rs.getString("status");
                    list.add(t);
                }
                rs.close(); ps.close(); connection.close();
            } catch (SQLException ex) {
                pesan = "Error: " + ex.getMessage();
            }
        }
        return list;
    }

    public boolean simpanSewa() {
        boolean adaKesalahan = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false);
                // Insert transaksi
                String sql = "INSERT INTO tbtransaksi(idCustomer, platNomor, tanggalSewa, tanggalKembali, lamaSewa, status) VALUES(?,?,?,?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, idCustomer);
                ps.setString(2, platNomor);
                ps.setDate(3, tanggalSewa);
                ps.setDate(4, tanggalKembali);
                ps.setInt(5, lamaSewa);
                ps.setString(6, "aktif");
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan transaksi sewa"; }
                ps.close();

                // Update status mobil
                if (!adaKesalahan) {
                    String sqlUpdate = "UPDATE tbmobil SET status='disewa' WHERE platNomor=?";
                    PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate);
                    psUpdate.setString(1, platNomor);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                }

                if (!adaKesalahan) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Error: " + ex.getMessage();
                try { connection.rollback(); connection.close(); } catch (Exception e) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Koneksi gagal: " + koneksi.getPesanKesalahan();
        }
        return !adaKesalahan;
    }

    public boolean prosesKembali(int idTransaksiKembali) {
        boolean adaKesalahan = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                connection.setAutoCommit(false);
                // Cek transaksi
                String sqlCek = "SELECT platNomor FROM tbtransaksi WHERE idTransaksi=? AND status='aktif'";
                PreparedStatement psCek = connection.prepareStatement(sqlCek);
                psCek.setInt(1, idTransaksiKembali);
                ResultSet rs = psCek.executeQuery();
                String plat = null;
                if (rs.next()) { plat = rs.getString("platNomor"); }
                rs.close(); psCek.close();

                if (plat == null) {
                    adaKesalahan = true;
                    pesan = "Transaksi tidak ditemukan atau sudah selesai";
                } else {
                    // Update status transaksi
                    String sqlTr = "UPDATE tbtransaksi SET status='selesai' WHERE idTransaksi=?";
                    PreparedStatement psTr = connection.prepareStatement(sqlTr);
                    psTr.setInt(1, idTransaksiKembali);
                    psTr.executeUpdate();
                    psTr.close();

                    // Update status mobil
                    String sqlMobil = "UPDATE tbmobil SET status='tersedia' WHERE platNomor=?";
                    PreparedStatement psMobil = connection.prepareStatement(sqlMobil);
                    psMobil.setString(1, plat);
                    psMobil.executeUpdate();
                    psMobil.close();
                }

                if (!adaKesalahan) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Error: " + ex.getMessage();
                try { connection.rollback(); connection.close(); } catch (Exception e) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Koneksi gagal: " + koneksi.getPesanKesalahan();
        }
        return !adaKesalahan;
    }

    public Connection getConnection() {
        return koneksi.getConnection();
    }

    public ResultSet getDataLaporan() {
        Connection connection = koneksi.getConnection();
        ResultSet rs = null;
        if (connection != null) {
            try {
                String sql = "SELECT t.idTransaksi, t.idCustomer, c.nama AS namaCustomer, c.noKtp, "
                        + "t.platNomor, m.merk AS merkMobil, m.tipe, m.warna, "
                        + "t.tanggalSewa, t.tanggalKembali, t.lamaSewa, "
                        + "m.hargaSewa, (t.lamaSewa * m.hargaSewa) AS totalBayar, t.status "
                        + "FROM tbtransaksi t "
                        + "JOIN tbcustomer c ON t.idCustomer = c.idCustomer "
                        + "JOIN tbmobil m ON t.platNomor = m.platNomor "
                        + "ORDER BY t.idTransaksi DESC";
                PreparedStatement ps = connection.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                pesan = "Error: " + ex.getMessage();
            }
        }
        return rs;
    }
}
