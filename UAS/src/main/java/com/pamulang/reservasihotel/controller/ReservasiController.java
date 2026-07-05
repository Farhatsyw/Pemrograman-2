package com.pamulang.reservasihotel.controller;

import com.pamulang.reservasihotel.exception.KamarPenuhException;
import com.pamulang.reservasihotel.exception.ReservasiGagalException;
import com.pamulang.reservasihotel.service.ReservasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/reservasi")
public class ReservasiController {

    @Autowired
    private ReservasiService reservasiService;

    @GetMapping
    public String daftarReservasi(Model model) {
        model.addAttribute("daftarReservasi", reservasiService.getSemuaReservasi());
        model.addAttribute("activePage", "reservasi");
        return "reservasi/list";
    }

    @GetMapping("/tambah")
    public String formTambah(Model model) {
        model.addAttribute("daftarKamar", reservasiService.getKamarTersedia());
        model.addAttribute("activePage", "reservasi");
        model.addAttribute("today", LocalDate.now());
        return "reservasi/form";
    }

    @PostMapping("/simpan")
    public String simpanReservasi(
            @RequestParam String namaTamu,
            @RequestParam Long kamarId,
            @RequestParam("tanggalCheckIn") String tanggalCheckInStr,
            @RequestParam("tanggalCheckOut") String tanggalCheckOutStr,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDate checkIn = LocalDate.parse(tanggalCheckInStr);
            LocalDate checkOut = LocalDate.parse(tanggalCheckOutStr);

            reservasiService.buatReservasi(namaTamu, kamarId, checkIn, checkOut);

            redirectAttributes.addFlashAttribute("sukses",
                    "Reservasi atas nama " + namaTamu + " berhasil dibuat!");
            return "redirect:/reservasi";

        } catch (KamarPenuhException e) {
            // Exception handling: kamar penuh
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservasi/tambah";

        } catch (ReservasiGagalException e) {
            // Exception handling: reservasi gagal (validasi data / tanggal)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservasi/tambah";

        } catch (Exception e) {
            // Exception handling: kesalahan tak terduga (mis. format tanggal salah)
            redirectAttributes.addFlashAttribute("error",
                    "Terjadi kesalahan saat memproses reservasi: " + e.getMessage());
            return "redirect:/reservasi/tambah";
        }
    }

    @PostMapping("/batal/{id}")
    public String batalkanReservasi(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservasiService.batalkanReservasi(id);
            redirectAttributes.addFlashAttribute("sukses", "Reservasi berhasil dibatalkan.");
        } catch (ReservasiGagalException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservasi";
    }
}
