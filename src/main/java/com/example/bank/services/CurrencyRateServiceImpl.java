package com.example.bank.services;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.logging.ManualLogging;
import com.example.bank.requester.CurrencyRatesRequester;
import com.example.bank.requester.exception.RequesterException;
import com.example.bank.services.exception.DAOException;
import com.example.bank.services.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final ObjectMapper objectMapper;
    private final CurrencyRatesRequester currencyRatesRequester;

    @Override
    public Map<String, CurrencyRateDTO.Currency> findAll(){
        CurrencyRateDTO currencyRateDTO = requestCurrencyRates();
        return currencyRateDTO.currencies();
    }

    @Override
    public CurrencyRateDTO.Currency findCurrencyByCharCode(String charCode){
        CurrencyRateDTO currencyRateDTO = requestCurrencyRates();
        CurrencyRateDTO.Currency currency = currencyRateDTO.currencies().get(charCode);
        if(currency==null) throw new DAOException(String.format("Currency with %s charCode does`t exist",charCode));
        return currency;
    }

    @Override
    @ManualLogging
    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = "CurrencyRatesRequester::request")
    public void updateCurrencyRates(){
        log.trace("CurrencyRatesRequester::request cache was evicted");
    }

    private CurrencyRateDTO requestCurrencyRates(){
        String currencyRateJson=null;
        try {
            currencyRateJson = currencyRatesRequester.request();
        }catch (Exception ex) {
            throw new ServiceException(new RequesterException(String.format("CurrencyRatesRequesterException-> "+ex)));
        }
        return jsonToCurrencyRateDTO(currencyRateJson);
    }
    private CurrencyRateDTO jsonToCurrencyRateDTO(String json){
        try {
            return objectMapper.readValue(json, CurrencyRateDTO.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed attempt to convert json to CurrencyRateDTO");
        }
    }

}
