package Pert9;

import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

// ==================== DBHelper ====================
class DBHelper {
    static Connection conn;
    public static Connection koneksi() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/penjualan", "root", "");
            }
        } catch (Exception e) { System.err.println("Koneksi gagal: " + e.getMessage()); }
        return conn;
    }
}

// ==================== BarangForm ====================
class BarangForm extends JPanel {
    Connection conn; ResultSet rs; DefaultTableModel model; boolean isEdit = false;
    JTextField txtId, txtNama, txtKategori, txtHargaBeli, txtHargaJual, txtStok, txtSupplier, txtCari;
    JButton btnSimpan, btnHapus, btnBatal, btnCari, btnTampilSemua;
    JTable jTable1; JLabel lblStatus;

    public BarangForm() {
        conn = DBHelper.koneksi(); initComponents(); tampilData();
    }

    void tampilData() {
        try {
            model = (DefaultTableModel) jTable1.getModel(); model.setRowCount(0);
            rs = conn.createStatement().executeQuery("SELECT * FROM barang");
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_barang"), rs.getString("nama_barang"), rs.getString("kategori"),
                rs.getDouble("harga_beli"), rs.getDouble("harga_jual"), rs.getInt("stok"), rs.getString("id_supplier")
            });
            lblStatus.setText("Total: " + model.getRowCount() + " barang");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal tampil: " + e.getMessage()); }
    }

    void simpanData() {
        String id = txtId.getText().trim(), nama = txtNama.getText().trim();
        if (id.isEmpty() || nama.isEmpty()) { JOptionPane.showMessageDialog(this, "ID dan Nama wajib diisi!"); return; }
        try {
            String kategori = txtKategori.getText().trim(), supplier = txtSupplier.getText().trim();
            double hargaBeli = Double.parseDouble(txtHargaBeli.getText().trim());
            double hargaJual = Double.parseDouble(txtHargaJual.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());
            if (isEdit) {
                conn.createStatement().executeUpdate("UPDATE barang SET nama_barang='" + nama + "', kategori='" + kategori +
                    "', harga_beli=" + hargaBeli + ", harga_jual=" + hargaJual + ", stok=" + stok +
                    ", id_supplier='" + supplier + "' WHERE id_barang='" + id + "'");
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            } else {
                conn.createStatement().executeUpdate("INSERT INTO barang VALUES ('" + id + "','" + nama + "','" +
                    kategori + "'," + hargaBeli + "," + hargaJual + "," + stok + ",'" + supplier + "')");
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            }
            resetForm(); tampilData();
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Harga dan stok harus angka!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage()); }
    }

    void hapusData() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih data dari tabel!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                conn.createStatement().executeUpdate("DELETE FROM barang WHERE id_barang='" + id + "'");
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!"); resetForm(); tampilData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage()); }
        }
    }

    void cariData() {
        try {
            String kw = txtCari.getText().trim(); model = (DefaultTableModel) jTable1.getModel(); model.setRowCount(0);
            String sql = kw.isEmpty() ? "SELECT * FROM barang" :
                "SELECT * FROM barang WHERE id_barang LIKE '%" + kw + "%' OR nama_barang LIKE '%" + kw + "%' OR kategori LIKE '%" + kw + "%'";
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_barang"), rs.getString("nama_barang"), rs.getString("kategori"),
                rs.getDouble("harga_beli"), rs.getDouble("harga_jual"), rs.getInt("stok"), rs.getString("id_supplier")
            });
            lblStatus.setText("Ditemukan: " + model.getRowCount() + " data");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal cari: " + e.getMessage()); }
    }

    void resetForm() {
        txtId.setText(""); txtNama.setText(""); txtKategori.setText("");
        txtHargaBeli.setText(""); txtHargaJual.setText(""); txtStok.setText("");
        txtSupplier.setText(""); isEdit = false; btnSimpan.setText("Simpan"); jTable1.clearSelection();
    }

    private void initComponents() {
        txtId=new JTextField(15); txtNama=new JTextField(15); txtKategori=new JTextField(15);
        txtHargaBeli=new JTextField(15); txtHargaJual=new JTextField(15); txtStok=new JTextField(15);
        txtSupplier=new JTextField(15); txtCari=new JTextField(15);
        btnSimpan=new JButton("Simpan"); btnHapus=new JButton("Hapus");
        btnBatal=new JButton("Batal"); btnCari=new JButton("Cari");
        btnTampilSemua=new JButton("Tampil Semua"); lblStatus=new JLabel("Total: 0 barang");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Form Data Barang"));
        formPanel.setPreferredSize(new Dimension(270, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(4,5,4,5); c.fill=GridBagConstraints.HORIZONTAL;
        String[] labels={"ID Barang","Nama Barang","Kategori","Harga Beli","Harga Jual","Stok","ID Supplier"};
        JTextField[] fields={txtId,txtNama,txtKategori,txtHargaBeli,txtHargaJual,txtStok,txtSupplier};
        for (int i=0;i<labels.length;i++) {
            c.gridx=0;c.gridy=i;c.gridwidth=1; formPanel.add(new JLabel(labels[i]),c);
            c.gridx=1; formPanel.add(fields[i],c);
        }
        JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        btnSimpan.setBackground(new Color(39,174,96)); btnSimpan.setForeground(Color.WHITE);
        btnHapus.setBackground(new Color(231,76,60)); btnHapus.setForeground(Color.WHITE);
        btnPanel.add(btnSimpan); btnPanel.add(btnHapus); btnPanel.add(btnBatal);
        c.gridx=0;c.gridy=labels.length;c.gridwidth=2; formPanel.add(btnPanel,c);

        jTable1=new JTable(new DefaultTableModel(new Object[][]{},
            new String[]{"ID","Nama Barang","Kategori","Harga Beli","Harga Jual","Stok","Supplier"}));
        jTable1.setRowHeight(25);

        JPanel searchPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        searchPanel.add(new JLabel("Cari:")); searchPanel.add(txtCari);
        searchPanel.add(btnCari); searchPanel.add(btnTampilSemua); searchPanel.add(lblStatus);

        JPanel tablePanel=new JPanel(new BorderLayout(5,5));
        tablePanel.setBorder(new TitledBorder("Data Barang"));
        tablePanel.add(searchPanel,BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(jTable1),BorderLayout.CENTER);

        setLayout(new BorderLayout(10,10));
        add(formPanel,BorderLayout.WEST); add(tablePanel,BorderLayout.CENTER);

        btnSimpan.addActionListener(e->simpanData()); btnHapus.addActionListener(e->hapusData());
        btnBatal.addActionListener(e->resetForm()); btnCari.addActionListener(e->cariData());
        btnTampilSemua.addActionListener(e->{txtCari.setText("");tampilData();});
        txtCari.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){cariData();}});
        jTable1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row=jTable1.getSelectedRow(); if(row<0)return;
                txtId.setText(model.getValueAt(row,0).toString());
                txtNama.setText(model.getValueAt(row,1).toString());
                txtKategori.setText(model.getValueAt(row,2).toString());
                txtHargaBeli.setText(model.getValueAt(row,3).toString());
                txtHargaJual.setText(model.getValueAt(row,4).toString());
                txtStok.setText(model.getValueAt(row,5).toString());
                txtSupplier.setText(model.getValueAt(row,6).toString());
                isEdit=true; btnSimpan.setText("Update");
            }
        });
    }
}

