package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mobil {
    private String platNomor, merk, tipe, warna;
    private int tahun;
    private double hargaSewa; // per hari
    private String status; // tersedia / disewa
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public String getMerk() { return merk; }
    public void setMerk(String merk) { this.merk = merk; }
    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public String getWarna() { return warna; }
    public void setWarna(String warna) { this.warna = warna; }
    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }
    public double getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(double hargaSewa) { this.hargaSewa = hargaSewa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPesan() { return pesan; }

    public List<Mobil> getList() {
        List<Mobil> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM tbmobil ORDER BY platNomor";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Mobil m = new Mobil();
                    m.platNomor = rs.getString("platNomor");
                    m.merk = rs.getString("merk");
                    m.tipe = rs.getString("tipe");
                    m.warna = rs.getString("warna");
                    m.tahun = rs.getInt("tahun");
                    m.hargaSewa = rs.getDouble("hargaSewa");
                    m.status = rs.getString("status");
                    list.add(m);
                }
                rs.close(); ps.close(); connection.close();
            } catch (SQLException ex) {
                pesan = "Error: " + ex.getMessage();
            }
        }
        return list;
    }

    public boolean simpan() {
        boolean adaKesalahan = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "INSERT INTO tbmobil(platNomor, merk, tipe, warna, tahun, hargaSewa, status) VALUES(?,?,?,?,?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, platNomor);
                ps.setString(2, merk);
                ps.setString(3, tipe);
                ps.setString(4, warna);
                ps.setInt(5, tahun);
                ps.setDouble(6, hargaSewa);
                ps.setString(7, "tersedia");
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan data mobil"; }
                ps.close(); connection.close();
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Error: " + ex.getMessage();
            }
        } else {
            adaKesalahan = true;
            pesan = "Koneksi gagal: " + koneksi.getPesanKesalahan();
        }
        return !adaKesalahan;
    }
}
