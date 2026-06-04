package com.unpam.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import com.unpam.model.Transaksi;
import com.unpam.view.MainForm;

@WebServlet(name = "LaporanTransaksiController", urlPatterns = {"/LaporanTransaksi"})
public class LaporanTransaksiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        String userName = "";
        try { userName = session.getAttribute("userName").toString(); } catch (Exception e) {}
        if (userName == null || userName.isEmpty()) { response.sendRedirect("LoginController"); return; }

        String aksi = request.getParameter("aksi");

        if ("cetak".equals(aksi)) {
            Transaksi tr = new Transaksi();
            ResultSet rs = tr.getDataLaporan();
            try {
                String reportPath = getServletContext().getRealPath("/reports/TransaksiReport.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("judul", "Laporan Transaksi Penyewaan Mobil");
                JasperPrint jasperPrint = JasperFillManager.fillReport(
                        jasperReport, parameters, new JRResultSetDataSource(rs));

                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                JRExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.exportReport();

                byte[] pdfBytes = baos.toByteArray();
                OutputStream outStream = response.getOutputStream();
                try {
                    response.setHeader("Content-Disposition", "inline; filename=LaporanTransaksi.pdf");
                    response.setContentType("application/pdf");
                    response.setContentLength(pdfBytes.length);
                    outStream.write(pdfBytes, 0, pdfBytes.length);
                    outStream.flush();
                } finally {
                    outStream.close();
                }
                return;
            } catch (Exception ex) {
                StringBuilder err = new StringBuilder();
                err.append("<h1 class='page-title'>Laporan Transaksi</h1>");
                err.append("<div class='alert alert-danger'>⚠️ Error cetak PDF: ").append(ex.getMessage()).append("</div>");
                new MainForm().tampilkan(err.toString(), request, response, "laporan");
                return;
            }
        }

        // Tampilkan tabel + tombol cetak
        Transaksi tr = new Transaksi();
        List<Transaksi> list = tr.getList();

        // Hitung total
        double grandTotal = 0;
        int totalAktif = 0, totalSelesai = 0;
        for (Transaksi t : list) {
            grandTotal += t.getTotalBayar();
            if ("aktif".equals(t.getStatus())) totalAktif++;
            else totalSelesai++;
        }

        StringBuilder konten = new StringBuilder();
        konten.append("<h1 class='page-title'>Laporan Transaksi</h1>");
        konten.append("<p class='page-subtitle'>Rekap seluruh transaksi penyewaan mobil</p>");

        // Stats
        konten.append("<div class='stats-grid'>");
        konten.append("<div class='stat-card'><div class='stat-icon'>📋</div><div class='stat-info'><p>Total Transaksi</p><h3>").append(list.size()).append("</h3></div></div>");
        konten.append("<div class='stat-card'><div class='stat-icon'>⏳</div><div class='stat-info'><p>Masih Aktif</p><h3>").append(totalAktif).append("</h3></div></div>");
        konten.append("<div class='stat-card'><div class='stat-icon'>✅</div><div class='stat-info'><p>Sudah Selesai</p><h3>").append(totalSelesai).append("</h3></div></div>");
        konten.append("<div class='stat-card'><div class='stat-icon'>💰</div><div class='stat-info'><p>Total Pendapatan</p><h3 style='font-size:1rem;'>Rp ").append(String.format("%,.0f", grandTotal)).append("</h3></div></div>");
        konten.append("</div>");

        konten.append("<div class='card'>");
        konten.append("<div class='card-title'>📄 Data Transaksi");
        konten.append("<form method='post' action='LaporanTransaksi' style='margin-left:auto;'>");
        konten.append("<input type='hidden' name='aksi' value='cetak'/>");
        konten.append("<button type='submit' class='btn btn-outline' style='font-size:0.8rem;padding:7px 16px;'>🖨️ Cetak PDF</button>");
        konten.append("</form></div>");

        konten.append("<div class='table-wrap'>");
        konten.append("<table class='data-table'><thead><tr>");
        konten.append("<th>No</th><th>ID Tr</th><th>Customer</th><th>Mobil</th><th>Plat</th>");
        konten.append("<th>Tgl Sewa</th><th>Tgl Kembali</th><th>Lama</th><th>Harga/Hari</th><th>Total Bayar</th><th>Status</th>");
        konten.append("</tr></thead><tbody>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='11' style='text-align:center;color:var(--text-light);padding:24px;'>Belum ada data transaksi</td></tr>");
        } else {
            int no = 1;
            for (Transaksi t : list) {
                boolean aktif = "aktif".equals(t.getStatus());
                konten.append("<tr>");
                konten.append("<td style='color:var(--text-light);'>").append(no++).append("</td>");
                konten.append("<td><b>#").append(t.getIdTransaksi()).append("</b></td>");
                konten.append("<td>").append(t.getNamaCustomer()).append("</td>");
                konten.append("<td>").append(t.getMerkMobil()).append(" ").append(t.getTipe()).append("</td>");
                konten.append("<td>").append(t.getPlatNomor()).append("</td>");
                konten.append("<td>").append(t.getTanggalSewa()).append("</td>");
                konten.append("<td>").append(t.getTanggalKembali()).append("</td>");
                konten.append("<td style='text-align:center;'>").append(t.getLamaSewa()).append(" hr</td>");
                konten.append("<td>Rp ").append(String.format("%,.0f", t.getHargaSewa())).append("</td>");
                konten.append("<td><b>Rp ").append(String.format("%,.0f", t.getTotalBayar())).append("</b></td>");
                konten.append("<td><span class='badge ").append(aktif ? "badge-info" : "badge-muted").append("'>")
                      .append(aktif ? "⏳ Aktif" : "✓ Selesai").append("</span></td>");
                konten.append("</tr>");
            }
        }
        konten.append("</tbody></table></div></div>");

        new MainForm().tampilkan(konten.toString(), request, response, "laporan");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res); }
}
