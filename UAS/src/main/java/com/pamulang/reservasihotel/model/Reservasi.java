package com.pamulang.reservasihotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Class Reservasi merepresentasikan entitas transaksi reservasi kamar hotel.
 * Atribut sesuai soal: id, nama tamu, tipe kamar, tanggal check-in, tanggal check-out.
 * Relasi ManyToOne ke Kamar (satu kamar bisa punya banyak riwayat reservasi).
 */
@Entity
@Table(name = "reservasi")
public class Reservasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama tamu wajib diisi")
    @Column(name = "nama_tamu", nullable = false, length = 100)
    private String namaTamu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kamar_id", nullable = false)
    private Kamar kamar;

    @NotNull(message = "Tanggal check-in wajib diisi")
    @Column(name = "tanggal_checkin", nullable = false)
    private LocalDate tanggalCheckIn;

    @NotNull(message = "Tanggal check-out wajib diisi")
    @Column(name = "tanggal_checkout", nullable = false)
    private LocalDate tanggalCheckOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusReservasi status;

    public Reservasi() {
    }

    public Reservasi(String namaTamu, Kamar kamar, LocalDate tanggalCheckIn, LocalDate tanggalCheckOut) {
        this.namaTamu = namaTamu;
        this.kamar = kamar;
        this.tanggalCheckIn = tanggalCheckIn;
        this.tanggalCheckOut = tanggalCheckOut;
        this.status = StatusReservasi.AKTIF;
    }

    /** Method OOP: menghitung jumlah malam menginap */
    public long getJumlahMalam() {
        if (tanggalCheckIn == null || tanggalCheckOut == null) return 0;
        return ChronoUnit.DAYS.between(tanggalCheckIn, tanggalCheckOut);
    }

    /** Method OOP: menghitung total biaya reservasi */
    public long getTotalBiaya() {
        if (kamar == null) return 0;
        return getJumlahMalam() * kamar.getHargaPerMalam();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaTamu() {
        return namaTamu;
    }

    public void setNamaTamu(String namaTamu) {
        this.namaTamu = namaTamu;
    }

    public Kamar getKamar() {
        return kamar;
    }

    public void setKamar(Kamar kamar) {
        this.kamar = kamar;
    }

    public LocalDate getTanggalCheckIn() {
        return tanggalCheckIn;
    }

    public void setTanggalCheckIn(LocalDate tanggalCheckIn) {
        this.tanggalCheckIn = tanggalCheckIn;
    }

    public LocalDate getTanggalCheckOut() {
        return tanggalCheckOut;
    }

    public void setTanggalCheckOut(LocalDate tanggalCheckOut) {
        this.tanggalCheckOut = tanggalCheckOut;
    }

    public StatusReservasi getStatus() {
        return status;
    }

    public void setStatus(StatusReservasi status) {
        this.status = status;
    }
}
