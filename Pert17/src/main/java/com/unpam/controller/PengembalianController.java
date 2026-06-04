package com.unpam.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Transaksi;
import com.unpam.view.MainForm;

@WebServlet(name = "PengembalianController", urlPatterns = {"/Pengembalian"})
public class PengembalianController extends HttpServlet {

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

        if ("proses".equals(aksi)) {
            try {
                int idTransaksi = Integer.parseInt(request.getParameter("idTransaksi"));
                Transaksi tr = new Transaksi();
                berhasil = tr.prosesKembali(idTransaksi);
                pesan = berhasil ? "Pengembalian mobil ID #" + idTransaksi + " berhasil diproses!" : "Gagal: " + tr.getPesan();
            } catch (Exception e) {
                pesan = "ID Transaksi tidak valid.";
            }
        }

        Transaksi tr = new Transaksi();
        List<Transaksi> list = tr.getList();

        konten.append("<h1 class='page-title'>Pengembalian Mobil</h1>");
        konten.append("<p class='page-subtitle'>Proses pengembalian kendaraan dari customer</p>");

        if (!pesan.isEmpty()) {
            konten.append("<div class='alert ").append(berhasil ? "alert-success" : "alert-danger").append("'>");
            konten.append(berhasil ? "✅ " : "⚠️ ").append(pesan).append("</div>");
        }

        // Form proses kembali
        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>🔄 Proses Pengembalian</div>");
        konten.append("<form method='post' action='Pengembalian'>");
        konten.append("<input type='hidden' name='aksi' value='proses'/>");
        konten.append("<div class='form-grid'>");
        konten.append("<div class='form-group'><label>ID Transaksi</label><input type='number' name='idTransaksi' min='1' placeholder='Masukkan ID transaksi aktif'/></div>");
        konten.append("</div>");
        konten.append("<div class='form-actions'><button type='submit' class='btn btn-primary'>🔄 Proses Pengembalian</button></div>");
        konten.append("</form></div>");

        // Transaksi aktif
        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>⏳ Transaksi Aktif (Belum Dikembalikan)</div>");
        konten.append("<div class='table-wrap'>");
        konten.append("<table class='data-table'><thead><tr>");
        konten.append("<th>ID Tr</th><th>Customer</th><th>Mobil</th><th>Plat Nomor</th><th>Tgl Sewa</th><th>Tgl Kembali</th><th>Total Bayar</th>");
        konten.append("</tr></thead><tbody>");

        boolean adaAktif = false;
        for (Transaksi t : list) {
            if ("aktif".equals(t.getStatus())) {
                adaAktif = true;
                konten.append("<tr>");
                konten.append("<td><b>#").append(t.getIdTransaksi()).append("</b></td>");
                konten.append("<td>").append(t.getNamaCustomer()).append("</td>");
                konten.append("<td>").append(t.getMerkMobil()).append(" ").append(t.getTipe()).append("</td>");
                konten.append("<td>").append(t.getPlatNomor()).append("</td>");
                konten.append("<td>").append(t.getTanggalSewa()).append("</td>");
                konten.append("<td>").append(t.getTanggalKembali()).append("</td>");
                konten.append("<td><b>Rp ").append(String.format("%,.0f", t.getTotalBayar())).append("</b></td>");
                konten.append("</tr>");
            }
        }
        if (!adaAktif) {
            konten.append("<tr><td colspan='7' style='text-align:center;color:var(--text-light);padding:24px;'>Tidak ada transaksi aktif</td></tr>");
        }
        konten.append("</tbody></table></div></div>");

        new MainForm().tampilkan(konten.toString(), request, response, "kembali");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
}
