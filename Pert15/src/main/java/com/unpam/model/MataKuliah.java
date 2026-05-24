package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MataKuliah {
    private String kodeMataKuliah, namaMataKuliah;
    private int jumlahSks;
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getKodeMataKuliah() { return kodeMataKuliah; }
    public void setKodeMataKuliah(String kodeMataKuliah) { this.kodeMataKuliah = kodeMataKuliah; }
    public String getNamaMataKuliah() { return namaMataKuliah; }
    public void setNamaMataKuliah(String namaMataKuliah) { this.namaMataKuliah = namaMataKuliah; }
    public int getJumlahSks() { return jumlahSks; }
    public void setJumlahSks(int jumlahSks) { this.jumlahSks = jumlahSks; }
    public String getPesan() { return pesan; }

    public List<MataKuliah> getList() {
        List<MataKuliah> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM tbmatakuliah ORDER BY kodeMataKuliah";
                Statement sta = connection.createStatement();
                ResultSet rs = sta.executeQuery(sql);
                while (rs.next()) {
                    MataKuliah mk = new MataKuliah();
                    mk.kodeMataKuliah = rs.getString("kodeMataKuliah");
                    mk.namaMataKuliah = rs.getString("namaMataKuliah");
                    mk.jumlahSks = rs.getInt("jumlahSks");
                    list.add(mk);
                }
                rs.close(); sta.close(); connection.close();
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
                String sql = "INSERT INTO tbmatakuliah VALUES(?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, kodeMataKuliah);
                ps.setString(2, namaMataKuliah);
                ps.setInt(3, jumlahSks);
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan mata kuliah"; }
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
