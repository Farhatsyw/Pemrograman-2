<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String userName = "";
    try { userName = session.getAttribute("userName").toString(); } catch (Exception e) {}
    if (userName == null || userName.isEmpty()) {
        response.sendRedirect("LoginController");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href='style.css' rel='stylesheet' type='text/css'/>
    <title>Informasi Nilai Mahasiswa</title>
</head>
<body bgcolor="#808080">
    <%
        String menu = "<br><b>Master Data</b><br>"
                + "<a href='Mahasiswa'>Mahasiswa</a><br>"
                + "<a href='MataKuliah'>Mata Kuliah</a><br><br>"
                + "<b>Transaksi</b><br>"
                + "<a href='Nilai'>Nilai</a><br><br>"
                + "<b>Laporan</b><br>"
                + "<a href='LaporanNilai'>Nilai</a><br><br>"
                + "<a href='LogoutController'>Logout</a><br><br>";

        String topMenu = "<nav><ul>"
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
    %>
    <center>
    <table width="80%" bgcolor="#eeeeee">
        <tr>
            <td colspan="2" align="center">
                <br>
                <h2 style="margin-bottom:0px;margin-top:0px;">Informasi Nilai Mahasiswa</h2>
                <h1 style="margin-bottom:0px;margin-top:0px;">UNIVERSITAS PAMULANG</h1>
                <h4 style="margin-bottom:0px;margin-top:0px;">Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan, Banten</h4>
                <br>
            </td>
        </tr>
        <tr height="400">
            <td width="200" align="center" valign="top" bgcolor="#eeffee">
                <br><div id="menu"><%=menu%></div>
            </td>
            <td align="center" valign="top" bgcolor="#ffffff">
                <%=topMenu%>
                <br>
                <h1>Selamat Datang</h1>
                <h2><%=userName%></h2>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center" bgcolor="#eeeeff">
                <small>Copyright &copy; 2016 Universitas Pamulang<br>
                Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan, Banten</small>
            </td>
        </tr>
    </table>
    </center>
</body>
</html>
