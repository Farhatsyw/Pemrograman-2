package com.unpam.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String database = "jdbc:mysql://localhost:3306/pemrograman2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String user = "root";
    private static final String password = "";

    private Connection connection;
    private String pesanKesalahan;

    public String getPesanKesalahan() { return pesanKesalahan; }

    public Connection getConnection() {
        connection = null;
        pesanKesalahan = "";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            pesanKesalahan = "JDBC Driver tidak ditemukan\n" + ex;
        }
        if (pesanKesalahan.equals("")) {
            try {
                connection = DriverManager.getConnection(database, user, password);
            } catch (SQLException ex) {
                pesanKesalahan = "Koneksi gagal\n" + ex;
            }
        }
        return connection;
    }
}
