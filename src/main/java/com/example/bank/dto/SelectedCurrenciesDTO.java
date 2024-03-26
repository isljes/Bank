package com.example.bank.dto;

import lombok.Data;

import java.io.Serializable;


public record SelectedCurrenciesDTO(String[] currencies) implements Serializable {}
