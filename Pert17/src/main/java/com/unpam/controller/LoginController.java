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

        String aksi = request.getParameter("aksi");
        boolean loginGagal = false;

        if ("login".equals(aksi)) {
            String usernameInput = request.getParameter("username");
            String passwordInput = request.getParameter("password");
            try {
                Enkripsi enkripsi = new Enkripsi();
                String passwordMD5 = enkripsi.hashMD5(passwordInput);
                Admin admin = new Admin();
                if (admin.cekLogin(usernameInput, passwordMD5)) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userName", admin.getNama());
                    response.sendRedirect(".");
                    return;
                } else {
                    loginGagal = true;
                }
            } catch (Exception ex) {
                loginGagal = true;
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='id'><head>");
            out.println("<meta charset='UTF-8'/>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'/>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css'/>");
            out.println("<title>Login - Rent Car UNPAM</title></head><body>");

            out.println("<div class='login-wrap'>");
            out.println("<div class='login-card'>");
            out.println("  <div class='login-logo'>");
            out.println("    <div class='login-icon'>🚗</div>");
            out.println("    <h1>Rent Car</h1>");
            out.println("    <p>Universitas Pamulang</p>");
            out.println("  </div>");

            if (loginGagal) {
                out.println("<div class='alert alert-danger'>⚠️ Username atau password salah!</div>");
            }

            out.println("  <form method='post' action='LoginController'>");
            out.println("    <input type='hidden' name='aksi' value='login'/>");
            out.println("    <div class='form-group'>");
            out.println("      <label>Username</label>");
            out.println("      <input type='text' name='username' placeholder='Masukkan username' autocomplete='username'/>");
            out.println("    </div>");
            out.println("    <div class='form-group'>");
            out.println("      <label>Password</label>");
            out.println("      <input type='password' name='password' placeholder='Masukkan password' autocomplete='current-password'/>");
            out.println("    </div>");
            out.println("    <button type='submit' class='btn btn-primary'>Masuk</button>");
            out.println("  </form>");
            out.println("</div></div></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { processRequest(request, response); }
}
