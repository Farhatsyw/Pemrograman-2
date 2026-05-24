package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Nilai {
    private int id;
    private String nim, kodeMataKuliah, namaMahasiswa, namaMataKuliah, kelas;
    private int semester, jumlahSks;
    private double tugas, uts, uas, nilaiAkhir;
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    public String getKodeMataKuliah() { return kodeMataKuliah; }
    public void setKodeMataKuliah(String kodeMataKuliah) { this.kodeMataKuliah = kodeMataKuliah; }
    public double getTugas() { return tugas; }
    public void setTugas(double tugas) { this.tugas = tugas; }
    public double getUts() { return uts; }
    public void setUts(double uts) { this.uts = uts; }
    public double getUas() { return uas; }
    public void setUas(double uas) { this.uas = uas; }
    public double getNilaiAkhir() { return nilaiAkhir; }
    public String getNamaMahasiswa() { return namaMahasiswa; }
    public String getNamaMataKuliah() { return namaMataKuliah; }
    public String getKelas() { return kelas; }
    public int getSemester() { return semester; }
    public int getJumlahSks() { return jumlahSks; }
    public int getId() { return id; }
    public String getPesan() { return pesan; }

    public String getHuruf() {
        if (nilaiAkhir >= 80) return "A";
        else if (nilaiAkhir >= 70) return "B";
        else if (nilaiAkhir >= 60) return "C";
        else if (nilaiAkhir >= 50) return "D";
        else return "E";
    }

    public String getStatus() {
        return nilaiAkhir >= 60 ? "Lulus" : "Tidak Lulus";
    }

    public List<Nilai> getList() {
        List<Nilai> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT n.id, n.nim, m.nama AS namaMahasiswa, m.semester, m.kelas, "
                        + "n.kodeMataKuliah, mk.namaMataKuliah, mk.jumlahSks, "
                        + "n.tugas, n.uts, n.uas, "
                        + "ROUND((0.2*n.tugas + 0.3*n.uts + 0.4*n.uas + 0.1*mk.jumlahSks*10), 2) AS nilaiAkhir "
                        + "FROM tbnilai n "
                        + "JOIN tbmahasiswa m ON n.nim = m.nim "
                        + "JOIN tbmatakuliah mk ON n.kodeMataKuliah = mk.kodeMataKuliah "
                        + "ORDER BY m.semester, m.kelas, m.nim";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Nilai n = new Nilai();
                    n.id = rs.getInt("id");
                    n.nim = rs.getString("nim");
                    n.namaMahasiswa = rs.getString("namaMahasiswa");
                    n.semester = rs.getInt("semester");
                    n.kelas = rs.getString("kelas");
                    n.kodeMataKuliah = rs.getString("kodeMataKuliah");
                    n.namaMataKuliah = rs.getString("namaMataKuliah");
                    n.jumlahSks = rs.getInt("jumlahSks");
                    n.tugas = rs.getDouble("tugas");
                    n.uts = rs.getDouble("uts");
                    n.uas = rs.getDouble("uas");
                    n.nilaiAkhir = rs.getDouble("nilaiAkhir");
                    list.add(n);
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
                String sql = "INSERT INTO tbnilai(nim, kodeMataKuliah, tugas, uts, uas) VALUES(?,?,?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, nim);
                ps.setString(2, kodeMataKuliah);
                ps.setDouble(3, tugas);
                ps.setDouble(4, uts);
                ps.setDouble(5, uas);
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan nilai"; }
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

    public Connection getConnection() {
        return koneksi.getConnection();
    }

    public ResultSet getDataLaporan() {
        Connection connection = koneksi.getConnection();
        ResultSet rs = null;
        if (connection != null) {
            try {
                String sql = "SELECT n.nim, m.nama AS namaMahasiswa, m.semester, m.kelas, "
                        + "n.kodeMataKuliah, mk.namaMataKuliah, mk.jumlahSks, "
                        + "n.tugas, n.uts, n.uas, "
                        + "ROUND((0.2*n.tugas + 0.3*n.uts + 0.4*n.uas + 0.1*mk.jumlahSks*10), 2) AS nilaiAkhir "
                        + "FROM tbnilai n "
                        + "JOIN tbmahasiswa m ON n.nim = m.nim "
                        + "JOIN tbmatakuliah mk ON n.kodeMataKuliah = mk.kodeMataKuliah "
                        + "ORDER BY m.semester, m.kelas, m.nim";
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
