package com.pamulang.reservasihotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReservasiHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasiHotelApplication.class, args);
        System.out.println("=======================================================");
        System.out.println(" Aplikasi Reservasi Hotel berhasil dijalankan!");
        System.out.println(" Buka browser: http://localhost:8080");
        System.out.println("=======================================================");
    }
}
