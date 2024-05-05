package com.example.bank.controller;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.dto.SelectedCurrenciesDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class MainController {

    private final CurrencyRateService currencyRateService;
    private final ObjectMapper objectMapper;
    private final TransactionHistoryService transactionHistoryService;
    private final CardService cardService;

    @Value("${date.pattern.time}")
    private String timeStampPatternOnlyTime;

    @GetMapping
    @PreAuthorize("authentication.authenticated")
    public String getWelcomePage(@CookieValue(value = "selectedCurrencyRates", required = false) String selectedCurrencyRatesFromCookie,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletResponse response,
                                 Model model) throws JsonProcessingException {

        String currentUserEmail = userDetails.getUsername();
        List<CardEntity> cardEntities = cardService.findAllByUserEntityEmail(currentUserEmail);
        model.addAttribute("myCards", cardEntities);

        Set<String> allCurrenciesCharCodes = currencyRateService.findAll().keySet();
        model.addAttribute("allCurrenciesCharCodes", allCurrenciesCharCodes);

        List<CurrencyRateDTO.Currency> selectedCurrency = new ArrayList<>();

        if (selectedCurrencyRatesFromCookie == null) {

            SelectedCurrenciesDTO selectedCurrenciesDTO = new SelectedCurrenciesDTO(new String[]{"EUR", "USD"});

            Cookie cookie = new Cookie("selectedCurrencyRates",
                    URLEncoder.encode(objectMapper.writeValueAsString(selectedCurrenciesDTO),
                            StandardCharsets.UTF_8));
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);

            model.addAttribute("selectedCurrenciesDTO", selectedCurrenciesDTO);

            CurrencyRateDTO.Currency eur = currencyRateService.findCurrencyByCharCode("EUR");
            CurrencyRateDTO.Currency usd = currencyRateService.findCurrencyByCharCode("USD");
            selectedCurrency.addAll(List.of(eur, usd));
        } else {
            String decodedSelectedCurrencyRatesCookie = URLDecoder.decode(selectedCurrencyRatesFromCookie, StandardCharsets.UTF_8);
            SelectedCurrenciesDTO selectedCurrenciesDTO = objectMapper.readValue(decodedSelectedCurrencyRatesCookie, SelectedCurrenciesDTO.class);
            model.addAttribute("selectedCurrenciesDTO", selectedCurrenciesDTO);
            for (String currencyCharCode : selectedCurrenciesDTO.currencies()) {
                selectedCurrency.add(currencyRateService.findCurrencyByCharCode(currencyCharCode));
            }
        }

        model.addAttribute("selectedCurrencies", selectedCurrency);

        List<TransactionHistoryEntity> transactionHistoryLast24h = new ArrayList<>();
        Instant instant = Instant.now().minus(24, ChronoUnit.HOURS);
        Timestamp from = Timestamp.from(instant);
        Timestamp to = Timestamp.from(Instant.now());
        for (CardEntity card : cardEntities) {
            transactionHistoryLast24h.addAll(transactionHistoryService.findAllByCardAndTimestampBetween(card, from, to));
        }
        transactionHistoryLast24h.sort(Comparator.comparing(TransactionHistoryEntity::getTimestamp));
        model.addAttribute("transactionHistoryLast24h", transactionHistoryLast24h);
        model.addAttribute("timePattern", timeStampPatternOnlyTime);
        return "welcome";
    }

    @PostMapping("/update-currency")
    @PreAuthorize("authentication.authenticated")
    public String updateCookie(@ModelAttribute("selectedCurrenciesDTO") SelectedCurrenciesDTO selectedCurrenciesDTO,
                               HttpServletResponse response) throws JsonProcessingException {
        Cookie cookie = new Cookie("selectedCurrencyRates",
                URLEncoder.encode(objectMapper.writeValueAsString(selectedCurrenciesDTO),
                        StandardCharsets.UTF_8));
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
        return "redirect:/";
    }


}
