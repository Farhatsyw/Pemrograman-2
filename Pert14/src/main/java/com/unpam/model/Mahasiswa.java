package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mahasiswa {
    private String nim, nama, kelas, password;
    private int semester;
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPesan() { return pesan; }

    public List<Mahasiswa> getList() {
        List<Mahasiswa> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT nim, nama, semester, kelas FROM tbmahasiswa ORDER BY nim";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Mahasiswa m = new Mahasiswa();
                    m.nim = rs.getString("nim");
                    m.nama = rs.getString("nama");
                    m.semester = rs.getInt("semester");
                    m.kelas = rs.getString("kelas");
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
                String sql = "INSERT INTO tbmahasiswa(nim,nama,semester,kelas,password) VALUES(?,?,?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, nim);
                ps.setString(2, nama);
                ps.setInt(3, semester);
                ps.setString(4, kelas);
                ps.setString(5, password);
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan data mahasiswa"; }
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
