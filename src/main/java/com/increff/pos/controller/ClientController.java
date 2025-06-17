package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/client")
public class ClientController {

    @RequestMapping("")
    public String showClient(Model model) {
        model.addAttribute("name", "POS Client");
        return "client";
    }
}
