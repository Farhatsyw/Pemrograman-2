package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {
    private String username, password, nama;
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNama() { return nama; }
    public String getPesan() { return pesan; }

    public boolean cekLogin(String usernameInput, String passwordMD5) {
        boolean berhasil = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT username, nama FROM tbadmin WHERE username=? AND password=?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, usernameInput);
                ps.setString(2, passwordMD5);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    berhasil = true;
                    this.username = rs.getString("username");
                    this.nama = rs.getString("nama");
                }
                rs.close(); ps.close(); connection.close();
            } catch (SQLException ex) {
                pesan = "Error: " + ex.getMessage();
            }
        } else {
            pesan = "Koneksi gagal: " + koneksi.getPesanKesalahan();
        }
        return berhasil;
    }
}
