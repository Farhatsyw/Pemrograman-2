package com.unpam.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Mahasiswa;
import com.unpam.model.Enkripsi;
import com.unpam.view.MainForm;

@WebServlet(name = "MahasiswaController", urlPatterns = {"/Mahasiswa"})
public class MahasiswaController extends HttpServlet {

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
            Mahasiswa mhs = new Mahasiswa();
            Enkripsi enkripsi = new Enkripsi();
            mhs.setNim(request.getParameter("nim"));
            mhs.setNama(request.getParameter("nama"));
            mhs.setKelas(request.getParameter("kelas"));
            try { mhs.setSemester(Integer.parseInt(request.getParameter("semester"))); } catch (Exception e) {}
            try { mhs.setPassword(enkripsi.hashMD5(request.getParameter("password"))); } catch (Exception e) {}

            if (mhs.simpan()) {
                konten.append("<p style='color:green;font-weight:bold;'>Data mahasiswa berhasil disimpan!</p>");
            } else {
                konten.append("<p style='color:red;'>Gagal: ").append(mhs.getPesan()).append("</p>");
            }
        }

        // Form tambah mahasiswa (di atas)
        konten.append("<h2>Tambah Mahasiswa</h2>");
        konten.append("<form method='post' action='Mahasiswa'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<table>");
        konten.append("<tr><td>NIM</td><td><input type='text' name='nim'/></td></tr>");
        konten.append("<tr><td>Nama</td><td><input type='text' name='nama'/></td></tr>");
        konten.append("<tr><td>Kelas</td><td><input type='text' name='kelas'/></td></tr>");
        konten.append("<tr><td>Semester</td><td><input type='text' name='semester'/></td></tr>");
        konten.append("<tr><td>Password</td><td><input type='password' name='password'/></td></tr>");
        konten.append("<tr><td colspan='2' align='center'><input type='submit' value='Simpan'/></td></tr>");
        konten.append("</table></form><br>");

        // Tabel daftar mahasiswa (di bawah)
        Mahasiswa mhs = new Mahasiswa();
        List<Mahasiswa> list = mhs.getList();

        konten.append("<h2>Daftar Mahasiswa</h2>");
        konten.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse;width:90%;'>");
        konten.append("<tr style='background:#534AB7;color:#fff;'>");
        konten.append("<th>No</th><th>NIM</th><th>Nama</th><th>Semester</th><th>Kelas</th>");
        konten.append("</tr>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='5' align='center'>Belum ada data</td></tr>");
        } else {
            int no = 1;
            for (Mahasiswa m : list) {
                String bg = (no % 2 == 0) ? "#f5f5f5" : "#ffffff";
                konten.append("<tr style='background:").append(bg).append(";'>");
                konten.append("<td align='center'>").append(no++).append("</td>");
                konten.append("<td>").append(m.getNim()).append("</td>");
                konten.append("<td>").append(m.getNama()).append("</td>");
                konten.append("<td align='center'>").append(m.getSemester()).append("</td>");
                konten.append("<td align='center'>").append(m.getKelas()).append("</td>");
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
