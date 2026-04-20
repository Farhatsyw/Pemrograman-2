package Pert7;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.view.*;

public class CetakLaporan extends JFrame {

    Connection conn;
    Statement st;
    ResultSet rs;
    DefaultTableModel model;

    public CetakLaporan() {
        initComponents();
        koneksi();
        tampilData();
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
    public void tampilData() {
        try {
            model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM nilai");

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("matkul"),
                    rs.getInt("nil1"),
                    rs.getInt("nil2"),
                    rs.getDouble("rata")
                };
                model.addRow(row);
            }

            lblHasil.setText("Total data: " + model.getRowCount() + " mahasiswa");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil: " + e.getMessage());
        }
    }

    // ===================== SIMPAN DATA =====================
    public void simpanData() {
        try {
            String nim    = txtNim.getText().trim();
            String nama   = txtNama.getText().trim();
            String matkul = txtMatkul.getText().trim();
            String sNil1  = txtNil1.getText().trim();
            String sNil2  = txtNil2.getText().trim();

            if (nim.isEmpty() || nama.isEmpty() || matkul.isEmpty()
                    || sNil1.isEmpty() || sNil2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            int nil1    = Integer.parseInt(sNil1);
            int nil2    = Integer.parseInt(sNil2);
            double rata = (nil1 + nil2) / 2.0;

            // Cek apakah NIM sudah ada
            PreparedStatement cek = conn.prepareStatement(
                "SELECT nim FROM nilai WHERE nim=?");
            cek.setString(1, nim);
            ResultSet rsCek = cek.executeQuery();

            if (rsCek.next()) {
                // UPDATE
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE nilai SET nama=?, matkul=?, nil1=?, nil2=?, rata=? WHERE nim=?");
                ps.setString(1, nama);
                ps.setString(2, matkul);
                ps.setInt(3, nil1);
                ps.setInt(4, nil2);
                ps.setDouble(5, rata);
                ps.setString(6, nim);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            } else {
                // INSERT
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO nilai (nim, nama, matkul, nil1, nil2, rata) VALUES (?,?,?,?,?,?)");
                ps.setString(1, nim);
                ps.setString(2, nama);
                ps.setString(3, matkul);
                ps.setInt(4, nil1);
                ps.setInt(5, nil2);
                ps.setDouble(6, rata);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            }

            tampilData();
            bersihForm();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nilai 1 dan Nilai 2 harus berupa angka!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    // ===================== UPDATE DATA =====================
    public void updateData() {
        try {
            String nim    = txtNim.getText().trim();
            String nama   = txtNama.getText().trim();
            String matkul = txtMatkul.getText().trim();
            String sNil1  = txtNil1.getText().trim();
            String sNil2  = txtNil2.getText().trim();

            if (nim.isEmpty() || nama.isEmpty() || matkul.isEmpty()
                    || sNil1.isEmpty() || sNil2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            int nil1    = Integer.parseInt(sNil1);
            int nil2    = Integer.parseInt(sNil2);
            double rata = (nil1 + nil2) / 2.0;

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE nilai SET nama=?, matkul=?, nil1=?, nil2=?, rata=? WHERE nim=?");
            ps.setString(1, nama);
            ps.setString(2, matkul);
            ps.setInt(3, nil1);
            ps.setInt(4, nil2);
            ps.setDouble(5, rata);
            ps.setString(6, nim);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                tampilData();
                bersihForm();
            } else {
                JOptionPane.showMessageDialog(this, "NIM tidak ditemukan!");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nilai 1 dan Nilai 2 harus berupa angka!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal update: " + e.getMessage());
        }
    }

    // ===================== HAPUS DATA =====================
    public void hapusData() {
        String nim = txtNim.getText().trim();
        if (nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data dari tabel terlebih dahulu!");
            return;
        }
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Hapus data NIM: " + nim + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM nilai WHERE nim=?");
                ps.setString(1, nim);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                tampilData();
                bersihForm();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
            }
        }
    }

    // ===================== BERSIH FORM =====================
    public void bersihForm() {
        txtNim.setText("");
        txtNama.setText("");
        txtMatkul.setText("");
        txtNil1.setText("");
        txtNil2.setText("");
        lblRata.setText("0.0");
        txtNim.requestFocus();
    }

    // ===================== CETAK LAPORAN =====================
    public void cetakLaporan() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("Pert7.jasper");

            if (is == null) {
                JOptionPane.showMessageDialog(this, "File Pert7.jasper tidak ditemukan!");
                return;
            }

            java.util.Map<String, Object> params = new java.util.HashMap<>();
            JasperPrint print = JasperFillManager.fillReport(is, params, conn);

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Laporan Nilai Mahasiswa");
            viewer.setVisible(true);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, "Gagal cetak: " + e.getMessage());
        }
    }

    // ===================== INIT COMPONENTS =====================
    private void initComponents() {

        // Label & Field
        jLabel1   = new JLabel("NIM");
        jLabel2   = new JLabel("Nama");
        jLabel3   = new JLabel("Mata Kuliah");
        jLabel4   = new JLabel("Nilai 1");
        jLabel5   = new JLabel("Nilai 2");
        jLabel6   = new JLabel("Rata-rata");
        lblHasil  = new JLabel("Total data: 0 mahasiswa");
        lblRata   = new JLabel("0.0");

        txtNim    = new JTextField();
        txtNama   = new JTextField();
        txtMatkul = new JTextField();
        txtNil1   = new JTextField();
        txtNil2   = new JTextField();

        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus  = new JButton("Hapus");
        btnBersih = new JButton("Bersih");
        btnCetak  = new JButton("Cetak");

        jTable1 = new JTable();
        jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NIM", "NAMA", "MATA KULIAH", "NIL1", "NIL2", "RATA-RATA"}
        ));

        JScrollPane scrollPane = new JScrollPane(jTable1);

        // ---- Layout ----
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Row 0 - NIM
        c.gridx = 0; c.gridy = 0; add(jLabel1, c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0; add(txtNim, c);
        c.weightx = 0;

        // Row 1 - Nama
        c.gridx = 0; c.gridy = 1; add(jLabel2, c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1.0; add(txtNama, c);
        c.weightx = 0;

        // Row 2 - Mata Kuliah
        c.gridx = 0; c.gridy = 2; add(jLabel3, c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1.0; add(txtMatkul, c);
        c.weightx = 0;

        // Row 3 - Nilai 1
        c.gridx = 0; c.gridy = 3; add(jLabel4, c);
        c.gridx = 1; c.gridy = 3; c.weightx = 1.0; add(txtNil1, c);
        c.weightx = 0;

        // Row 4 - Nilai 2
        c.gridx = 0; c.gridy = 4; add(jLabel5, c);
        c.gridx = 1; c.gridy = 4; c.weightx = 1.0; add(txtNil2, c);
        c.weightx = 0;

        // Row 5 - Rata-rata
        c.gridx = 0; c.gridy = 5; add(jLabel6, c);
        c.gridx = 1; c.gridy = 5; add(lblRata, c);

        // Row 6 - Tombol
        JPanel panelBtn = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelBtn.add(btnSimpan);
        panelBtn.add(btnUpdate);
        panelBtn.add(btnHapus);
        panelBtn.add(btnBersih);
        panelBtn.add(btnCetak);
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2; add(panelBtn, c);
        c.gridwidth = 1;

        // Row 7 - Label hasil
        c.gridx = 0; c.gridy = 7; c.gridwidth = 2; add(lblHasil, c);
        c.gridwidth = 1;

        // Row 8 - Tabel
        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 1.0;
        add(scrollPane, c);

        // ---- Action Listeners ----
        btnSimpan.addActionListener(e -> simpanData());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e  -> hapusData());
        btnBersih.addActionListener(e -> bersihForm());
        btnCetak.addActionListener(e  -> cetakLaporan());

        // Auto hitung rata-rata saat ketik
        txtNil1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { hitungRata(); }
        });
        txtNil2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { hitungRata(); }
        });

        // Klik baris tabel → isi form
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = jTable1.getSelectedRow();
                if (row >= 0) {
                    txtNim.setText(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtMatkul.setText(model.getValueAt(row, 2).toString());
                    txtNil1.setText(model.getValueAt(row, 3).toString());
                    txtNil2.setText(model.getValueAt(row, 4).toString());
                    lblRata.setText(model.getValueAt(row, 5).toString());
                }
            }
        });

        setTitle("Form Nilai Mahasiswa");
        setSize(600, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ===================== HITUNG RATA-RATA =====================
    private void hitungRata() {
        try {
            double n1 = Double.parseDouble(txtNil1.getText().trim());
            double n2 = Double.parseDouble(txtNil2.getText().trim());
            lblRata.setText(String.format("%.1f", (n1 + n2) / 2));
        } catch (NumberFormatException ex) {
            lblRata.setText("0.0");
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CetakLaporan().setVisible(true));
    }

    // Variables
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private JLabel lblHasil, lblRata;
    private JTextField txtNim, txtNama, txtMatkul, txtNil1, txtNil2;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBersih, btnCetak;
    private JTable jTable1;
}