package com.example.bank.service;

import com.example.bank.dto.CurrencyRateDTO;

import java.util.Map;

public interface CurrencyRateService {
    Map<String, CurrencyRateDTO.Currency> findAll();

    CurrencyRateDTO.Currency findCurrencyByCharCode(String charCode);

}
