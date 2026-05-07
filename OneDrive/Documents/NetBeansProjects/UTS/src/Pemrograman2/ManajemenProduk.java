package Pemrograman2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.logging.*;

// ===================== CLASS PRODUK =====================
class Produk {
    private String nama;
    private String kategori;
    private double harga;
    private int stok;

    public Produk(String nama, String kategori, double harga, int stok) {
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
    }

    public String getNama()       { return nama; }
    public String getKategori()   { return kategori; }
    public double getHarga()      { return harga; }
    public int    getStok()       { return stok; }

    public void setNama(String nama)         { this.nama = nama; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setHarga(double harga)       { this.harga = harga; }
    public void setStok(int stok)            { this.stok = stok; }

    @Override
    public String toString() {
        return String.format("Produk{nama='%s', kategori='%s', harga=%.2f, stok=%d}",
                nama, kategori, harga, stok);
    }
}

// ===================== CUSTOM EXCEPTION =====================
class StokKosongException extends Exception {
    public StokKosongException(String namaProduk) {
        super("Stok produk '" + namaProduk + "' sudah kosong! Tidak dapat dihapus.");
    }
}

class ProdukTidakDitemukanException extends Exception {
    public ProdukTidakDitemukanException(String namaProduk) {
        super("Produk '" + namaProduk + "' tidak ditemukan dalam sistem.");
    }
}

// ===================== STACK PRODUK =====================
class StackProduk {
    private Stack<Produk> stack;
    private static final Logger logger = Logger.getLogger(StackProduk.class.getName());

    public StackProduk() {
        this.stack = new Stack<>();
        setupLogger();
    }

