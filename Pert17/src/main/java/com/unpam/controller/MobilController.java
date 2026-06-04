package com.unpam.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Mobil;
import com.unpam.view.MainForm;

@WebServlet(name = "MobilController", urlPatterns = {"/Mobil"})
public class MobilController extends HttpServlet {

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
            Mobil mobil = new Mobil();
            mobil.setPlatNomor(request.getParameter("platNomor"));
            mobil.setMerk(request.getParameter("merk"));
            mobil.setTipe(request.getParameter("tipe"));
            mobil.setWarna(request.getParameter("warna"));
            try { mobil.setTahun(Integer.parseInt(request.getParameter("tahun"))); } catch (Exception e) {}
            try { mobil.setHargaSewa(Double.parseDouble(request.getParameter("hargaSewa"))); } catch (Exception e) {}
            berhasil = mobil.simpan();
            pesan = berhasil ? "Data mobil berhasil disimpan!" : "Gagal: " + mobil.getPesan();
        }

        konten.append("<h1 class='page-title'>Data Mobil</h1>");
        konten.append("<p class='page-subtitle'>Kelola master data armada kendaraan</p>");

        if (!pesan.isEmpty()) {
            konten.append("<div class='alert ").append(berhasil ? "alert-success" : "alert-danger").append("'>");
            konten.append(berhasil ? "✅ " : "⚠️ ").append(pesan).append("</div>");
        }

        // Form
        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>🚙 Tambah Data Mobil</div>");
        konten.append("<form method='post' action='Mobil'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<div class='form-grid'>");
        konten.append("<div class='form-group'><label>Plat Nomor</label><input type='text' name='platNomor' placeholder='Contoh: B 1234 ABC'/></div>");
        konten.append("<div class='form-group'><label>Merk</label><input type='text' name='merk' placeholder='Contoh: Toyota'/></div>");
        konten.append("<div class='form-group'><label>Tipe</label><input type='text' name='tipe' placeholder='Contoh: Avanza'/></div>");
        konten.append("<div class='form-group'><label>Warna</label><input type='text' name='warna' placeholder='Contoh: Putih'/></div>");
        konten.append("<div class='form-group'><label>Tahun</label><input type='number' name='tahun' placeholder='Contoh: 2022'/></div>");
        konten.append("<div class='form-group'><label>Harga Sewa / Hari (Rp)</label><input type='number' name='hargaSewa' placeholder='Contoh: 350000'/></div>");
        konten.append("</div>");
        konten.append("<div class='form-actions'><button type='submit' class='btn btn-primary'>💾 Simpan</button></div>");
        konten.append("</form></div>");

        // Tabel
        Mobil mobil = new Mobil();
        List<Mobil> list = mobil.getList();

        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>📋 Daftar Mobil <span style='margin-left:auto;font-size:0.8rem;color:var(--text-light);font-weight:400;'>").append(list.size()).append(" kendaraan</span></div>");
        konten.append("<div class='table-wrap'>");
        konten.append("<table class='data-table'><thead><tr>");
        konten.append("<th>No</th><th>Plat Nomor</th><th>Merk</th><th>Tipe</th><th>Warna</th><th>Tahun</th><th>Harga Sewa/Hari</th><th>Status</th>");
        konten.append("</tr></thead><tbody>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='8' style='text-align:center;color:var(--text-light);padding:24px;'>Belum ada data mobil</td></tr>");
        } else {
            int no = 1;
            for (Mobil m : list) {
                boolean tersedia = "tersedia".equals(m.getStatus());
                konten.append("<tr>");
                konten.append("<td style='color:var(--text-light);'>").append(no++).append("</td>");
                konten.append("<td><b>").append(m.getPlatNomor()).append("</b></td>");
                konten.append("<td>").append(m.getMerk()).append("</td>");
                konten.append("<td>").append(m.getTipe()).append("</td>");
                konten.append("<td>").append(m.getWarna()).append("</td>");
                konten.append("<td>").append(m.getTahun()).append("</td>");
                konten.append("<td>Rp ").append(String.format("%,.0f", m.getHargaSewa())).append("</td>");
                konten.append("<td><span class='badge ").append(tersedia ? "badge-success" : "badge-danger").append("'>")
                      .append(tersedia ? "✓ Tersedia" : "✗ Disewa").append("</span></td>");
                konten.append("</tr>");
            }
        }
        konten.append("</tbody></table></div></div>");

        new MainForm().tampilkan(konten.toString(), request, response, "mobil");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
}
