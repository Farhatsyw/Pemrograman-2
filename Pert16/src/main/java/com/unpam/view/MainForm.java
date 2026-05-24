package com.unpam.view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainForm", urlPatterns = {"/MainForm"})
public class MainForm extends HttpServlet {

    public void tampilkan(String konten, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);
        String userName = "";
        String menu = "";
        String topMenu = "";

        try { userName = session.getAttribute("userName").toString(); } catch (Exception ex) {}

        if (userName != null && !userName.isEmpty()) {
            menu = "<br><b>Master Data</b><br>"
                    + "<a href='Mahasiswa'>Mahasiswa</a><br>"
                    + "<a href='MataKuliah'>Mata Kuliah</a><br><br>"
                    + "<b>Transaksi</b><br>"
                    + "<a href='Nilai'>Nilai</a><br><br>"
                    + "<b>Laporan</b><br>"
                    + "<a href='LaporanNilai'>Nilai</a><br><br>"
                    + "<a href='LogoutController'>Logout</a><br><br>";

            topMenu = "<nav><ul>"
                    + "<li><a href='.'>Home</a></li>"
                    + "<li><a href='#'>Master Data</a><ul>"
                    + "<li><a href='Mahasiswa'>Mahasiswa</a></li>"
                    + "<li><a href='MataKuliah'>Mata Kuliah</a></li>"
                    + "</ul></li>"
                    + "<li><a href='#'>Transaksi</a><ul>"
                    + "<li><a href='Nilai'>Nilai</a></li>"
                    + "</ul></li>"
                    + "<li><a href='#'>Laporan</a><ul>"
                    + "<li><a href='LaporanNilai'>Nilai</a></li>"
                    + "</ul></li>"
                    + "<li><a href='LogoutController'>Logout (" + userName + ")</a></li>"
                    + "</ul></nav>";
        }

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css'/>");
            out.println("<title>Informasi Nilai Mahasiswa</title>");
            out.println("</head><body bgcolor='#808080'>");
            out.println("<center>");
            out.println("<table width='80%' bgcolor='#eeeeee'>");
            out.println("<tr><td colspan='2' align='center'>");
            out.println("<br><h2 style='margin-bottom:0px;margin-top:0px;'>Informasi Nilai Mahasiswa</h2>");
            out.println("<h1 style='margin-bottom:0px;margin-top:0px;'>UNIVERSITAS PAMULANG</h1>");
            out.println("<h4 style='margin-bottom:0px;margin-top:0px;'>Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan, Banten</h4>");
            out.println("<br></td></tr>");
            out.println("<tr height='400'>");
            out.println("<td width='200' align='center' valign='top' bgcolor='#eeffee'><br>");
            out.println("<div id='menu'>" + menu + "</div>");
            out.println("</td>");
            out.println("<td align='center' valign='top' bgcolor='#ffffff'>");
            out.println(topMenu);
            out.println("<br>" + konten);
            out.println("</td></tr>");
            out.println("<tr><td colspan='2' align='center' bgcolor='#eeeeff'>");
            out.println("<small>Copyright &copy; 2016 Universitas Pamulang<br>");
            out.println("Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan, Banten</small>");
            out.println("</td></tr></table></center></body></html>");
        }
    }
}
