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
import com.unpam.model.Nilai;
import com.unpam.view.MainForm;

@WebServlet(name = "LaporanNilaiController", urlPatterns = {"/LaporanNilai"})
public class LaporanNilaiController extends HttpServlet {

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

        String aksi = request.getParameter("aksi");

        // Kalau klik tombol cetak PDF
        if ("cetak".equals(aksi)) {
            Nilai nilai = new Nilai();
            ResultSet rs = nilai.getDataLaporan();
            try {
                String reportPath = getServletContext().getRealPath("/reports/NilaiReport.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);
                HashMap<String, Object> parameters = new HashMap<>();
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
                    response.setHeader("Content-Disposition", "inline; filename=LaporanNilai.pdf");
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
                err.append("<p style='color:red;'>Error cetak: ").append(ex.getMessage()).append("</p>");
                err.append("<a href='LaporanNilai'>&laquo; Kembali</a>");
                new MainForm().tampilkan(err.toString(), request, response);
                return;
            }
        }

        // Tampilkan tabel + tombol cetak
        Nilai nilai = new Nilai();
        List<Nilai> list = nilai.getList();

        StringBuilder konten = new StringBuilder();
        konten.append("<h2>Laporan Nilai Mahasiswa</h2>");
        konten.append("<form method='post' action='LaporanNilai'>");
        konten.append("<input type='hidden' name='aksi' value='cetak'/>");
        konten.append("<input type='submit' value='Cetak PDF' style='background:#534AB7;color:#fff;padding:8px 16px;border:none;border-radius:4px;cursor:pointer;margin-bottom:12px;'/>");
        konten.append("</form>");

        konten.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse;width:95%;font-size:12px;'>");
        konten.append("<tr style='background:#534AB7;color:#fff;'>");
        konten.append("<th>No</th><th>NIM</th><th>Nama</th><th>Sem</th><th>Kelas</th>");
        konten.append("<th>Kode MK</th><th>Mata Kuliah</th><th>SKS</th>");
        konten.append("<th>Tugas</th><th>UTS</th><th>UAS</th><th>Nilai Akhir</th><th>Huruf</th><th>Status</th>");
        konten.append("</tr>");

        if (list.isEmpty()) {
            konten.append("<tr><td colspan='14' align='center'>Belum ada data</td></tr>");
        } else {
            int no = 1;
            for (Nilai n : list) {
                String bg = (no % 2 == 0) ? "#f5f5f5" : "#ffffff";
                String statusColor = "Lulus".equals(n.getStatus()) ? "green" : "red";
                konten.append("<tr style='background:").append(bg).append(";'>");
                konten.append("<td align='center'>").append(no++).append("</td>");
                konten.append("<td>").append(n.getNim()).append("</td>");
                konten.append("<td>").append(n.getNamaMahasiswa()).append("</td>");
                konten.append("<td align='center'>").append(n.getSemester()).append("</td>");
                konten.append("<td align='center'>").append(n.getKelas()).append("</td>");
                konten.append("<td>").append(n.getKodeMataKuliah()).append("</td>");
                konten.append("<td>").append(n.getNamaMataKuliah()).append("</td>");
                konten.append("<td align='center'>").append(n.getJumlahSks()).append("</td>");
                konten.append("<td align='center'>").append(n.getTugas()).append("</td>");
                konten.append("<td align='center'>").append(n.getUts()).append("</td>");
                konten.append("<td align='center'>").append(n.getUas()).append("</td>");
                konten.append("<td align='center'>").append(n.getNilaiAkhir()).append("</td>");
                konten.append("<td align='center'><b>").append(n.getHuruf()).append("</b></td>");
                konten.append("<td align='center' style='color:").append(statusColor).append(";'>").append(n.getStatus()).append("</td>");
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
