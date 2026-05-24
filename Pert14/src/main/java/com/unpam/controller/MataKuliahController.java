package com.unpam.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.MataKuliah;
import com.unpam.view.MainForm;

@WebServlet(name = "MataKuliahController", urlPatterns = {"/MataKuliah"})
public class MataKuliahController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);
        String userName = "";
        try { userName = session.getAttribute("userName").toString(); } catch (Exception e) {}
        if (userName == null || userName.isEmpty()) {
            response.sendRedirect("LoginController");
            return;
        }

        StringBuilder konten = new StringBuilder();
        String aksi = request.getParameter("aksi");

        if ("simpan".equals(aksi)) {
            MataKuliah mk = new MataKuliah();
            mk.setKodeMataKuliah(request.getParameter("kodeMataKuliah"));
            mk.setNamaMataKuliah(request.getParameter("namaMataKuliah"));
            try { mk.setJumlahSks(Integer.parseInt(request.getParameter("jumlahSks"))); } catch (Exception e) {}

            if (mk.simpan()) {
                konten.append("<p style='color:green;font-weight:bold;'>Data mata kuliah berhasil disimpan!</p>");
            } else {
                konten.append("<p style='color:red;'>Gagal: ").append(mk.getPesan()).append("</p>");
            }
        }

        // Form tambah (di atas)
        konten.append("<h2>Tambah Mata Kuliah</h2>");
        konten.append("<form method='post' action='MataKuliah'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<table>");
        konten.append("<tr><td>Kode Mata Kuliah</td><td><input type='text' name='kodeMataKuliah'/></td></tr>");
        konten.append("<tr><td>Nama Mata Kuliah</td><td><input type='text' name='namaMataKuliah'/></td></tr>");
        konten.append("<tr><td>Jumlah SKS</td><td><input type='text' name='jumlahSks'/></td></tr>");
        konten.append("<tr><td colspan='2' align='center'><input type='submit' value='Simpan'/></td></tr>");
        konten.append("</table></form><br>");

        // Tabel daftar (di bawah)
        MataKuliah mk = new MataKuliah();
        List<MataKuliah> list = mk.getList();

        konten.append("<h2>Daftar Mata Kuliah</h2>");
        konten.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse;width:90%;'>");
        konten.append("<tr style='background:#534AB7;color:#fff;'>");
        konten.append("<th>No</th><th>Kode</th><th>Nama Mata Kuliah</th><th>SKS</th>");
        konten.append("</tr>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='4' align='center'>Belum ada data</td></tr>");
        } else {
            int no = 1;
            for (MataKuliah m : list) {
                String bg = (no % 2 == 0) ? "#f5f5f5" : "#ffffff";
                konten.append("<tr style='background:").append(bg).append(";'>");
                konten.append("<td align='center'>").append(no++).append("</td>");
                konten.append("<td>").append(m.getKodeMataKuliah()).append("</td>");
                konten.append("<td>").append(m.getNamaMataKuliah()).append("</td>");
                konten.append("<td align='center'>").append(m.getJumlahSks()).append("</td>");
                konten.append("</tr>");
            }
        }
        konten.append("</table>");

        new MainForm().tampilkan(konten.toString(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }
}
