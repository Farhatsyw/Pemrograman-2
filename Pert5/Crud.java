package Pert5;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Crud extends JFrame {

    Connection conn;
    Statement st;
    ResultSet rs;
    DefaultTableModel model;

    public Crud() {
        initComponents();
        koneksi();
        tampilData();
    }

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

    public void tampilData() {
        try {
            model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // kosongkan dulu

            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM datamhs");

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("semester"),
                    rs.getString("kelas")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new JLabel("NIM");
        jLabel2 = new JLabel("Nama");
        jLabel3 = new JLabel("Semester");
        jLabel4 = new JLabel("Kelas");

        jTextField1 = new JTextField();
        jTextField2 = new JTextField();
        jTextField3 = new JTextField();
        jTextField4 = new JTextField();

        btnSimpan = new JButton("Simpan");
        btnHapus  = new JButton("Hapus");
        btnUpdate = new JButton("Update");
        btnBersih = new JButton("Bersih");

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

        // Row 0 - NIM
        c.gridx = 0; c.gridy = 0; add(jLabel1, c);
        c.gridx = 1; c.gridy = 0; c.gridwidth = 2; add(jTextField1, c);
        c.gridwidth = 1;

        // Row 1 - Nama
        c.gridx = 0; c.gridy = 1; add(jLabel2, c);
        c.gridx = 1; c.gridy = 1; c.gridwidth = 2; add(jTextField2, c);
        c.gridwidth = 1;

        // Row 2 - Semester
        c.gridx = 0; c.gridy = 2; add(jLabel3, c);
        c.gridx = 1; c.gridy = 2; c.gridwidth = 2; add(jTextField3, c);
        c.gridwidth = 1;

        // Row 3 - Kelas
        c.gridx = 0; c.gridy = 3; add(jLabel4, c);
        c.gridx = 1; c.gridy = 3; c.gridwidth = 2; add(jTextField4, c);
        c.gridwidth = 1;

        // Row 4 - Tombol
        c.gridx = 0; c.gridy = 4; add(btnSimpan, c);
        c.gridx = 1; c.gridy = 4; add(btnHapus, c);
        c.gridx = 2; c.gridy = 4; add(btnUpdate, c);
        c.gridx = 3; c.gridy = 4; add(btnBersih, c);

        // Row 5 - Tabel
        c.gridx = 0; c.gridy = 5; c.gridwidth = 4;
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 1.0;
        add(scrollPane, c);

        // ---- Action Listeners ----
        btnSimpan.addActionListener(e -> simpanData());
        btnHapus.addActionListener(e -> hapusData());
        btnUpdate.addActionListener(e -> updateData());
        btnBersih.addActionListener(e -> bersihField());

        // Klik baris tabel → isi field
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = jTable1.getSelectedRow();
                jTextField1.setText(model.getValueAt(row, 0).toString());
                jTextField2.setText(model.getValueAt(row, 1).toString());
                jTextField3.setText(model.getValueAt(row, 2).toString());
                jTextField4.setText(model.getValueAt(row, 3).toString());
            }
        });

        setTitle("Data Mahasiswa");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ===================== SIMPAN =====================
    private void simpanData() {
        try {
            String nim      = jTextField1.getText().trim();
            String nama     = jTextField2.getText().trim();
            String semester = jTextField3.getText().trim();
            String kelas    = jTextField4.getText().trim();

            if (nim.isEmpty() || nama.isEmpty() || semester.isEmpty() || kelas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            String sql = "INSERT INTO datamhs VALUES ('" + nim + "','" + nama + "'," + semester + ",'" + kelas + "')";
            st = conn.createStatement();
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            bersihField();
            tampilData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    // ===================== HAPUS =====================
    private void hapusData() {
        try {
            String nim = jTextField1.getText().trim();
            if (nim.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
                return;
            }
            int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?");
            if (konfirmasi == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM datamhs WHERE nim='" + nim + "'";
                st = conn.createStatement();
                st.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                bersihField();
                tampilData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
        }
    }

    // ===================== UPDATE =====================
    private void updateData() {
        try {
            String nim      = jTextField1.getText().trim();
            String nama     = jTextField2.getText().trim();
            String semester = jTextField3.getText().trim();
            String kelas    = jTextField4.getText().trim();

            if (nim.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diupdate!");
                return;
            }

            String sql = "UPDATE datamhs SET nama='" + nama + "', semester=" + semester + ", kelas='" + kelas + "' WHERE nim='" + nim + "'";
            st = conn.createStatement();
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            bersihField();
            tampilData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update: " + e.getMessage());
        }
    }

    // ===================== BERSIH FIELD =====================
    private void bersihField() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField1.requestFocus();
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Crud().setVisible(true));
    }

    // Variables
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4;
    private JTextField jTextField1, jTextField2, jTextField3, jTextField4;
    private JButton btnSimpan, btnHapus, btnUpdate, btnBersih;
    private JTable jTable1;
}