// ==================== CustomerForm ====================
class CustomerForm extends JPanel {
    Connection conn; ResultSet rs; DefaultTableModel model; boolean isEdit=false;
    JTextField txtId,txtNama,txtAlamat,txtTelepon,txtEmail,txtCari;
    JButton btnSimpan,btnHapus,btnBatal,btnCari,btnTampilSemua;
    JTable jTable1; JLabel lblStatus;

    public CustomerForm() { conn=DBHelper.koneksi(); initComponents(); tampilData(); }

    void tampilData() {
        try {
            model=(DefaultTableModel)jTable1.getModel(); model.setRowCount(0);
            rs=conn.createStatement().executeQuery("SELECT * FROM customer");
            while(rs.next()) model.addRow(new Object[]{
                rs.getString("id_customer"),rs.getString("nama_customer"),
                rs.getString("alamat"),rs.getString("telepon"),rs.getString("email")
            });
            lblStatus.setText("Total: "+model.getRowCount()+" customer");
        } catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal tampil: "+e.getMessage());}
    }

    void simpanData() {
        String id=txtId.getText().trim(), nama=txtNama.getText().trim();
        if(id.isEmpty()||nama.isEmpty()){JOptionPane.showMessageDialog(this,"ID dan Nama wajib diisi!");return;}
        try {
            String alamat=txtAlamat.getText().trim(),telepon=txtTelepon.getText().trim(),email=txtEmail.getText().trim();
            if(isEdit){
                conn.createStatement().executeUpdate("UPDATE customer SET nama_customer='"+nama+"', alamat='"+alamat+
                    "', telepon='"+telepon+"', email='"+email+"' WHERE id_customer='"+id+"'");
                JOptionPane.showMessageDialog(this,"Data berhasil diupdate!");
            } else {
                conn.createStatement().executeUpdate("INSERT INTO customer VALUES ('"+id+"','"+nama+"','"+alamat+"','"+telepon+"','"+email+"')");
                JOptionPane.showMessageDialog(this,"Data berhasil disimpan!");
            }
            resetForm(); tampilData();
        } catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal simpan: "+e.getMessage());}
    }

    void hapusData() {
        String id=txtId.getText().trim();
        if(id.isEmpty()){JOptionPane.showMessageDialog(this,"Pilih data dari tabel!");return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{conn.createStatement().executeUpdate("DELETE FROM customer WHERE id_customer='"+id+"'");
                JOptionPane.showMessageDialog(this,"Data berhasil dihapus!");resetForm();tampilData();
            }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal hapus: "+e.getMessage());}
        }
    }

