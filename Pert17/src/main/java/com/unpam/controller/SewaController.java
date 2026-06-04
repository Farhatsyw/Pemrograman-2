package com.unpam.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Customer;
import com.unpam.model.Mobil;
import com.unpam.model.Transaksi;
import com.unpam.view.MainForm;

@WebServlet(name = "SewaController", urlPatterns = {"/Sewa"})
public class SewaController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);
        String userName = "";
        try { userName = session.getAttribute("userName").toString(); } catch (Exception e) {}
        if (userName == null || userName.isEmpty()) { response.sendRedirect("LoginController"); return; }

        StringBuilder konten = new StringBuilder();
        String aksi = request.getParameter("aksi");
        String pesan = "";
        boolean berhasil = false;

        if ("simpan".equals(aksi)) {
            Transaksi tr = new Transaksi();
            tr.setIdCustomer(request.getParameter("idCustomer"));
            tr.setPlatNomor(request.getParameter("platNomor"));
            try { tr.setTanggalSewa(Date.valueOf(request.getParameter("tanggalSewa"))); } catch (Exception e) {}
            try { tr.setTanggalKembali(Date.valueOf(request.getParameter("tanggalKembali"))); } catch (Exception e) {}
            try { tr.setLamaSewa(Integer.parseInt(request.getParameter("lamaSewa"))); } catch (Exception e) {}
            berhasil = tr.simpanSewa();
            pesan = berhasil ? "Transaksi penyewaan berhasil disimpan!" : "Gagal: " + tr.getPesan();
        }

        Customer customer = new Customer();
        List<Customer> listCustomer = customer.getList();
        Mobil mobil = new Mobil();
        List<Mobil> listMobil = mobil.getList();

        konten.append("<h1 class='page-title'>Penyewaan Mobil</h1>");
        konten.append("<p class='page-subtitle'>Catat transaksi penyewaan kendaraan baru</p>");

        if (!pesan.isEmpty()) {
            konten.append("<div class='alert ").append(berhasil ? "alert-success" : "alert-danger").append("'>");
            konten.append(berhasil ? "✅ " : "⚠️ ").append(pesan).append("</div>");
        }

        // Form
        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>📋 Form Penyewaan</div>");
        konten.append("<form method='post' action='Sewa'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<div class='form-grid'>");

        // Dropdown customer
        konten.append("<div class='form-group'><label>Customer</label><select name='idCustomer'>");
        konten.append("<option value=''>-- Pilih Customer --</option>");
        for (Customer c : listCustomer) {
            konten.append("<option value='").append(c.getIdCustomer()).append("'>")
                  .append(c.getIdCustomer()).append(" - ").append(c.getNama()).append("</option>");
        }
        konten.append("</select></div>");

        // Dropdown mobil tersedia
        konten.append("<div class='form-group'><label>Mobil (Tersedia)</label><select name='platNomor'>");
        konten.append("<option value=''>-- Pilih Mobil --</option>");
        for (Mobil m : listMobil) {
            if ("tersedia".equals(m.getStatus())) {
                konten.append("<option value='").append(m.getPlatNomor()).append("'>")
                      .append(m.getPlatNomor()).append(" - ").append(m.getMerk()).append(" ").append(m.getTipe())
                      .append(" | Rp ").append(String.format("%,.0f", m.getHargaSewa())).append("/hari</option>");
            }
        }
        konten.append("</select></div>");

        konten.append("<div class='form-group'><label>Tanggal Sewa</label><input type='date' name='tanggalSewa'/></div>");
        konten.append("<div class='form-group'><label>Tanggal Kembali</label><input type='date' name='tanggalKembali'/></div>");
        konten.append("<div class='form-group'><label>Lama Sewa (hari)</label><input type='number' name='lamaSewa' min='1' placeholder='Jumlah hari'/></div>");
        konten.append("</div>");
        konten.append("<div class='form-actions'><button type='submit' class='btn btn-primary'>💾 Simpan Transaksi</button></div>");
        konten.append("</form></div>");

        // Tabel semua transaksi
        Transaksi tr = new Transaksi();
        List<Transaksi> list = tr.getList();

        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>📑 Riwayat Transaksi</div>");
        konten.append("<div class='table-wrap'>");
        konten.append("<table class='data-table'><thead><tr>");
        konten.append("<th>No</th><th>ID Tr</th><th>Customer</th><th>Mobil</th><th>Tgl Sewa</th><th>Tgl Kembali</th><th>Lama</th><th>Total Bayar</th><th>Status</th>");
        konten.append("</tr></thead><tbody>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='9' style='text-align:center;color:var(--text-light);padding:24px;'>Belum ada transaksi</td></tr>");
        } else {
            int no = 1;
            for (Transaksi t : list) {
                boolean aktif = "aktif".equals(t.getStatus());
                konten.append("<tr>");
                konten.append("<td style='color:var(--text-light);'>").append(no++).append("</td>");
                konten.append("<td><b>#").append(t.getIdTransaksi()).append("</b></td>");
                konten.append("<td>").append(t.getNamaCustomer()).append("</td>");
                konten.append("<td>").append(t.getMerkMobil()).append(" ").append(t.getTipe())
                      .append("<br><small style='color:var(--text-light);'>").append(t.getPlatNomor()).append("</small></td>");
                konten.append("<td>").append(t.getTanggalSewa()).append("</td>");
                konten.append("<td>").append(t.getTanggalKembali()).append("</td>");
                konten.append("<td style='text-align:center;'>").append(t.getLamaSewa()).append(" hr</td>");
                konten.append("<td><b>Rp ").append(String.format("%,.0f", t.getTotalBayar())).append("</b></td>");
                konten.append("<td><span class='badge ").append(aktif ? "badge-info" : "badge-muted").append("'>")
                      .append(aktif ? "⏳ Aktif" : "✓ Selesai").append("</span></td>");
                konten.append("</tr>");
            }
        }
        konten.append("</tbody></table></div></div>");

        new MainForm().tampilkan(konten.toString(), request, response, "sewa");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
}
