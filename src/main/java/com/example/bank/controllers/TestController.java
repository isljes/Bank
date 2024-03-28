package com.example.bank.controllers;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardType;
import com.example.bank.model.PaymentSystem;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.repositories.CardRepository;
import com.example.bank.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final PersonalDetailsServiceImpl personalDetailsService;
    private final CardService cardService;
    private final CurrencyRateServiceImpl currencyRateService;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;
    private final TransferMoneyServiceImpl transferMoneyService;
    private final CardRepository cardRepository;
    private final TransactionHistoryService transactionHistoryService;



    @GetMapping
    public String test(Model model) throws Exception {
        cardService.findById(13L);
        cardService.findById(13L);
        cardService.findById(13L);
        return "adapt";
    }

}
