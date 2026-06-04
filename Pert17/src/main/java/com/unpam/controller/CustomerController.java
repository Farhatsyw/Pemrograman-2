package com.unpam.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Customer;
import com.unpam.view.MainForm;

@WebServlet(name = "CustomerController", urlPatterns = {"/Customer"})
public class CustomerController extends HttpServlet {

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
            Customer customer = new Customer();
            customer.setIdCustomer(request.getParameter("idCustomer"));
            customer.setNama(request.getParameter("nama"));
            customer.setAlamat(request.getParameter("alamat"));
            customer.setNoTelp(request.getParameter("noTelp"));
            customer.setNoKtp(request.getParameter("noKtp"));
            berhasil = customer.simpan();
            pesan = berhasil ? "Data customer berhasil disimpan!" : "Gagal: " + customer.getPesan();
        }

        konten.append("<h1 class='page-title'>Data Customer</h1>");
        konten.append("<p class='page-subtitle'>Kelola data pelanggan penyewaan mobil</p>");

        if (!pesan.isEmpty()) {
            konten.append("<div class='alert ").append(berhasil ? "alert-success" : "alert-danger").append("'>");
            konten.append(berhasil ? "✅ " : "⚠️ ").append(pesan).append("</div>");
        }

        // Form
        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>👤 Tambah Data Customer</div>");
        konten.append("<form method='post' action='Customer'>");
        konten.append("<input type='hidden' name='aksi' value='simpan'/>");
        konten.append("<div class='form-grid'>");
        konten.append("<div class='form-group'><label>ID Customer</label><input type='text' name='idCustomer' placeholder='Contoh: C004'/></div>");
        konten.append("<div class='form-group'><label>Nama Lengkap</label><input type='text' name='nama' placeholder='Nama customer'/></div>");
        konten.append("<div class='form-group'><label>No. Telepon</label><input type='text' name='noTelp' placeholder='Contoh: 081234567890'/></div>");
        konten.append("<div class='form-group'><label>No. KTP</label><input type='text' name='noKtp' placeholder='16 digit NIK'/></div>");
        konten.append("<div class='form-group full'><label>Alamat</label><input type='text' name='alamat' placeholder='Alamat lengkap'/></div>");
        konten.append("</div>");
        konten.append("<div class='form-actions'><button type='submit' class='btn btn-primary'>💾 Simpan</button></div>");
        konten.append("</form></div>");

        // Tabel
        Customer customer = new Customer();
        List<Customer> list = customer.getList();

        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>👥 Daftar Customer <span style='margin-left:auto;font-size:0.8rem;color:var(--text-light);font-weight:400;'>").append(list.size()).append(" customer</span></div>");
        konten.append("<div class='table-wrap'>");
        konten.append("<table class='data-table'><thead><tr>");
        konten.append("<th>No</th><th>ID</th><th>Nama</th><th>Alamat</th><th>No. Telepon</th><th>No. KTP</th>");
        konten.append("</tr></thead><tbody>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='6' style='text-align:center;color:var(--text-light);padding:24px;'>Belum ada data customer</td></tr>");
        } else {
            int no = 1;
            for (Customer c : list) {
                konten.append("<tr>");
                konten.append("<td style='color:var(--text-light);'>").append(no++).append("</td>");
                konten.append("<td><b>").append(c.getIdCustomer()).append("</b></td>");
                konten.append("<td>").append(c.getNama()).append("</td>");
                konten.append("<td style='color:var(--text-mid);'>").append(c.getAlamat()).append("</td>");
                konten.append("<td>").append(c.getNoTelp()).append("</td>");
                konten.append("<td style='color:var(--text-mid);font-size:0.82rem;'>").append(c.getNoKtp()).append("</td>");
                konten.append("</tr>");
            }
        }
        konten.append("</tbody></table></div></div>");

        new MainForm().tampilkan(konten.toString(), request, response, "customer");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
}
