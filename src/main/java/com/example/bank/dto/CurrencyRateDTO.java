package com.example.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;
public record CurrencyRateDTO (
        @JsonProperty("Date")
        String date,
        @JsonProperty("Timestamp")
        String timeStamp,
        @JsonProperty("Valute")
        Map<String,Currency> currencies) implements Serializable {
        public record Currency(
                @JsonProperty("CharCode")
                String charCode,
                @JsonProperty("Name")
                String name,
                @JsonProperty("Nominal")
                double nominal,
                @JsonProperty("Value")
                double value,
                @JsonProperty("Previous")
                double previous) {}
}
