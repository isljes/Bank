package com.example.bank.services;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.logging.ManualLogging;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public interface CurrencyRateService {
    Map<String, CurrencyRateDTO.Currency> findAll();

    CurrencyRateDTO.Currency findCurrencyByCharCode(String charCode);

    void updateCurrencyRates();
}
