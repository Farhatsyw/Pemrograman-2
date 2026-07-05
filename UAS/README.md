# Aplikasi Reservasi Hotel
UAS Pemrograman II — Teknik Informatika S1, Universitas Pamulang

Aplikasi web reservasi hotel berbasis **Spring Boot + Thymeleaf + JDBC (Spring Data JPA) + MySQL**, dibuat untuk dijalankan di **NetBeans**.

---

## ✅ Kesesuaian dengan Soal

| Poin Soal | Implementasi |
|---|---|
| a. Framework Java untuk Web Development (Spring Boot + Thymeleaf) | `ReservasiHotelApplication.java`, seluruh `Controller`, template Thymeleaf di `src/main/resources/templates` |
| b. Database dengan JDBC (MySQL + Spring Data JPA) | `application.properties` (koneksi JDBC), `KamarRepository`, `ReservasiRepository` |
| c. OOP: class `Reservasi` (id, nama tamu, tipe kamar, tanggal check-in/out) | `model/Reservasi.java` & `model/Kamar.java` |
| d. Layout Management (Bootstrap) | `fragments/layout.html` + `static/css/style.css`, Bootstrap 5 via CDN |
| e. Exception Handling (reservasi gagal / kamar penuh) | `exception/KamarPenuhException.java`, `exception/ReservasiGagalException.java`, ditangani di `ReservasiService` & `ReservasiController` |

Fitur aplikasi:
- Dashboard ringkasan (total kamar, kamar tersedia, reservasi aktif)
- Halaman daftar kamar dengan status Tersedia / Terisi
- Halaman daftar reservasi lengkap (nama tamu, kamar, tanggal, total biaya, status)
- Form buat reservasi baru dengan validasi tanggal & pengecekan kamar penuh
- Batalkan reservasi (otomatis mengembalikan status kamar jadi tersedia)

---

## 🗂️ Struktur Project

```
reservasi-hotel/
├── pom.xml
└── src/main/
    ├── java/com/pamulang/reservasihotel/
    │   ├── ReservasiHotelApplication.java
    │   ├── model/          (Kamar, Reservasi, StatusKamar, StatusReservasi)
    │   ├── repository/     (KamarRepository, ReservasiRepository)
    │   ├── service/        (ReservasiService)
    │   ├── exception/      (KamarPenuhException, ReservasiGagalException)
    │   └── controller/     (HomeController, KamarController, ReservasiController)
    └── resources/
        ├── application.properties
        ├── data.sql
        ├── static/css/style.css
        └── templates/      (index, kamar/list, reservasi/list, reservasi/form, fragments/layout)
```

---

## 🚀 Langkah Menjalankan di NetBeans

### 1. Siapkan software
- **JDK 17** (atau lebih baru)
- **NetBeans** versi terbaru (dengan plugin Maven — biasanya sudah bawaan)
- **MySQL Server** (bisa pakai XAMPP/Laragon/MySQL Workbench)

### 2. Ekstrak project
- Extract file `reservasi-hotel.zip`.

### 3. Buka project di NetBeans
1. Buka NetBeans → **File → Open Project**
2. Arahkan ke folder hasil extract (`reservasi-hotel`) → pilih folder tersebut (NetBeans otomatis mengenali `pom.xml` sebagai project Maven)
3. Klik **Open Project**
4. Tunggu NetBeans mengunduh dependency Maven (butuh koneksi internet saat pertama kali build)

### 4. Siapkan Database
Cukup nyalakan MySQL Server. **Tidak perlu membuat database manual**, karena `application.properties` sudah diset:
```
createDatabaseIfNotExist=true
```
Database `db_reservasi_hotel` dan seluruh tabel akan dibuat otomatis saat aplikasi pertama kali dijalankan (Hibernate `ddl-auto=update`), lalu data awal kamar akan otomatis terisi dari `data.sql`.

> Jika password MySQL kamu tidak kosong, edit file:
> `src/main/resources/application.properties`
> ```
> spring.datasource.username=root
> spring.datasource.password=isi_password_disini
> ```

### 5. Jalankan Aplikasi
- Klik kanan pada project **reservasi-hotel** → **Run**
- Atau klik kanan file `ReservasiHotelApplication.java` → **Run File**
- Tunggu sampai muncul log:
  ```
  Aplikasi Reservasi Hotel berhasil dijalankan!
  Buka browser: http://localhost:8080
  ```

### 6. Buka di Browser
Akses: **http://localhost:8080**

---

## 🧪 Alur Uji Coba (Skenario Demo)
1. Buka halaman **Beranda** → lihat ringkasan jumlah kamar & reservasi.
2. Buka **Daftar Kamar** → lihat kamar berstatus *Tersedia* (hijau) dan *Terisi* (merah).
3. Klik **Reservasi Baru** → isi nama tamu, pilih kamar, isi tanggal check-in/out → **Simpan**.
4. Coba pilih kamar yang berstatus *Terisi* (jika ada di dropdown karena reservasi lain) atau isi tanggal check-out lebih awal dari check-in → sistem akan menampilkan pesan error (exception handling) tanpa aplikasi crash.
5. Buka **Reservasi** → lihat data tersimpan, total biaya otomatis terhitung, serta tombol batalkan reservasi.

---

## 🛠️ Troubleshooting
- **Error koneksi database** → pastikan MySQL Server sudah berjalan (cek service MySQL / XAMPP).
- **Port 8080 sudah dipakai** → ubah `server.port` di `application.properties`.
- **Dependency Maven gagal download** → pastikan komputer terkoneksi internet saat build pertama kali.

---
Selamat mengerjakan UAS! 🎓
