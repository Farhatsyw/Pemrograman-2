package com.pamulang.reservasihotel.exception;

/**
 * Exception khusus yang dilempar ketika proses reservasi gagal dilakukan,
 * misalnya data tidak valid atau tanggal tidak sesuai.
 */
public class ReservasiGagalException extends RuntimeException {
    public ReservasiGagalException(String message) {
        super(message);
    }
}
