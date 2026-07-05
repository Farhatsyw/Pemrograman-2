package com.pamulang.reservasihotel.exception;

/**
 * Exception khusus yang dilempar ketika kamar yang dipilih
 * sudah penuh / tidak tersedia untuk direservasi.
 */
public class KamarPenuhException extends RuntimeException {
    public KamarPenuhException(String message) {
        super(message);
    }
}