    void cariData() {
        try {
            String kw=txtCari.getText().trim(); model=(DefaultTableModel)jTable1.getModel(); model.setRowCount(0);
            String sql=kw.isEmpty()?"SELECT * FROM customer":
                "SELECT * FROM customer WHERE id_customer LIKE '%"+kw+"%' OR nama_customer LIKE '%"+kw+"%' OR telepon LIKE '%"+kw+"%'";
            rs=conn.createStatement().executeQuery(sql);
            while(rs.next()) model.addRow(new Object[]{
                rs.getString("id_customer"),rs.getString("nama_customer"),
                rs.getString("alamat"),rs.getString("telepon"),rs.getString("email")
            });
            lblStatus.setText("Ditemukan: "+model.getRowCount()+" data");
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal cari: "+e.getMessage());}
    }

    void resetForm(){
        txtId.setText("");txtNama.setText("");txtAlamat.setText("");
        txtTelepon.setText("");txtEmail.setText("");
        isEdit=false;btnSimpan.setText("Simpan");jTable1.clearSelection();
    }

    private void initComponents(){
        txtId=new JTextField(15);txtNama=new JTextField(15);txtAlamat=new JTextField(15);
        txtTelepon=new JTextField(15);txtEmail=new JTextField(15);txtCari=new JTextField(15);
        btnSimpan=new JButton("Simpan");btnHapus=new JButton("Hapus");
        btnBatal=new JButton("Batal");btnCari=new JButton("Cari");
        btnTampilSemua=new JButton("Tampil Semua");lblStatus=new JLabel("Total: 0 customer");

        JPanel formPanel=new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Form Data Customer"));
        formPanel.setPreferredSize(new Dimension(270,0));
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(4,5,4,5);c.fill=GridBagConstraints.HORIZONTAL;
        String[] labels={"ID Customer","Nama Customer","Alamat","Telepon","Email"};
        JTextField[] fields={txtId,txtNama,txtAlamat,txtTelepon,txtEmail};
        for(int i=0;i<labels.length;i++){
            c.gridx=0;c.gridy=i;c.gridwidth=1;formPanel.add(new JLabel(labels[i]),c);
            c.gridx=1;formPanel.add(fields[i],c);
        }
        JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        btnSimpan.setBackground(new Color(39,174,96));btnSimpan.setForeground(Color.WHITE);
        btnHapus.setBackground(new Color(231,76,60));btnHapus.setForeground(Color.WHITE);
        btnPanel.add(btnSimpan);btnPanel.add(btnHapus);btnPanel.add(btnBatal);
        c.gridx=0;c.gridy=labels.length;c.gridwidth=2;formPanel.add(btnPanel,c);

        jTable1=new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Nama Customer","Alamat","Telepon","Email"}));
        jTable1.setRowHeight(25);

        JPanel searchPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        searchPanel.add(new JLabel("Cari:"));searchPanel.add(txtCari);
        searchPanel.add(btnCari);searchPanel.add(btnTampilSemua);searchPanel.add(lblStatus);

        JPanel tablePanel=new JPanel(new BorderLayout(5,5));
        tablePanel.setBorder(new TitledBorder("Data Customer"));
        tablePanel.add(searchPanel,BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(jTable1),BorderLayout.CENTER);

        setLayout(new BorderLayout(10,10));
        add(formPanel,BorderLayout.WEST);add(tablePanel,BorderLayout.CENTER);

        btnSimpan.addActionListener(e->simpanData());btnHapus.addActionListener(e->hapusData());
        btnBatal.addActionListener(e->resetForm());btnCari.addActionListener(e->cariData());
        btnTampilSemua.addActionListener(e->{txtCari.setText("");tampilData();});
        txtCari.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){cariData();}});
        jTable1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row=jTable1.getSelectedRow();if(row<0)return;
                txtId.setText(model.getValueAt(row,0).toString());
                txtNama.setText(model.getValueAt(row,1).toString());
                txtAlamat.setText(model.getValueAt(row,2).toString());
                txtTelepon.setText(model.getValueAt(row,3).toString());
                txtEmail.setText(model.getValueAt(row,4).toString());
                isEdit=true;btnSimpan.setText("Update");
            }
        });
    }
}

// ==================== SupplierForm ====================
class SupplierForm extends JPanel {
    Connection conn; ResultSet rs; DefaultTableModel model; boolean isEdit=false;
    JTextField txtId,txtNama,txtAlamat,txtTelepon,txtEmail,txtCari;
    JButton btnSimpan,btnHapus,btnBatal,btnCari,btnTampilSemua;
    JTable jTable1; JLabel lblStatus;

    public SupplierForm(){conn=DBHelper.koneksi();initComponents();tampilData();}

