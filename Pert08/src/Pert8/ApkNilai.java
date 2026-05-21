package Pert8;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ApkNilai extends JFrame {

    Connection conn;
    Statement st;
    ResultSet rs;
    DefaultTableModel model;

    // ===================== KONSTRUKTOR =====================
    public ApkNilai() {
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

            String sql = "SELECT * FROM datanilai";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("mata_kuliah"),
                    rs.getString("nilai_uas"),
                    rs.getString("nilai_uts"),
                    rs.getString("nilai_tugas"),
                    rs.getString("nilai_akhir"),
                    rs.getString("grade")
                };
                model.addRow(row);
            }

            lblStatus.setText("Total data: " + model.getRowCount());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil: " + e.getMessage());
        }
    }

    // ===================== SIMPAN / UPDATE DATA =====================
    public void simpanData() {
        String nim        = txtNIM.getText().trim();
        String nama       = txtNama.getText().trim();
        String mk         = txtMK.getText().trim();
        String nilaiUAS   = txtUAS.getText().trim();
        String nilaiUTS   = txtUTS.getText().trim();
        String nilaiTugas = txtTugas.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || mk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM, Nama, dan Mata Kuliah wajib diisi!");
            return;
        }

        try {
            double uas   = Double.parseDouble(nilaiUAS);
            double uts   = Double.parseDouble(nilaiUTS);
            double tugas = Double.parseDouble(nilaiTugas);

            // Hitung nilai akhir: UAS 40% + UTS 30% + Tugas 30%
            double akhir = (uas * 0.4) + (uts * 0.3) + (tugas * 0.3);
            String grade = hitungGrade(akhir);

            String sql;
            if (isEdit) {
                // Mode update
                sql = "UPDATE datanilai SET nama='" + nama + "', mata_kuliah='" + mk + "', " +
                      "nilai_uas=" + uas + ", nilai_uts=" + uts + ", nilai_tugas=" + tugas + ", " +
                      "nilai_akhir=" + akhir + ", grade='" + grade + "' " +
                      "WHERE nim='" + nim + "'";
                conn.createStatement().executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            } else {
                // Mode insert
                sql = "INSERT INTO datanilai (nim, nama, mata_kuliah, nilai_uas, nilai_uts, nilai_tugas, nilai_akhir, grade) " +
                      "VALUES ('" + nim + "','" + nama + "','" + mk + "'," + uas + "," + uts + "," + tugas + "," + akhir + ",'" + grade + "')";
                conn.createStatement().executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            }

            resetForm();
            tampilData();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nilai harus berupa angka!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    // ===================== HAPUS DATA =====================
    public void hapusData() {
        String nim = txtNIM.getText().trim();
        if (nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data dari tabel terlebih dahulu!");
            return;
        }

        int konfirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM datanilai WHERE nim='" + nim + "'";
                conn.createStatement().executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                resetForm();
                tampilData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
            }
        }
    }

    // ===================== CARI DATA =====================
    public void cariData() {
        try {
            String keyword  = txtCari.getText().trim();
            String kategori = cmbKategori.getSelectedItem().toString();

            model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            String sql;
            if (keyword.isEmpty()) {
                sql = "SELECT * FROM datanilai";
            } else if (kategori.equals("Semua")) {
                sql = "SELECT * FROM datanilai WHERE " +
                      "nim LIKE '%" + keyword + "%' OR " +
                      "nama LIKE '%" + keyword + "%' OR " +
                      "mata_kuliah LIKE '%" + keyword + "%' OR " +
                      "grade LIKE '%" + keyword + "%'";
            } else {
                String kolom = kategori.toLowerCase().replace(" ", "_");
                sql = "SELECT * FROM datanilai WHERE " + kolom + " LIKE '%" + keyword + "%'";
            }

            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("mata_kuliah"),
                    rs.getString("nilai_uas"),
                    rs.getString("nilai_uts"),
                    rs.getString("nilai_tugas"),
                    rs.getString("nilai_akhir"),
                    rs.getString("grade")
                };
                model.addRow(row);
            }

            lblStatus.setText("Ditemukan: " + model.getRowCount() + " data");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cari: " + e.getMessage());
        }
    }

    // ===================== HITUNG GRADE =====================
    public String hitungGrade(double nilai) {
        if (nilai >= 85) return "A";
        else if (nilai >= 75) return "B";
        else if (nilai >= 65) return "C";
        else if (nilai >= 55) return "D";
        else return "E";
    }

    // ===================== HITUNG OTOMATIS =====================
    public void hitungOtomatis() {
        try {
            double uas   = Double.parseDouble(txtUAS.getText().trim());
            double uts   = Double.parseDouble(txtUTS.getText().trim());
            double tugas = Double.parseDouble(txtTugas.getText().trim());
            double akhir = (uas * 0.4) + (uts * 0.3) + (tugas * 0.3);
            txtAkhir.setText(String.format("%.2f", akhir));
            txtGrade.setText(hitungGrade(akhir));
        } catch (NumberFormatException e) {
            txtAkhir.setText("");
            txtGrade.setText("");
        }
    }

    // ===================== RESET FORM =====================
    public void resetForm() {
        txtNIM.setText("");
        txtNama.setText("");
        txtMK.setText("");
        txtUAS.setText("");
        txtUTS.setText("");
        txtTugas.setText("");
        txtAkhir.setText("");
        txtGrade.setText("");
        isEdit = false;
        btnSimpan.setText("Simpan");
        jTable1.clearSelection();
    }

    // ===================== INIT COMPONENTS =====================
    private void initComponents() {

        // --- Komponen Form ---
        JLabel lblNIM   = new JLabel("NIM");
        JLabel lblNama  = new JLabel("Nama");
        JLabel lblMK    = new JLabel("Mata Kuliah");
        JLabel lblUAS   = new JLabel("Nilai UAS");
        JLabel lblUTS   = new JLabel("Nilai UTS");
        JLabel lblTugas = new JLabel("Nilai Tugas");
        JLabel lblAkhir = new JLabel("Nilai Akhir");
        JLabel lblGrade = new JLabel("Grade");

        txtNIM   = new JTextField(15);
        txtNama  = new JTextField(15);
        txtMK    = new JTextField(15);
        txtUAS   = new JTextField(15);
        txtUTS   = new JTextField(15);
        txtTugas = new JTextField(15);
        txtAkhir = new JTextField(15);
        txtGrade = new JTextField(5);
        txtAkhir.setEditable(false);
        txtGrade.setEditable(false);

        btnSimpan = new JButton("Simpan");
        btnHapus  = new JButton("Hapus");
        btnBatal  = new JButton("Batal");

        // --- Komponen Search ---
        JLabel lblCari  = new JLabel("Cari:");
        txtCari         = new JTextField(15);
        btnCari         = new JButton("Cari");
        btnTampilSemua  = new JButton("Tampil Semua");
        lblStatus       = new JLabel("Total data: 0");
        String[] kategori = {"Semua", "NIM", "Nama", "Mata_kuliah", "Grade"};
        cmbKategori     = new JComboBox<>(kategori);

        // --- Tabel ---
        jTable1 = new JTable();
        jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"NIM", "Nama", "Mata Kuliah", "UAS", "UTS", "Tugas", "Nilai Akhir", "Grade"}
        ));
        JScrollPane scrollPane = new JScrollPane(jTable1);

        // =================== LAYOUT ===================

        // Panel Form (kiri atas)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Input Nilai"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(formPanel, c, row++, lblNIM,   txtNIM);
        addRow(formPanel, c, row++, lblNama,  txtNama);
        addRow(formPanel, c, row++, lblMK,    txtMK);
        addRow(formPanel, c, row++, lblUAS,   txtUAS);
        addRow(formPanel, c, row++, lblUTS,   txtUTS);
        addRow(formPanel, c, row++, lblTugas, txtTugas);
        addRow(formPanel, c, row++, lblAkhir, txtAkhir);
        addRow(formPanel, c, row++, lblGrade, txtGrade);

        // Tombol aksi
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.add(btnSimpan);
        btnPanel.add(btnHapus);
        btnPanel.add(btnBatal);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        formPanel.add(btnPanel, c);

        // Panel Search + Tabel (kanan)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.add(lblCari);
        searchPanel.add(cmbKategori);
        searchPanel.add(txtCari);
        searchPanel.add(btnCari);
        searchPanel.add(btnTampilSemua);
        searchPanel.add(lblStatus);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Data Nilai Mahasiswa"));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Main layout
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);

        // =================== ACTION LISTENERS ===================

        btnSimpan.addActionListener(e -> simpanData());
        btnHapus.addActionListener(e -> hapusData());
        btnBatal.addActionListener(e -> resetForm());
        btnCari.addActionListener(e -> cariData());
        btnTampilSemua.addActionListener(e -> {
            txtCari.setText("");
            cmbKategori.setSelectedIndex(0);
            tampilData();
        });

        // Hitung otomatis saat ketik nilai
        KeyAdapter hitungListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                hitungOtomatis();
            }
        };
        txtUAS.addKeyListener(hitungListener);
        txtUTS.addKeyListener(hitungListener);
        txtTugas.addKeyListener(hitungListener);

        // Cari realtime
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                cariData();
            }
        });

        // Klik baris tabel → isi form
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int baris = jTable1.getSelectedRow();
                txtNIM.setText(model.getValueAt(baris, 0).toString());
                txtNama.setText(model.getValueAt(baris, 1).toString());
                txtMK.setText(model.getValueAt(baris, 2).toString());
                txtUAS.setText(model.getValueAt(baris, 3).toString());
                txtUTS.setText(model.getValueAt(baris, 4).toString());
                txtTugas.setText(model.getValueAt(baris, 5).toString());
                txtAkhir.setText(model.getValueAt(baris, 6).toString());
                txtGrade.setText(model.getValueAt(baris, 7).toString());
                isEdit = true;
                btnSimpan.setText("Update");
            }
        });

        setTitle("Administrasi Nilai Mahasiswa");
        setSize(950, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Helper tambah baris form
    private void addRow(JPanel panel, GridBagConstraints c, int row, JLabel lbl, JTextField tf) {
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = row; panel.add(lbl, c);
        c.gridx = 1; c.gridy = row; panel.add(tf, c);
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApkNilai().setVisible(true));
    }

    // Variables
    private JTextField txtNIM, txtNama, txtMK, txtUAS, txtUTS, txtTugas, txtAkhir, txtGrade, txtCari;
    private JButton btnSimpan, btnHapus, btnBatal, btnCari, btnTampilSemua;
    private JComboBox<String> cmbKategori;
    private JTable jTable1;
    private JLabel lblStatus;
    private boolean isEdit = false;
}