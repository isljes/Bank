package com.example.bank.service;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.logging.ManualLogging;
import com.example.bank.requester.CurrencyRatesRequester;
import com.example.bank.service.exception.DAOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {

        private final CurrencyRatesRequester currencyRatesRequester;

        @Override
        public Map<String, CurrencyRateDTO.Currency> findAll(){
            return currencyRatesRequester.request().currencies();
        }

        @Override
        public CurrencyRateDTO.Currency findCurrencyByCharCode(String charCode){
            CurrencyRateDTO currencyRateDTO = currencyRatesRequester.request();
            CurrencyRateDTO.Currency currency = currencyRateDTO.currencies().get(charCode);
            if(currency==null) throw new DAOException(String.format("Currency with %s charCode does`t exist",charCode));
            return currency;
        }

        @ManualLogging
        @Scheduled(cron = "0 0 0 * * *")
        @CacheEvict(value = "CurrencyRatesRequester::request")
        public void updateCurrencyRates(){
            log.trace("CurrencyRatesRequester::request cache was evicted");
        }



}
