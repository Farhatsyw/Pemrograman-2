package com.pamulang.reservasihotel.controller;

import com.pamulang.reservasihotel.service.ReservasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kamar")
public class KamarController {

    @Autowired
    private ReservasiService reservasiService;

    @GetMapping
    public String daftarKamar(Model model) {
        model.addAttribute("daftarKamar", reservasiService.getSemuaKamar());
        model.addAttribute("activePage", "kamar");
        return "kamar/list";
    }
}
