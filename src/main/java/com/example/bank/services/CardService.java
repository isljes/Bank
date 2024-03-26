package com.example.bank.services;

import com.example.bank.model.CardEntity;

import java.util.List;

public interface CardService {
    List<CardEntity> findAll();

    CardEntity findByCardNumber(String cardNumber);

    CardEntity findById(Long id);

    void activateCard(Long id);

    void deactivateCard(Long id);

    CardEntity createNewCard(String email, CardEntity card);

    boolean checkCorrectnessCardNumber(String cardNumber);
}
