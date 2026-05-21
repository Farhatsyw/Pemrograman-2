package Pert6;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SearchForm extends JFrame {

    Connection conn;
    Statement st;
    ResultSet rs;
    DefaultTableModel model;

    public SearchForm() {
        initComponents();
        koneksi();
        tampilData("");
    }

    // ===================== KONEKSI =====================
    public void koneksi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pemrograman2",
                "root",
                ""
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

    // ===================== TAMPIL DATA =====================
    public void tampilData(String keyword) {
        try {
            model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            String sql;
            if (keyword.isEmpty()) {
                // Tampil semua data
                sql = "SELECT * FROM datamhs";
            } else {
                // Filter berdasarkan keyword
                sql = "SELECT * FROM datamhs WHERE " +
                      "nim LIKE '%" + keyword + "%' OR " +
                      "nama LIKE '%" + keyword + "%' OR " +
                      "semester LIKE '%" + keyword + "%' OR " +
                      "kelas LIKE '%" + keyword + "%'";
            }

            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("semester"),
                    rs.getString("kelas")
                };
                model.addRow(row);
            }

            // Tampilkan jumlah hasil
            lblHasil.setText("Ditemukan: " + model.getRowCount() + " data");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil: " + e.getMessage());
        }
    }

    // ===================== INIT COMPONENTS =====================
    private void initComponents() {

        jLabel1  = new JLabel("Search:");
        txtSearch = new JTextField();
        btnSearch = new JButton("Cari");
        btnReset  = new JButton("Reset");
        lblHasil  = new JLabel("Ditemukan: 0 data");

        // Dropdown pilihan kolom pencarian
        String[] kategori = {"Semua", "NIM", "Nama", "Semester", "Kelas"};
        cmbKategori = new JComboBox<>(kategori);

        jTable1 = new JTable();
        jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NIM", "Nama", "Semester", "Kelas"}
        ));

        JScrollPane scrollPane = new JScrollPane(jTable1);

        // ---- Layout ----
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Row 0 - Search bar
        c.gridx = 0; c.gridy = 0; add(jLabel1, c);
        c.gridx = 1; c.gridy = 0; add(cmbKategori, c);
        c.gridx = 2; c.gridy = 0; c.weightx = 1.0; add(txtSearch, c);
        c.weightx = 0;
        c.gridx = 3; c.gridy = 0; add(btnSearch, c);
        c.gridx = 4; c.gridy = 0; add(btnReset, c);

        // Row 1 - Label hasil
        c.gridx = 0; c.gridy = 1; c.gridwidth = 5; add(lblHasil, c);
        c.gridwidth = 1;

        // Row 2 - Tabel
        c.gridx = 0; c.gridy = 2; c.gridwidth = 5;
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 1.0;
        add(scrollPane, c);

        // ---- Action Listeners ----

        // Tombol Cari
        btnSearch.addActionListener(e -> cariData());

        // Tombol Reset
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbKategori.setSelectedIndex(0);
            tampilData("");
        });

        // Search realtime saat ketik
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                cariData();
            }
        });

        setTitle("Search Data Mahasiswa");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ===================== CARI DATA =====================
    private void cariData() {
        try {
            String keyword  = txtSearch.getText().trim();
            String kategori = cmbKategori.getSelectedItem().toString();

            model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            String sql;

            if (keyword.isEmpty()) {
                sql = "SELECT * FROM datamhs";
            } else if (kategori.equals("Semua")) {
                sql = "SELECT * FROM datamhs WHERE " +
                      "nim LIKE '%" + keyword + "%' OR " +
                      "nama LIKE '%" + keyword + "%' OR " +
                      "semester LIKE '%" + keyword + "%' OR " +
                      "kelas LIKE '%" + keyword + "%'";
            } else {
                // Cari berdasarkan kolom yang dipilih
                String kolom = kategori.toLowerCase();
                sql = "SELECT * FROM datamhs WHERE " + kolom + " LIKE '%" + keyword + "%'";
            }

            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("semester"),
                    rs.getString("kelas")
                };
                model.addRow(row);
            }

            lblHasil.setText("Ditemukan: " + model.getRowCount() + " data");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cari: " + e.getMessage());
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchForm().setVisible(true));
    }

    // Variables
    private JLabel jLabel1, lblHasil;
    private JTextField txtSearch;
    private JButton btnSearch, btnReset;
    private JComboBox<String> cmbKategori;
    private JTable jTable1;
}