    void tampilData(){
        try{
            model=(DefaultTableModel)jTable1.getModel();model.setRowCount(0);
            rs=conn.createStatement().executeQuery("SELECT * FROM supplier");
            while(rs.next()) model.addRow(new Object[]{
                rs.getString("id_supplier"),rs.getString("nama_supplier"),
                rs.getString("alamat"),rs.getString("telepon"),rs.getString("email")
            });
            lblStatus.setText("Total: "+model.getRowCount()+" supplier");
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal tampil: "+e.getMessage());}
    }

    void simpanData(){
        String id=txtId.getText().trim(),nama=txtNama.getText().trim();
        if(id.isEmpty()||nama.isEmpty()){JOptionPane.showMessageDialog(this,"ID dan Nama wajib diisi!");return;}
        try{
            String alamat=txtAlamat.getText().trim(),telepon=txtTelepon.getText().trim(),email=txtEmail.getText().trim();
            if(isEdit){
                conn.createStatement().executeUpdate("UPDATE supplier SET nama_supplier='"+nama+"', alamat='"+alamat+
                    "', telepon='"+telepon+"', email='"+email+"' WHERE id_supplier='"+id+"'");
                JOptionPane.showMessageDialog(this,"Data berhasil diupdate!");
            }else{
                conn.createStatement().executeUpdate("INSERT INTO supplier VALUES ('"+id+"','"+nama+"','"+alamat+"','"+telepon+"','"+email+"')");
                JOptionPane.showMessageDialog(this,"Data berhasil disimpan!");
            }
            resetForm();tampilData();
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal simpan: "+e.getMessage());}
    }

    void hapusData(){
        String id=txtId.getText().trim();
        if(id.isEmpty()){JOptionPane.showMessageDialog(this,"Pilih data dari tabel!");return;}
        if(JOptionPane.showConfirmDialog(this,"Yakin hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{conn.createStatement().executeUpdate("DELETE FROM supplier WHERE id_supplier='"+id+"'");
                JOptionPane.showMessageDialog(this,"Data berhasil dihapus!");resetForm();tampilData();
            }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal hapus: "+e.getMessage());}
        }
    }

    void cariData(){
        try{
            String kw=txtCari.getText().trim();model=(DefaultTableModel)jTable1.getModel();model.setRowCount(0);
            String sql=kw.isEmpty()?"SELECT * FROM supplier":
                "SELECT * FROM supplier WHERE id_supplier LIKE '%"+kw+"%' OR nama_supplier LIKE '%"+kw+"%'";
            rs=conn.createStatement().executeQuery(sql);
            while(rs.next()) model.addRow(new Object[]{
                rs.getString("id_supplier"),rs.getString("nama_supplier"),
                rs.getString("alamat"),rs.getString("telepon"),rs.getString("email")
            });
            lblStatus.setText("Ditemukan: "+model.getRowCount()+" data");
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal cari: "+e.getMessage());}
    }

    void resetForm(){
        txtId.setText("");txtNama.setText("");txtAlamat.setText("");
        txtTelepon.setText("");txtEmail.setText("");
        isEdit=false;btnSimpan.setText("Simpan");jTable1.clearSelection();
    }

    private void initComponents(){
        txtId=new JTextField(15);txtNama=new JTextField(15);txtAlamat=new JTextField(15);
        txtTelepon=new JTextField(15);txtEmail=new JTextField(15);txtCari=new JTextField(15);
        btnSimpan=new JButton("Simpan");btnHapus=new JButton("Hapus");
        btnBatal=new JButton("Batal");btnCari=new JButton("Cari");
        btnTampilSemua=new JButton("Tampil Semua");lblStatus=new JLabel("Total: 0 supplier");

        JPanel formPanel=new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Form Data Supplier"));
        formPanel.setPreferredSize(new Dimension(270,0));
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(4,5,4,5);c.fill=GridBagConstraints.HORIZONTAL;
        String[] labels={"ID Supplier","Nama Supplier","Alamat","Telepon","Email"};
        JTextField[] fields={txtId,txtNama,txtAlamat,txtTelepon,txtEmail};
        for(int i=0;i<labels.length;i++){
            c.gridx=0;c.gridy=i;c.gridwidth=1;formPanel.add(new JLabel(labels[i]),c);
            c.gridx=1;formPanel.add(fields[i],c);
        }
        JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        btnSimpan.setBackground(new Color(39,174,96));btnSimpan.setForeground(Color.WHITE);
        btnHapus.setBackground(new Color(231,76,60));btnHapus.setForeground(Color.WHITE);
        btnPanel.add(btnSimpan);btnPanel.add(btnHapus);btnPanel.add(btnBatal);
        c.gridx=0;c.gridy=labels.length;c.gridwidth=2;formPanel.add(btnPanel,c);

        jTable1=new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID","Nama Supplier","Alamat","Telepon","Email"}));
        jTable1.setRowHeight(25);

        JPanel searchPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        searchPanel.add(new JLabel("Cari:"));searchPanel.add(txtCari);
        searchPanel.add(btnCari);searchPanel.add(btnTampilSemua);searchPanel.add(lblStatus);

        JPanel tablePanel=new JPanel(new BorderLayout(5,5));
        tablePanel.setBorder(new TitledBorder("Data Supplier"));
        tablePanel.add(searchPanel,BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(jTable1),BorderLayout.CENTER);

        setLayout(new BorderLayout(10,10));
        add(formPanel,BorderLayout.WEST);add(tablePanel,BorderLayout.CENTER);

        btnSimpan.addActionListener(e->simpanData());btnHapus.addActionListener(e->hapusData());
        btnBatal.addActionListener(e->resetForm());btnCari.addActionListener(e->cariData());
        btnTampilSemua.addActionListener(e->{txtCari.setText("");tampilData();});
        txtCari.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){cariData();}});
        jTable1.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row=jTable1.getSelectedRow();if(row<0)return;
                txtId.setText(model.getValueAt(row,0).toString());
                txtNama.setText(model.getValueAt(row,1).toString());
                txtAlamat.setText(model.getValueAt(row,2).toString());
                txtTelepon.setText(model.getValueAt(row,3).toString());
                txtEmail.setText(model.getValueAt(row,4).toString());
                isEdit=true;btnSimpan.setText("Update");
            }
        });
    }
}

// ==================== LaporanForm ====================
class LaporanForm extends JPanel {
    Connection conn; ResultSet rs; DefaultTableModel model; String jenis;
    JTextField txtDari,txtSampai,txtCari;
    JButton btnFilter,btnTampilSemua,btnCari;
    JTable jTable1; JLabel lblStatus,lblTotal;

    public LaporanForm(String jenis){
        this.jenis=jenis; conn=DBHelper.koneksi(); initComponents(); tampilData("");
    }

