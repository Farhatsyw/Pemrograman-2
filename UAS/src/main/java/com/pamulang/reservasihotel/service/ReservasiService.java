package com.pamulang.reservasihotel.service;

import com.pamulang.reservasihotel.exception.KamarPenuhException;
import com.pamulang.reservasihotel.exception.ReservasiGagalException;
import com.pamulang.reservasihotel.model.*;
import com.pamulang.reservasihotel.repository.KamarRepository;
import com.pamulang.reservasihotel.repository.ReservasiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer berisi business logic aplikasi reservasi hotel.
 * Menangani validasi, exception handling, dan perubahan status kamar.
 */
@Service
public class ReservasiService {

    private final ReservasiRepository reservasiRepository;
    private final KamarRepository kamarRepository;

    @Autowired
    public ReservasiService(ReservasiRepository reservasiRepository, KamarRepository kamarRepository) {
        this.reservasiRepository = reservasiRepository;
        this.kamarRepository = kamarRepository;
    }

    public List<Kamar> getSemuaKamar() {
        return kamarRepository.findAll();
    }

    public List<Kamar> getKamarTersedia() {
        return kamarRepository.findByStatus(StatusKamar.TERSEDIA);
    }

    public List<Reservasi> getSemuaReservasi() {
        return reservasiRepository.findAllByOrderByIdDesc();
    }

    /**
     * Membuat reservasi baru.
     * Melempar ReservasiGagalException jika data tidak valid,
     * dan KamarPenuhException jika kamar yang dipilih sudah terisi.
     */
    @Transactional
    public Reservasi buatReservasi(String namaTamu, Long kamarId, LocalDate checkIn, LocalDate checkOut) {

        if (namaTamu == null || namaTamu.isBlank()) {
            throw new ReservasiGagalException("Nama tamu wajib diisi.");
        }
        if (checkIn == null || checkOut == null) {
            throw new ReservasiGagalException("Tanggal check-in dan check-out wajib diisi.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ReservasiGagalException("Tanggal check-in tidak boleh sebelum hari ini.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ReservasiGagalException("Tanggal check-out harus setelah tanggal check-in.");
        }

        Kamar kamar = kamarRepository.findById(kamarId)
                .orElseThrow(() -> new ReservasiGagalException("Kamar tidak ditemukan."));

        if (!kamar.isTersedia()) {
            throw new KamarPenuhException(
                    "Kamar " + kamar.getNomorKamar() + " (" + kamar.getTipeKamar() + ") sedang penuh / tidak tersedia."
            );
        }

        Reservasi reservasi = new Reservasi(namaTamu.trim(), kamar, checkIn, checkOut);

        kamar.setStatus(StatusKamar.TERISI);
        kamarRepository.save(kamar);

        return reservasiRepository.save(reservasi);
    }

    /**
     * Membatalkan reservasi dan mengembalikan status kamar menjadi tersedia.
     */
    @Transactional
    public void batalkanReservasi(Long reservasiId) {
        Reservasi reservasi = reservasiRepository.findById(reservasiId)
                .orElseThrow(() -> new ReservasiGagalException("Reservasi tidak ditemukan."));

        reservasi.setStatus(StatusReservasi.DIBATALKAN);
        reservasiRepository.save(reservasi);

        Kamar kamar = reservasi.getKamar();
        kamar.setStatus(StatusKamar.TERSEDIA);
        kamarRepository.save(kamar);
    }

    public long hitungKamarTersedia() {
        return kamarRepository.findByStatus(StatusKamar.TERSEDIA).size();
    }

    public long hitungReservasiAktif() {
        return reservasiRepository.findByStatusOrderByTanggalCheckInDesc(StatusReservasi.AKTIF).size();
    }
}
