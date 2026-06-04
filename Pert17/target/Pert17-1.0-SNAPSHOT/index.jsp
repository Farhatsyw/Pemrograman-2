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
<html lang="id">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="style.css" rel="stylesheet" type="text/css"/>
    <title>Dashboard - Rent Car UNPAM</title>
</head>
<body>

<!-- TOP BAR -->
<div class="topbar">
    <div class="topbar-brand"><span class="icon">🚗</span> Rent Car UNPAM</div>
    <div class="topbar-right">
        <span class="topbar-user">👤 <%=userName%></span>
        <a href="LogoutController" class="topbar-logout">Keluar →</a>
    </div>
</div>

<div class="layout">
    <!-- SIDEBAR -->
    <nav class="sidebar">
        <div class="sidebar-label">Master Data</div>
        <div class="sidebar-section">
            <a href="Mobil"><span class="nav-icon">🚙</span> Data Mobil</a>
            <a href="Customer"><span class="nav-icon">👥</span> Data Customer</a>
        </div>
        <div class="sidebar-divider"></div>
        <div class="sidebar-label">Transaksi</div>
        <div class="sidebar-section">
            <a href="Sewa"><span class="nav-icon">📋</span> Penyewaan</a>
            <a href="Pengembalian"><span class="nav-icon">🔄</span> Pengembalian</a>
        </div>
        <div class="sidebar-divider"></div>
        <div class="sidebar-label">Laporan</div>
        <div class="sidebar-section">
            <a href="LaporanTransaksi"><span class="nav-icon">📄</span> Laporan Transaksi</a>
        </div>
    </nav>

    <!-- MAIN -->
    <main class="main">
        <h1 class="page-title">Dashboard</h1>
        <p class="page-subtitle">Selamat datang, <b><%=userName%></b> — Aplikasi Penyewaan Mobil UNPAM</p>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">🚙</div>
                <div class="stat-info"><p>Menu Master</p><h3>Mobil & Customer</h3></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📋</div>
                <div class="stat-info"><p>Transaksi</p><h3>Sewa & Kembali</h3></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📄</div>
                <div class="stat-info"><p>Laporan</p><h3>Cetak PDF</h3></div>
            </div>
        </div>

        <div class="card">
            <div class="card-title">🏢 Informasi Aplikasi</div>
            <table style="width:100%;font-size:0.875rem;border-collapse:collapse;">
                <tr style="border-bottom:1px solid var(--cream-mid);">
                    <td style="padding:10px 0;color:var(--text-light);width:180px;">Nama Aplikasi</td>
                    <td style="padding:10px 0;font-weight:500;">Aplikasi Penyewaan Mobil (Rent Car)</td>
                </tr>
                <tr style="border-bottom:1px solid var(--cream-mid);">
                    <td style="padding:10px 0;color:var(--text-light);">Institusi</td>
                    <td style="padding:10px 0;">Universitas Pamulang</td>
                </tr>
                <tr style="border-bottom:1px solid var(--cream-mid);">
                    <td style="padding:10px 0;color:var(--text-light);">Alamat</td>
                    <td style="padding:10px 0;">Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan, Banten</td>
                </tr>
                <tr>
                    <td style="padding:10px 0;color:var(--text-light);">Mata Kuliah</td>
                    <td style="padding:10px 0;">Pemrograman 2 — Pertemuan 17</td>
                </tr>
            </table>
        </div>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:18px;">
            <a href="Mobil" style="text-decoration:none;">
                <div class="card" style="cursor:pointer;transition:box-shadow 0.2s;border:1.5px solid var(--cream-mid);">
                    <div style="font-size:2rem;margin-bottom:10px;">🚙</div>
                    <div style="font-weight:500;color:var(--text-dark);margin-bottom:4px;">Data Mobil</div>
                    <div style="font-size:0.8rem;color:var(--text-light);">Tambah &amp; lihat daftar armada kendaraan</div>
                </div>
            </a>
            <a href="Customer" style="text-decoration:none;">
                <div class="card" style="cursor:pointer;transition:box-shadow 0.2s;border:1.5px solid var(--cream-mid);">
                    <div style="font-size:2rem;margin-bottom:10px;">👥</div>
                    <div style="font-weight:500;color:var(--text-dark);margin-bottom:4px;">Data Customer</div>
                    <div style="font-size:0.8rem;color:var(--text-light);">Tambah &amp; lihat daftar pelanggan</div>
                </div>
            </a>
            <a href="Sewa" style="text-decoration:none;">
                <div class="card" style="cursor:pointer;transition:box-shadow 0.2s;border:1.5px solid var(--cream-mid);">
                    <div style="font-size:2rem;margin-bottom:10px;">📋</div>
                    <div style="font-weight:500;color:var(--text-dark);margin-bottom:4px;">Penyewaan</div>
                    <div style="font-size:0.8rem;color:var(--text-light);">Input transaksi sewa mobil baru</div>
                </div>
            </a>
            <a href="Pengembalian" style="text-decoration:none;">
                <div class="card" style="cursor:pointer;transition:box-shadow 0.2s;border:1.5px solid var(--cream-mid);">
                    <div style="font-size:2rem;margin-bottom:10px;">🔄</div>
                    <div style="font-weight:500;color:var(--text-dark);margin-bottom:4px;">Pengembalian</div>
                    <div style="font-size:0.8rem;color:var(--text-light);">Proses pengembalian kendaraan</div>
                </div>
            </a>
        </div>
    </main>
</div>

<footer class="footer">Copyright &copy; 2024 Universitas Pamulang &mdash; Jl. Surya Kencana No. 1 Pamulang, Tangerang Selatan</footer>
</body>
</html>