    void tampilData(String keyword){
        try{
            model=(DefaultTableModel)jTable1.getModel();model.setRowCount(0);
            String sql;
            if(jenis.equals("transaksi")){
                sql="SELECT t.id_transaksi, c.nama_customer, t.tanggal, b.nama_barang, dt.jumlah, dt.harga_satuan, dt.subtotal " +
                    "FROM transaksi t JOIN customer c ON t.id_customer=c.id_customer " +
                    "JOIN detail_transaksi dt ON t.id_transaksi=dt.id_transaksi " +
                    "JOIN barang b ON dt.id_barang=b.id_barang";
                if(!keyword.isEmpty()) sql+=" WHERE c.nama_customer LIKE '%"+keyword+"%' OR b.nama_barang LIKE '%"+keyword+"%'";
                sql+=" ORDER BY t.tanggal DESC";
                rs=conn.createStatement().executeQuery(sql);
                double grandTotal=0;
                while(rs.next()){
                    double subtotal=rs.getDouble("subtotal"); grandTotal+=subtotal;
                    model.addRow(new Object[]{rs.getString("id_transaksi"),rs.getString("nama_customer"),
                        rs.getString("tanggal"),rs.getString("nama_barang"),rs.getInt("jumlah"),rs.getDouble("harga_satuan"),subtotal});
                }
                lblTotal.setText("Grand Total: Rp "+String.format("%,.0f",grandTotal));
            }else{
                sql="SELECT b.id_barang, b.nama_barang, b.kategori, b.harga_beli, b.harga_jual, b.stok, " +
                    "(b.harga_jual-b.harga_beli) as margin, s.nama_supplier " +
                    "FROM barang b LEFT JOIN supplier s ON b.id_supplier=s.id_supplier";
                if(!keyword.isEmpty()) sql+=" WHERE b.nama_barang LIKE '%"+keyword+"%' OR b.kategori LIKE '%"+keyword+"%'";
                rs=conn.createStatement().executeQuery(sql);
                int totalStok=0;
                while(rs.next()){
                    int stok=rs.getInt("stok"); totalStok+=stok;
                    model.addRow(new Object[]{rs.getString("id_barang"),rs.getString("nama_barang"),rs.getString("kategori"),
                        rs.getDouble("harga_beli"),rs.getDouble("harga_jual"),stok,rs.getDouble("margin"),rs.getString("nama_supplier")});
                }
                lblTotal.setText("Total Stok: "+totalStok+" item");
            }
            lblStatus.setText("Total data: "+model.getRowCount());
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal tampil laporan: "+e.getMessage());}
    }

    void filterTanggal(){
        if(!jenis.equals("transaksi"))return;
        String dari=txtDari.getText().trim(),sampai=txtSampai.getText().trim();
        if(dari.isEmpty()||sampai.isEmpty()){JOptionPane.showMessageDialog(this,"Isi tanggal dari dan sampai!");return;}
        try{
            model=(DefaultTableModel)jTable1.getModel();model.setRowCount(0);
            String sql="SELECT t.id_transaksi, c.nama_customer, t.tanggal, b.nama_barang, dt.jumlah, dt.harga_satuan, dt.subtotal " +
                "FROM transaksi t JOIN customer c ON t.id_customer=c.id_customer " +
                "JOIN detail_transaksi dt ON t.id_transaksi=dt.id_transaksi " +
                "JOIN barang b ON dt.id_barang=b.id_barang " +
                "WHERE t.tanggal BETWEEN '"+dari+"' AND '"+sampai+"' ORDER BY t.tanggal DESC";
            rs=conn.createStatement().executeQuery(sql);
            double grandTotal=0;
            while(rs.next()){
                double subtotal=rs.getDouble("subtotal"); grandTotal+=subtotal;
                model.addRow(new Object[]{rs.getString("id_transaksi"),rs.getString("nama_customer"),
                    rs.getString("tanggal"),rs.getString("nama_barang"),rs.getInt("jumlah"),rs.getDouble("harga_satuan"),subtotal});
            }
            lblTotal.setText("Grand Total: Rp "+String.format("%,.0f",grandTotal));
            lblStatus.setText("Total data: "+model.getRowCount());
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal filter: "+e.getMessage());}
    }

    private void initComponents(){
        txtDari=new JTextField(12);txtSampai=new JTextField(12);txtCari=new JTextField(15);
        btnFilter=new JButton("Filter");btnTampilSemua=new JButton("Tampil Semua");btnCari=new JButton("Cari");
        lblStatus=new JLabel("Total data: 0");
        lblTotal=new JLabel("Total: 0");
        lblTotal.setFont(new Font("Segoe UI",Font.BOLD,14));
        lblTotal.setForeground(new Color(39,174,96));

        jTable1=new JTable();
        if(jenis.equals("transaksi"))
            jTable1.setModel(new DefaultTableModel(new Object[][]{},new String[]{"ID Transaksi","Customer","Tanggal","Nama Barang","Jumlah","Harga Satuan","Subtotal"}));
        else
            jTable1.setModel(new DefaultTableModel(new Object[][]{},new String[]{"ID","Nama Barang","Kategori","Harga Beli","Harga Jual","Stok","Margin","Supplier"}));
        model=(DefaultTableModel)jTable1.getModel();
        jTable1.setRowHeight(25);

        JPanel panelFilter=new JPanel(new FlowLayout(FlowLayout.LEFT,8,5));
        panelFilter.setBorder(new TitledBorder("Filter"));
        if(jenis.equals("transaksi")){
            panelFilter.add(new JLabel("Dari:"));panelFilter.add(txtDari);
            panelFilter.add(new JLabel("Sampai:"));panelFilter.add(txtSampai);
            panelFilter.add(btnFilter);
        }
        panelFilter.add(new JLabel("  Cari:"));panelFilter.add(txtCari);
        panelFilter.add(btnCari);panelFilter.add(btnTampilSemua);panelFilter.add(lblStatus);

        JPanel panelBottom=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5));
        panelBottom.add(lblTotal);

        String judul=jenis.equals("transaksi")?"Laporan Transaksi Penjualan":"Laporan Inventory Barang";
        JPanel panelTabel=new JPanel(new BorderLayout(5,5));
        panelTabel.setBorder(new TitledBorder(judul));
        panelTabel.add(new JScrollPane(jTable1),BorderLayout.CENTER);
        panelTabel.add(panelBottom,BorderLayout.SOUTH);

        setLayout(new BorderLayout(5,5));
        add(panelFilter,BorderLayout.NORTH);add(panelTabel,BorderLayout.CENTER);

        btnFilter.addActionListener(e->filterTanggal());
        btnTampilSemua.addActionListener(e->{txtCari.setText("");tampilData("");});
        btnCari.addActionListener(e->tampilData(txtCari.getText().trim()));
        txtCari.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){tampilData(txtCari.getText().trim());}});
    }
}

