package com.pamulang.reservasihotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Class Kamar merepresentasikan entitas kamar hotel.
 * Menerapkan konsep OOP (encapsulation) dengan atribut private
 * dan diakses melalui getter/setter.
 */
@Entity
@Table(name = "kamar")
public class Kamar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nomor kamar wajib diisi")
    @Column(name = "nomor_kamar", nullable = false, unique = true, length = 10)
    private String nomorKamar;

    @NotBlank(message = "Tipe kamar wajib diisi")
    @Column(name = "tipe_kamar", nullable = false, length = 20)
    private String tipeKamar; // STANDARD, DELUXE, SUITE

    @NotNull(message = "Harga per malam wajib diisi")
    @Positive(message = "Harga harus lebih dari 0")
    @Column(name = "harga_per_malam", nullable = false)
    private Long hargaPerMalam;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusKamar status;

    public Kamar() {
    }

    public Kamar(String nomorKamar, String tipeKamar, Long hargaPerMalam, StatusKamar status) {
        this.nomorKamar = nomorKamar;
        this.tipeKamar = tipeKamar;
        this.hargaPerMalam = hargaPerMalam;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomorKamar() {
        return nomorKamar;
    }

    public void setNomorKamar(String nomorKamar) {
        this.nomorKamar = nomorKamar;
    }

    public String getTipeKamar() {
        return tipeKamar;
    }

    public void setTipeKamar(String tipeKamar) {
        this.tipeKamar = tipeKamar;
    }

    public Long getHargaPerMalam() {
        return hargaPerMalam;
    }

    public void setHargaPerMalam(Long hargaPerMalam) {
        this.hargaPerMalam = hargaPerMalam;
    }

    public StatusKamar getStatus() {
        return status;
    }

    public void setStatus(StatusKamar status) {
        this.status = status;
    }

    public boolean isTersedia() {
        return this.status == StatusKamar.TERSEDIA;
    }
}
