package com.pamulang.reservasihotel.controller;

import com.pamulang.reservasihotel.service.ReservasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ReservasiService reservasiService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalKamar", reservasiService.getSemuaKamar().size());
        model.addAttribute("kamarTersedia", reservasiService.hitungKamarTersedia());
        model.addAttribute("reservasiAktif", reservasiService.hitungReservasiAktif());
        model.addAttribute("activePage", "home");
        return "index";
    }
}
