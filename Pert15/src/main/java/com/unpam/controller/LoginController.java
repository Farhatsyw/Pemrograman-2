package com.unpam.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Admin;
import com.unpam.model.Enkripsi;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);

        // Kalau sudah login, langsung redirect ke index
        String userName = "";
        try { userName = session.getAttribute("userName").toString(); } catch (Exception e) {}
        if (userName != null && !userName.isEmpty()) {
            response.sendRedirect(".");
            return;
        }

        String pesan = "";
        String aksi = request.getParameter("aksi");

        if ("login".equals(aksi)) {
            String username = request.getParameter("username");
            String pass = request.getParameter("password");

            Admin admin = new Admin();
            Enkripsi enkripsi = new Enkripsi();

            try {
                String passMD5 = enkripsi.hashMD5(pass);
                if (admin.cekLogin(username, passMD5)) {
                    session.setAttribute("userName", admin.getNama());
                    session.setAttribute("userLogin", admin.getUsername());
                    response.sendRedirect(".");
                    return;
                } else {
                    pesan = "<p style='color:red;'>Username atau password salah!</p>";
                }
            } catch (Exception ex) {
                pesan = "<p style='color:red;'>Error: " + ex.getMessage() + "</p>";
            }
        }

        // Tampilkan form login
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css'/>");
            out.println("<title>Login - Informasi Nilai Mahasiswa</title>");
            out.println("<style>");
            out.println("body{background:#808080;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;font-family:Segoe UI,sans-serif;}");
            out.println(".login-box{background:#fff;padding:40px;border-radius:8px;width:320px;box-shadow:0 4px 20px rgba(0,0,0,0.3);}");
            out.println(".login-box h2{text-align:center;margin-bottom:5px;color:#1a1a2e;}");
            out.println(".login-box h3{text-align:center;margin-bottom:20px;color:#555;font-weight:normal;font-size:13px;}");
            out.println("table{width:100%;}");
            out.println("td{padding:6px 0;}");
            out.println("input[type=text],input[type=password]{width:100%;padding:8px;border:1px solid #ccc;border-radius:4px;box-sizing:border-box;}");
            out.println("input[type=submit]{width:100%;padding:10px;background:#534AB7;color:#fff;border:none;border-radius:4px;cursor:pointer;font-size:14px;font-weight:bold;margin-top:10px;}");
            out.println("input[type=submit]:hover{background:#3C3489;}");
            out.println("</style></head><body>");
            out.println("<div class='login-box'>");
            out.println("<h2>UNIVERSITAS PAMULANG</h2>");
            out.println("<h3>Informasi Nilai Mahasiswa</h3>");
            out.println(pesan);
            out.println("<form method='post' action='LoginController'>");
            out.println("<input type='hidden' name='aksi' value='login'/>");
            out.println("<table>");
            out.println("<tr><td>Username</td></tr>");
            out.println("<tr><td><input type='text' name='username' placeholder='Masukkan username'/></td></tr>");
            out.println("<tr><td>Password</td></tr>");
            out.println("<tr><td><input type='password' name='password' placeholder='Masukkan password'/></td></tr>");
            out.println("<tr><td><input type='submit' value='Login'/></td></tr>");
            out.println("</table></form>");
            out.println("</div></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }
}
