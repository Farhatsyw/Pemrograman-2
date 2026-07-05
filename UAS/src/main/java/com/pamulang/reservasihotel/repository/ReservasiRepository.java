package com.pamulang.reservasihotel.repository;

import com.pamulang.reservasihotel.model.Reservasi;
import com.pamulang.reservasihotel.model.StatusReservasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository untuk entitas Reservasi.
 * Menghubungkan aplikasi ke tabel "reservasi" pada database MySQL melalui JDBC.
 */
public interface ReservasiRepository extends JpaRepository<Reservasi, Long> {

    List<Reservasi> findByStatusOrderByTanggalCheckInDesc(StatusReservasi status);

    List<Reservasi> findAllByOrderByIdDesc();
}
