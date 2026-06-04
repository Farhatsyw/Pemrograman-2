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
        tampilkan(konten, request, response, "");
    }

    public void tampilkan(String konten, HttpServletRequest request,
            HttpServletResponse response, String activeMenu) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);
        String userName = "";
        try { userName = session.getAttribute("userName").toString(); } catch (Exception ex) {}

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='id'><head>");
            out.println("<meta charset='UTF-8'/>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css'/>");
            out.println("<title>Rent Car - UNPAM</title>");
            out.println("</head><body>");

            // TOP BAR
            out.println("<div class='topbar'>");
            out.println("  <div class='topbar-brand'><span class='icon'>🚗</span> Rent Car UNPAM</div>");
            out.println("  <div class='topbar-right'>");
            out.println("    <span class='topbar-user'>👤 " + userName + "</span>");
            out.println("    <a href='LogoutController' class='topbar-logout'>Keluar →</a>");
            out.println("  </div>");
            out.println("</div>");

            // LAYOUT
            out.println("<div class='layout'>");

            // SIDEBAR
            out.println("<nav class='sidebar'>");

            out.println("<div class='sidebar-label'>Master Data</div>");
            out.println("<div class='sidebar-section'>");
            out.println("  <a href='Mobil' class='" + ("mobil".equals(activeMenu) ? "active" : "") + "'><span class='nav-icon'>🚙</span> Data Mobil</a>");
            out.println("  <a href='Customer' class='" + ("customer".equals(activeMenu) ? "active" : "") + "'><span class='nav-icon'>👥</span> Data Customer</a>");
            out.println("</div>");

            out.println("<div class='sidebar-divider'></div>");

            out.println("<div class='sidebar-label'>Transaksi</div>");
            out.println("<div class='sidebar-section'>");
            out.println("  <a href='Sewa' class='" + ("sewa".equals(activeMenu) ? "active" : "") + "'><span class='nav-icon'>📋</span> Penyewaan</a>");
            out.println("  <a href='Pengembalian' class='" + ("kembali".equals(activeMenu) ? "active" : "") + "'><span class='nav-icon'>🔄</span> Pengembalian</a>");
            out.println("</div>");

            out.println("<div class='sidebar-divider'></div>");

            out.println("<div class='sidebar-label'>Laporan</div>");
            out.println("<div class='sidebar-section'>");
            out.println("  <a href='LaporanTransaksi' class='" + ("laporan".equals(activeMenu) ? "active" : "") + "'><span class='nav-icon'>📄</span> Laporan Transaksi</a>");
            out.println("</div>");

            out.println("</nav>");

            // MAIN
            out.println("<main class='main'>");
            out.println(konten);
            out.println("</main>");

            out.println("</div>"); // layout

            // FOOTER
            out.println("<footer class='footer'>Copyright &copy; 2024 Universitas Pamulang &mdash; Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan</footer>");
            out.println("</body></html>");
        }
    }
}
