package com.example.bank.requester;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.service.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class CurrencyRatesRequesterImpl implements CurrencyRatesRequester {

    @Value("${spring.currency-rates.url}")
    private String currencyRatesUrl;

    private final ObjectMapper objectMapper;

    @Cacheable(value = "CurrencyRatesRequester::request")
    public CurrencyRateDTO request()  {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(currencyRatesUrl))
                .GET()
                .build();
        HttpResponse<String> response = null;
        CurrencyRateDTO currencyRateDTO=null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String currencyRateJson = response.body();
            currencyRateDTO=objectMapper.readValue(currencyRateJson, CurrencyRateDTO.class);
        } catch (JsonProcessingException e){
            throw new ServiceException(e);
        }  catch(IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return currencyRateDTO;
    }
}