// ==================== TransaksiForm ====================
class TransaksiForm extends JPanel {
    Connection conn; ResultSet rs;
    DefaultTableModel modelKeranjang,modelRiwayat;
    JTextField txtIdTransaksi,txtTanggal,txtIdBarang,txtNamaBarang,txtHarga,txtJumlah,txtSubtotal,txtTotal;
    JComboBox<String> cmbCustomer;
    JButton btnTambah,btnHapusItem,btnSimpan,btnBatal,btnTampilSemua;
    JTable tblKeranjang,tblRiwayat; JLabel lblStatus;
    double total=0;

    public TransaksiForm(){conn=DBHelper.koneksi();initComponents();muatCustomer();tampilRiwayat();generateIdTransaksi();}

    void generateIdTransaksi(){
        try{
            rs=conn.createStatement().executeQuery("SELECT COUNT(*) FROM transaksi");
            rs.next(); int count=rs.getInt(1)+1;
            txtIdTransaksi.setText("T"+String.format("%03d",count));
            txtTanggal.setText(LocalDate.now().toString());
        }catch(Exception e){}
    }

    void muatCustomer(){
        try{
            cmbCustomer.removeAllItems();
            rs=conn.createStatement().executeQuery("SELECT id_customer, nama_customer FROM customer");
            while(rs.next()) cmbCustomer.addItem(rs.getString("id_customer")+" - "+rs.getString("nama_customer"));
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal muat customer: "+e.getMessage());}
    }

    void cariBarang(){
        String idBarang=txtIdBarang.getText().trim(); if(idBarang.isEmpty())return;
        try{
            rs=conn.createStatement().executeQuery("SELECT * FROM barang WHERE id_barang='"+idBarang+"'");
            if(rs.next()){txtNamaBarang.setText(rs.getString("nama_barang"));txtHarga.setText(String.valueOf(rs.getDouble("harga_jual")));hitungSubtotal();}
            else{JOptionPane.showMessageDialog(this,"Barang tidak ditemukan!");txtNamaBarang.setText("");txtHarga.setText("");}
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal cari barang: "+e.getMessage());}
    }

    void hitungSubtotal(){
        try{double harga=Double.parseDouble(txtHarga.getText().trim());int jumlah=Integer.parseInt(txtJumlah.getText().trim());
            txtSubtotal.setText(String.valueOf(harga*jumlah));
        }catch(Exception e){txtSubtotal.setText("0");}
    }

    void tambahKeKeranjang(){
        String idBarang=txtIdBarang.getText().trim(),namaBarang=txtNamaBarang.getText().trim();
        if(idBarang.isEmpty()||namaBarang.isEmpty()){JOptionPane.showMessageDialog(this,"Cari barang terlebih dahulu!");return;}
        try{
            double harga=Double.parseDouble(txtHarga.getText().trim());
            int jumlah=Integer.parseInt(txtJumlah.getText().trim());
            rs=conn.createStatement().executeQuery("SELECT stok FROM barang WHERE id_barang='"+idBarang+"'");
            rs.next();
            if(jumlah>rs.getInt("stok")){JOptionPane.showMessageDialog(this,"Stok tidak cukup! Tersedia: "+rs.getInt("stok"));return;}
            double subtotal=harga*jumlah;
            modelKeranjang.addRow(new Object[]{idBarang,namaBarang,harga,jumlah,subtotal});
            total+=subtotal; txtTotal.setText(String.valueOf(total));
            txtIdBarang.setText("");txtNamaBarang.setText("");txtHarga.setText("");txtJumlah.setText("1");txtSubtotal.setText("0");
        }catch(NumberFormatException e){JOptionPane.showMessageDialog(this,"Jumlah harus angka!");
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());}
    }

    void hapusItem(){
        int row=tblKeranjang.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Pilih item yang akan dihapus!");return;}
        total-=(double)modelKeranjang.getValueAt(row,4);
        txtTotal.setText(String.valueOf(total)); modelKeranjang.removeRow(row);
    }

    void simpanTransaksi(){
        if(modelKeranjang.getRowCount()==0){JOptionPane.showMessageDialog(this,"Keranjang masih kosong!");return;}
        String idTransaksi=txtIdTransaksi.getText().trim(),tanggal=txtTanggal.getText().trim();
        String customer=cmbCustomer.getSelectedItem().toString().split(" - ")[0];
        try{
            conn.createStatement().executeUpdate("INSERT INTO transaksi VALUES ('"+idTransaksi+"','"+customer+"','"+tanggal+"',"+total+",'')");
            for(int i=0;i<modelKeranjang.getRowCount();i++){
                String idBarang=modelKeranjang.getValueAt(i,0).toString();
                double harga=(double)modelKeranjang.getValueAt(i,2);
                int jumlah=(int)modelKeranjang.getValueAt(i,3);
                double subtotal=(double)modelKeranjang.getValueAt(i,4);
                conn.createStatement().executeUpdate("INSERT INTO detail_transaksi (id_transaksi,id_barang,jumlah,harga_satuan,subtotal) VALUES ('"+idTransaksi+"','"+idBarang+"',"+jumlah+","+harga+","+subtotal+")");
                conn.createStatement().executeUpdate("UPDATE barang SET stok=stok-"+jumlah+" WHERE id_barang='"+idBarang+"'");
            }
            JOptionPane.showMessageDialog(this,"Transaksi berhasil disimpan!\nID: "+idTransaksi);
            resetForm(); tampilRiwayat();
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal simpan transaksi: "+e.getMessage());}
    }

    void tampilRiwayat(){
        try{
            modelRiwayat=(DefaultTableModel)tblRiwayat.getModel();modelRiwayat.setRowCount(0);
            rs=conn.createStatement().executeQuery("SELECT t.id_transaksi, c.nama_customer, t.tanggal, t.total_harga " +
                "FROM transaksi t JOIN customer c ON t.id_customer=c.id_customer ORDER BY t.tanggal DESC");
            while(rs.next()) modelRiwayat.addRow(new Object[]{
                rs.getString("id_transaksi"),rs.getString("nama_customer"),rs.getString("tanggal"),rs.getDouble("total_harga")
            });
            lblStatus.setText("Total transaksi: "+modelRiwayat.getRowCount());
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal tampil riwayat: "+e.getMessage());}
    }

    void resetForm(){
        modelKeranjang.setRowCount(0); total=0; txtTotal.setText("0");
        txtIdBarang.setText("");txtNamaBarang.setText("");txtHarga.setText("");txtJumlah.setText("1");txtSubtotal.setText("0");
        generateIdTransaksi();
    }

    private void initComponents(){
        txtIdTransaksi=new JTextField(10);txtIdTransaksi.setEditable(false);
        txtTanggal=new JTextField(10); cmbCustomer=new JComboBox<>();
        txtIdBarang=new JTextField(10); txtNamaBarang=new JTextField(15);txtNamaBarang.setEditable(false);
        txtHarga=new JTextField(10);txtHarga.setEditable(false);
        txtJumlah=new JTextField(5);txtJumlah.setText("1");
        txtSubtotal=new JTextField(10);txtSubtotal.setEditable(false);
        txtTotal=new JTextField(12);txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Segoe UI",Font.BOLD,14));
        btnTambah=new JButton("Tambah");btnHapusItem=new JButton("Hapus Item");
        btnSimpan=new JButton("Simpan Transaksi");btnBatal=new JButton("Batal");
        btnTampilSemua=new JButton("Refresh");lblStatus=new JLabel("Total transaksi: 0");
        btnSimpan.setBackground(new Color(39,174,96));btnSimpan.setForeground(Color.WHITE);
        btnTambah.setBackground(new Color(41,128,185));btnTambah.setForeground(Color.WHITE);
        btnHapusItem.setBackground(new Color(231,76,60));btnHapusItem.setForeground(Color.WHITE);

        JPanel panelHeader=new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        panelHeader.setBorder(new TitledBorder("Header Transaksi"));
        panelHeader.add(new JLabel("ID Transaksi:"));panelHeader.add(txtIdTransaksi);
        panelHeader.add(new JLabel("Tanggal:"));panelHeader.add(txtTanggal);
        panelHeader.add(new JLabel("Customer:"));panelHeader.add(cmbCustomer);

        JPanel panelBarang=new JPanel(new FlowLayout(FlowLayout.LEFT,8,5));
        panelBarang.setBorder(new TitledBorder("Input Barang"));
        panelBarang.add(new JLabel("ID Barang:"));panelBarang.add(txtIdBarang);
        JButton btnCariBarang=new JButton("Cari Barang");
        btnCariBarang.addActionListener(e->cariBarang());
        panelBarang.add(btnCariBarang);
        panelBarang.add(new JLabel("Nama:"));panelBarang.add(txtNamaBarang);
        panelBarang.add(new JLabel("Harga:"));panelBarang.add(txtHarga);
        panelBarang.add(new JLabel("Jumlah:"));panelBarang.add(txtJumlah);
        panelBarang.add(new JLabel("Subtotal:"));panelBarang.add(txtSubtotal);
        panelBarang.add(btnTambah);

        tblKeranjang=new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID Barang","Nama Barang","Harga","Jumlah","Subtotal"}));
        modelKeranjang=(DefaultTableModel)tblKeranjang.getModel();tblKeranjang.setRowHeight(25);

        JPanel panelTotal=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5));
        panelTotal.add(new JLabel("TOTAL:"));panelTotal.add(txtTotal);
        panelTotal.add(btnHapusItem);panelTotal.add(btnSimpan);panelTotal.add(btnBatal);

        JPanel panelKeranjang=new JPanel(new BorderLayout());
        panelKeranjang.setBorder(new TitledBorder("Keranjang Belanja"));
        panelKeranjang.add(new JScrollPane(tblKeranjang),BorderLayout.CENTER);
        panelKeranjang.add(panelTotal,BorderLayout.SOUTH);

        tblRiwayat=new JTable(new DefaultTableModel(new Object[][]{},new String[]{"ID Transaksi","Customer","Tanggal","Total"}));
        modelRiwayat=(DefaultTableModel)tblRiwayat.getModel();tblRiwayat.setRowHeight(25);

        JPanel searchPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        searchPanel.add(btnTampilSemua);searchPanel.add(lblStatus);

        JPanel panelRiwayat=new JPanel(new BorderLayout());
        panelRiwayat.setBorder(new TitledBorder("Riwayat Transaksi"));
        panelRiwayat.add(searchPanel,BorderLayout.NORTH);
        panelRiwayat.add(new JScrollPane(tblRiwayat),BorderLayout.CENTER);

        JPanel panelAtas=new JPanel(new BorderLayout());
        panelAtas.add(panelHeader,BorderLayout.NORTH);
        panelAtas.add(panelBarang,BorderLayout.CENTER);

        JSplitPane splitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,panelKeranjang,panelRiwayat);
        splitPane.setDividerLocation(250);

        setLayout(new BorderLayout(5,5));
        add(panelAtas,BorderLayout.NORTH);add(splitPane,BorderLayout.CENTER);

        btnTambah.addActionListener(e->tambahKeKeranjang());
        btnHapusItem.addActionListener(e->hapusItem());
        btnSimpan.addActionListener(e->simpanTransaksi());
        btnBatal.addActionListener(e->resetForm());
        btnTampilSemua.addActionListener(e->tampilRiwayat());
        txtJumlah.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){hitungSubtotal();}});
    }
}

