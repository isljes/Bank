package com.example.bank.requester;

import com.example.bank.dto.CurrencyRateDTO;

public interface CurrencyRatesRequester {
    CurrencyRateDTO request();
}
