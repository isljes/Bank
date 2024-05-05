package com.example.bank.service;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.requester.CurrencyRatesRequester;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateServiceTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CurrencyRatesRequester currencyRatesRequester;

    @InjectMocks
    private CurrencyRateServiceImpl currencyRateService;

    @Test
    public void findAll_shouldCallRequester() throws Exception {
        final var currencyRateDTO = mock(CurrencyRateDTO.class);
        when(currencyRatesRequester.request()).thenReturn(currencyRateDTO);

        currencyRateService.findAll();

        verify(currencyRatesRequester, times(1)).request();
    }

    @Test
    public void findCurrencyByCharCode_shouldCallRequester() throws Exception {
        final var usd=new CurrencyRateDTO.Currency("some_charCode",
                "",
                12.0,
                12.0,
                12.0);

        final Map<String, CurrencyRateDTO.Currency> currencyRates =Map.of("usd",usd);
        final var currencyRateDTO = new CurrencyRateDTO("", "", currencyRates);

        when(currencyRatesRequester.request()).thenReturn(currencyRateDTO);

        final CurrencyRateDTO.Currency result = currencyRateService.findCurrencyByCharCode("usd");

        assertEquals(usd,result);
        verify(currencyRatesRequester, times(1)).request();
    }


}
