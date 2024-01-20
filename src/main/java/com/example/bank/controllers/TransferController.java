package com.example.bank.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {


    @GetMapping("/by-card-number")
    public String getTransferPageByCardNumber(Model model){
        model.addAttribute("by","cardNumberForm");
        return "transfers";
    }

    @GetMapping("/by-phone-number")
    public String getTransferPageByPhoneNumber(Model model){
        model.addAttribute("by","phoneNumberForm");
        return "transfers";
    }
}