// ==================== MainFrame ====================
public class Penjualan extends JFrame {
    JPanel panelKonten; CardLayout cardLayout;

    public Penjualan(){
        setTitle("Aplikasi Penjualan Barang");
        setSize(1100,680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar=new JMenuBar();
        JMenu menuMaster=new JMenu("Master Data");
        JMenuItem mBarang=new JMenuItem("Data Barang");
        JMenuItem mCustomer=new JMenuItem("Data Customer");
        JMenuItem mSupplier=new JMenuItem("Data Supplier");
        menuMaster.add(mBarang);menuMaster.add(mCustomer);menuMaster.add(mSupplier);

        JMenu menuTransaksi=new JMenu("Transaksi");
        JMenuItem mPenjualan=new JMenuItem("Penjualan Barang");
        menuTransaksi.add(mPenjualan);

        JMenu menuLaporan=new JMenu("Laporan");
        JMenuItem mLapTransaksi=new JMenuItem("Laporan Transaksi");
        JMenuItem mLapInventory=new JMenuItem("Laporan Inventory");
        menuLaporan.add(mLapTransaksi);menuLaporan.add(mLapInventory);

        JMenu menuKeluar=new JMenu("Keluar");
        JMenuItem mKeluar=new JMenuItem("Keluar Aplikasi");
        menuKeluar.add(mKeluar);

        menuBar.add(menuMaster);menuBar.add(menuTransaksi);menuBar.add(menuLaporan);menuBar.add(menuKeluar);
        setJMenuBar(menuBar);

        JLabel lblHeader=new JLabel("  Selamat Datang di Aplikasi Penjualan Barang",SwingConstants.LEFT);
        lblHeader.setFont(new Font("Segoe UI",Font.BOLD,18));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBackground(new Color(41,128,185));
        lblHeader.setOpaque(true);
        lblHeader.setPreferredSize(new Dimension(0,50));

        cardLayout=new CardLayout();
        panelKonten=new JPanel(cardLayout);

        JPanel panelWelcome=new JPanel(new BorderLayout());
        panelWelcome.setBackground(new Color(245,245,245));
        JLabel lblWelcome=new JLabel("Pilih menu di atas untuk mulai",SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI",Font.PLAIN,16));
        lblWelcome.setForeground(Color.GRAY);
        panelWelcome.add(lblWelcome,BorderLayout.CENTER);

        BarangForm    panelBarang    = new BarangForm();
        CustomerForm  panelCustomer  = new CustomerForm();
        SupplierForm  panelSupplier  = new SupplierForm();
        TransaksiForm panelTransaksi = new TransaksiForm();
        LaporanForm   panelLapTrx   = new LaporanForm("transaksi");
        LaporanForm   panelLapInv   = new LaporanForm("inventory");

        panelKonten.add(panelWelcome,  "welcome");
        panelKonten.add(panelBarang,   "barang");
        panelKonten.add(panelCustomer, "customer");
        panelKonten.add(panelSupplier, "supplier");
        panelKonten.add(panelTransaksi,"transaksi");
        panelKonten.add(panelLapTrx,  "lapTransaksi");
        panelKonten.add(panelLapInv,  "lapInventory");
        cardLayout.show(panelKonten,"welcome");

        JPanel panelUtama=new JPanel(new BorderLayout());
        panelUtama.add(lblHeader,BorderLayout.NORTH);
        panelUtama.add(panelKonten,BorderLayout.CENTER);
        add(panelUtama);

        mBarang.addActionListener(e->{panelBarang.tampilData();cardLayout.show(panelKonten,"barang");lblHeader.setText("  Master Data  ›  Data Barang");});
        mCustomer.addActionListener(e->{panelCustomer.tampilData();cardLayout.show(panelKonten,"customer");lblHeader.setText("  Master Data  ›  Data Customer");});
        mSupplier.addActionListener(e->{panelSupplier.tampilData();cardLayout.show(panelKonten,"supplier");lblHeader.setText("  Master Data  ›  Data Supplier");});
        mPenjualan.addActionListener(e->{panelTransaksi.tampilRiwayat();panelTransaksi.generateIdTransaksi();cardLayout.show(panelKonten,"transaksi");lblHeader.setText("  Transaksi  ›  Penjualan Barang");});
        mLapTransaksi.addActionListener(e->{panelLapTrx.tampilData("");cardLayout.show(panelKonten,"lapTransaksi");lblHeader.setText("  Laporan  ›  Laporan Transaksi");});
        mLapInventory.addActionListener(e->{panelLapInv.tampilData("");cardLayout.show(panelKonten,"lapInventory");lblHeader.setText("  Laporan  ›  Laporan Inventory");});
        mKeluar.addActionListener(e->{
            if(JOptionPane.showConfirmDialog(this,"Yakin ingin keluar?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) System.exit(0);
        });
    }

    public static void main(String[] args){
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
        SwingUtilities.invokeLater(()->{
            try{
                DBHelper.koneksi();
                new Penjualan().setVisible(true);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Koneksi database gagal!\nPastikan XAMPP MySQL sudah aktif.\n\nError: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
