package com.unpam.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Nilai;
import com.unpam.view.MainForm;

@WebServlet(name = "NilaiController", urlPatterns = {"/Nilai"})
public class NilaiController extends HttpServlet {

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
            Nilai nilai = new Nilai();
            nilai.setNim(request.getParameter("nim"));
            nilai.setKodeMataKuliah(request.getParameter("kodeMataKuliah"));
            try { nilai.setTugas(Double.parseDouble(request.getParameter("tugas"))); } catch (Exception e) {}
            try { nilai.setUts(Double.parseDouble(request.getParameter("uts"))); } catch (Exception e) {}
            try { nilai.setUas(Double.parseDouble(request.getParameter("uas"))); } catch (Exception e) {}

            if (nilai.simpan()) {
                konten.append("<p style='color:green;font-weight:bold;'>Data nilai berhasil disimpan!</p>");
            } else {
                konten.append("<p style='color:red;'>Gagal: ").append(nilai.getPesan()).append("</p>");
            }
        }

        // Form input saja, tanpa tabel dan tanpa link cetak
        konten.append("<h2>Input Nilai</h2>");
        konten.append("<form method='post' action='Nilai'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<table>");
        konten.append("<tr><td>NIM Mahasiswa</td><td><input type='text' name='nim'/></td></tr>");
        konten.append("<tr><td>Kode Mata Kuliah</td><td><input type='text' name='kodeMataKuliah'/></td></tr>");
        konten.append("<tr><td>Nilai Tugas</td><td><input type='text' name='tugas'/></td></tr>");
        konten.append("<tr><td>Nilai UTS</td><td><input type='text' name='uts'/></td></tr>");
        konten.append("<tr><td>Nilai UAS</td><td><input type='text' name='uas'/></td></tr>");
        konten.append("<tr><td colspan='2' align='center'><input type='submit' value='Simpan'/></td></tr>");
        konten.append("</table></form>");

        new MainForm().tampilkan(konten.toString(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }
}
