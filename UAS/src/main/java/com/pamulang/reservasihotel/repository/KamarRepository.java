package com.pamulang.reservasihotel.repository;

import com.pamulang.reservasihotel.model.Kamar;
import com.pamulang.reservasihotel.model.StatusKamar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository untuk entitas Kamar.
 * Spring Data JPA otomatis membuat koneksi JDBC ke MySQL
 * dan mengimplementasikan operasi CRUD dasar.
 */
public interface KamarRepository extends JpaRepository<Kamar, Long> {

    List<Kamar> findByStatus(StatusKamar status);

    List<Kamar> findByTipeKamarIgnoreCase(String tipeKamar);
}