    private void setupLogger() {
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public void push(Produk produk) {
        stack.push(produk);
        logger.info("[TAMBAH] Produk ditambahkan: " + produk);
    }

    public Produk pop() throws StokKosongException {
        if (stack.isEmpty()) {
            throw new StokKosongException("Stack");
        }
        Produk produk = stack.pop();
        logger.info("[HAPUS] Produk dihapus: " + produk);
        return produk;
    }

    public void hapusProduk(String nama) throws ProdukTidakDitemukanException, StokKosongException {
        Produk target = null;
        for (Produk p : stack) {
            if (p.getNama().equalsIgnoreCase(nama)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            throw new ProdukTidakDitemukanException(nama);
        }

        if (target.getStok() == 0) {
            throw new StokKosongException(nama);
        }

        stack.remove(target);
        logger.info("[HAPUS SPESIFIK] Produk dihapus: " + target);
    }

    public ArrayList<Produk> getAllProduk() {
        return new ArrayList<>(stack);
    }

    public ArrayList<Produk> sortByHarga() {
        ArrayList<Produk> sorted = new ArrayList<>(stack);
        Collections.sort(sorted, Comparator.comparingDouble(Produk::getHarga));
        logger.info("[SORT] Produk diurutkan berdasarkan harga.");
        return sorted;
    }

    public ArrayList<Produk> sortByKategori() {
        ArrayList<Produk> sorted = new ArrayList<>(stack);
        sorted.sort(Comparator.comparing(Produk::getKategori, String.CASE_INSENSITIVE_ORDER));
        logger.info("[SORT] Produk diurutkan berdasarkan kategori.");
        return sorted;
    }

    public ArrayList<Produk> searchByNama(String keyword) {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk p : stack) {
            if (p.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                hasil.add(p);
            }
        }
        logger.info("[SEARCH] Pencarian keyword='" + keyword + "', ditemukan: " + hasil.size() + " produk.");
        return hasil;
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}

// ===================== MAIN GUI =====================
public class ManajemenProduk extends JFrame {

    private StackProduk stackProduk;
    private DefaultTableModel tableModel;
    private JTable tabelProduk;
    private JTextField txtNama, txtKategori, txtHarga, txtStok, txtCari;
    private JLabel lblStatus;
    private static final Logger logger = Logger.getLogger(ManajemenProduk.class.getName());

    public ManajemenProduk() {
        stackProduk = new StackProduk();
        initUI();
        tambahDataContoh();
    }

    private void initUI() {
        setTitle("Aplikasi Manajemen Produk - Toko Kecil");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        final Color colorPrimary   = new Color(33, 97, 140);
        final Color colorSecondary = new Color(52, 152, 219);
        Color colorBg        = new Color(236, 240, 241);

        getContentPane().setBackground(colorBg);

        // ===== PANEL HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(colorPrimary);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🏪 Manajemen Produk Toko");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Pemrograman II - UTS 2025/2026");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitle.setForeground(new Color(200, 230, 255));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== PANEL INPUT =====
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorSecondary, 2),
                "  Input Produk  ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                colorPrimary));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Nama Produk:"), gbc);
        gbc.gridx = 1; txtNama = new JTextField(15); inputPanel.add(txtNama, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 3; txtKategori = new JTextField(12); inputPanel.add(txtKategori, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Harga (Rp):"), gbc);
        gbc.gridx = 1; txtHarga = new JTextField(15); inputPanel.add(txtHarga, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 3; txtStok = new JTextField(12); inputPanel.add(txtStok, gbc);

        JPanel btnInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnInputPanel.setBackground(Color.WHITE);

        JButton btnTambah     = buatTombol("➕ Tambah Produk",  new Color(39, 174, 96));
        JButton btnHapusStack = buatTombol("🗑 Hapus Teratas",  new Color(192, 57, 43));
        JButton btnHapusNama  = buatTombol("❌ Hapus by Nama",  new Color(231, 76, 60));
        JButton btnReset      = buatTombol("🔄 Reset Form",     new Color(127, 140, 141));

        btnTambah.addActionListener(e -> tambahProduk());
        btnHapusStack.addActionListener(e -> hapusProdukTeratas());
        btnHapusNama.addActionListener(e -> hapusProdukByNama());
        btnReset.addActionListener(e -> resetForm());

        btnInputPanel.add(btnTambah);
        btnInputPanel.add(btnHapusStack);
        btnInputPanel.add(btnHapusNama);
        btnInputPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        inputPanel.add(btnInputPanel, gbc);

        add(inputPanel, BorderLayout.WEST);

        // ===== PANEL TABEL =====
        String[] kolom = {"Nama Produk", "Kategori", "Harga (Rp)", "Stok"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabelProduk = new JTable(tableModel);
        tabelProduk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelProduk.setRowHeight(26);
        tabelProduk.setSelectionBackground(new Color(174, 214, 241));
        tabelProduk.setGridColor(new Color(189, 195, 199));

        // ===== PERBAIKAN: Custom renderer untuk header tabel =====
        tabelProduk.getTableHeader().setPreferredSize(new Dimension(0, 30));
        tabelProduk.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(colorPrimary);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, colorSecondary),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelProduk);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorSecondary, 2),
                "  Daftar Produk  ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                colorPrimary));

        // ===== PANEL SEARCH & SORT =====
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setBackground(colorBg);

        JPanel searchSortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchSortPanel.setBackground(colorBg);

        txtCari = new JTextField(20);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnCari        = buatTombol("🔍 Cari",          colorSecondary);
        JButton btnTampilSemua = buatTombol("📋 Tampil Semua",  colorPrimary);
        JButton btnSortHarga   = buatTombol("💰 Sort Harga",    new Color(142, 68, 173));
        JButton btnSortKategori= buatTombol("📦 Sort Kategori", new Color(41, 128, 185));

        btnCari.addActionListener(e -> cariProduk());
        btnTampilSemua.addActionListener(e -> tampilkanSemuaProduk());
        btnSortHarga.addActionListener(e -> sortByHarga());
        btnSortKategori.addActionListener(e -> sortByKategori());

        searchSortPanel.add(new JLabel("🔎 Cari Nama:"));
        searchSortPanel.add(txtCari);
        searchSortPanel.add(btnCari);
        searchSortPanel.add(new JSeparator(SwingConstants.VERTICAL));
        searchSortPanel.add(btnTampilSemua);
        searchSortPanel.add(btnSortHarga);
        searchSortPanel.add(btnSortKategori);

        lblStatus = new JLabel("  ✅ Aplikasi siap digunakan.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(39, 174, 96));
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        lblStatus.setBackground(new Color(250, 250, 250));
        lblStatus.setOpaque(true);

        bottomPanel.add(searchSortPanel, BorderLayout.CENTER);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) cariProduk();
            }
        });
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void tambahProduk() {
        try {
            String nama     = txtNama.getText().trim();
            String kategori = txtKategori.getText().trim();
            String hargaStr = txtHarga.getText().trim();
            String stokStr  = txtStok.getText().trim();

            if (nama.isEmpty() || kategori.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty()) {
                throw new IllegalArgumentException("Semua field harus diisi!");
            }

            double harga = Double.parseDouble(hargaStr);
            int stok = Integer.parseInt(stokStr);

            if (harga < 0) throw new IllegalArgumentException("Harga tidak boleh negatif!");
            if (stok < 0)  throw new IllegalArgumentException("Stok tidak boleh negatif!");

            Produk produk = new Produk(nama, kategori, harga, stok);
            stackProduk.push(produk);
            tampilkanSemuaProduk();
            setStatus("✅ Produk '" + nama + "' berhasil ditambahkan. Total: " + stackProduk.size() + " produk.", true);
            resetForm();

        } catch (NumberFormatException e) {
            tampilkanError("Format harga atau stok tidak valid! Masukkan angka.");
            logger.warning("[ERROR] Format angka salah: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            tampilkanError(e.getMessage());
            logger.warning("[ERROR] Input tidak valid: " + e.getMessage());
        }
    }

    private void hapusProdukTeratas() {
        try {
            if (stackProduk.isEmpty()) {
                throw new StokKosongException("Stack kosong");
            }
            Produk dihapus = stackProduk.pop();
            tampilkanSemuaProduk();
            setStatus("🗑 Produk teratas '" + dihapus.getNama() + "' berhasil dihapus dari stack.", true);

        } catch (StokKosongException e) {
            tampilkanError("Stack kosong! Tidak ada produk untuk dihapus.");
            logger.warning("[ERROR] " + e.getMessage());
        }
    }

    private void hapusProdukByNama() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            tampilkanError("Masukkan nama produk yang ingin dihapus di field 'Nama Produk'.");
            return;
        }

        try {
            stackProduk.hapusProduk(nama);
            tampilkanSemuaProduk();
            setStatus("❌ Produk '" + nama + "' berhasil dihapus.", true);
            txtNama.setText("");

        } catch (ProdukTidakDitemukanException e) {
            tampilkanError(e.getMessage());
            logger.warning("[ERROR] " + e.getMessage());
        } catch (StokKosongException e) {
            tampilkanError(e.getMessage());
            logger.warning("[ERROR] " + e.getMessage());
        }
    }

    private void cariProduk() {
        String keyword = txtCari.getText().trim();
        if (keyword.isEmpty()) {
            tampilkanSemuaProduk();
            return;
        }
        ArrayList<Produk> hasil = stackProduk.searchByNama(keyword);
        updateTabel(hasil);
        if (hasil.isEmpty()) {
            setStatus("🔍 Pencarian '" + keyword + "': Produk tidak ditemukan.", false);
        } else {
            setStatus("🔍 Ditemukan " + hasil.size() + " produk dengan keyword '" + keyword + "'.", true);
        }
    }

    private void sortByHarga() {
        ArrayList<Produk> sorted = stackProduk.sortByHarga();
        updateTabel(sorted);
        setStatus("💰 Produk diurutkan berdasarkan harga (termurah ke termahal).", true);
    }

    private void sortByKategori() {
        ArrayList<Produk> sorted = stackProduk.sortByKategori();
        updateTabel(sorted);
        setStatus("📦 Produk diurutkan berdasarkan kategori (A-Z).", true);
    }

    private void tampilkanSemuaProduk() {
        updateTabel(stackProduk.getAllProduk());
        setStatus("📋 Menampilkan semua produk. Total: " + stackProduk.size() + " produk.", true);
    }

    private void updateTabel(ArrayList<Produk> daftarProduk) {
        tableModel.setRowCount(0);
        for (Produk p : daftarProduk) {
            tableModel.addRow(new Object[]{
                p.getNama(),
                p.getKategori(),
                String.format("Rp %,.0f", p.getHarga()),
                p.getStok()
            });
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtKategori.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        txtNama.requestFocus();
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "⚠ Error", JOptionPane.ERROR_MESSAGE);
        setStatus("❌ " + pesan, false);
    }

    private void setStatus(String pesan, boolean sukses) {
        lblStatus.setText("  " + pesan);
        lblStatus.setForeground(sukses ? new Color(39, 174, 96) : new Color(192, 57, 43));
    }

    private void tambahDataContoh() {
        stackProduk.push(new Produk("Laptop Asus VivoBook",          "Elektronik",  8500000, 5));
        stackProduk.push(new Produk("Mouse Wireless Logitech",        "Elektronik",   185000, 20));
        stackProduk.push(new Produk("Buku Pemrograman Java",          "Buku",          95000, 15));
        stackProduk.push(new Produk("Keyboard Mechanical",            "Elektronik",   650000, 8));
        stackProduk.push(new Produk("Tas Ransel Laptop",              "Aksesoris",    275000, 12));
        stackProduk.push(new Produk("Flash Drive 64GB",               "Elektronik",   120000, 30));
        stackProduk.push(new Produk("Buku Algoritma & Struktur Data", "Buku",         110000, 10));
        tampilkanSemuaProduk();
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tT] %4$s %2$s - %5$s%n");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Tidak bisa set Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            ManajemenProduk app = new ManajemenProduk();
            app.setVisible(true);
        });
    }
}