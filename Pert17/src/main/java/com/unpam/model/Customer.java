package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String idCustomer, nama, alamat, noTelp, noKtp;
    private String pesan;
    private final Koneksi koneksi = new Koneksi();

    public String getIdCustomer() { return idCustomer; }
    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
    public String getNoKtp() { return noKtp; }
    public void setNoKtp(String noKtp) { this.noKtp = noKtp; }
    public String getPesan() { return pesan; }

    public List<Customer> getList() {
        List<Customer> list = new ArrayList<>();
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                String sql = "SELECT * FROM tbcustomer ORDER BY idCustomer";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Customer c = new Customer();
                    c.idCustomer = rs.getString("idCustomer");
                    c.nama = rs.getString("nama");
                    c.alamat = rs.getString("alamat");
                    c.noTelp = rs.getString("noTelp");
                    c.noKtp = rs.getString("noKtp");
                    list.add(c);
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
                String sql = "INSERT INTO tbcustomer(idCustomer, nama, alamat, noTelp, noKtp) VALUES(?,?,?,?,?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, idCustomer);
                ps.setString(2, nama);
                ps.setString(3, alamat);
                ps.setString(4, noTelp);
                ps.setString(5, noKtp);
                int result = ps.executeUpdate();
                if (result < 1) { adaKesalahan = true; pesan = "Gagal menyimpan data customer"; }
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
