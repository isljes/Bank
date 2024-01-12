package com.example.bank.services;

import com.example.bank.custom_exception.CardNotFoundException;
import com.example.bank.model.CardEntity;
import com.example.bank.model.PaymentSystem;
import com.example.bank.model.Status;
import com.example.bank.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    private final String BIN = "82390";

    public List<CardEntity> findAll() {
        return cardRepository.findAll();
    }

    public CardEntity findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() ->
                new CardNotFoundException(String.format("Card with %s card number does`t exist", cardNumber)));
    }

    public CardEntity findById(Long id){
        return cardRepository.findById(id).orElseThrow(() ->
                new CardNotFoundException(String.format("Card with %s id does`t exist", id)));
    }

    public void activateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.ACTIVE);
        cardRepository.save(cardEntity);
    }

    public void deactivateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.NOT_ACTIVE);
        cardRepository.save(cardEntity);
    }

    public CardEntity createNewCard(String email, PaymentSystem paymentSystem) {

        String cardNumber=generateCardNumberBeforeLunhAlgorithm(paymentSystem);

        CardEntity cardEntity = new CardEntity();

        cardEntity.setCardNumber(luhnAlgorithmForGenerateLastDigit(cardNumber));
        cardEntity.setDate(new Date(new java.util.Date().getTime()));
        cardEntity.setCvv(String.valueOf(new Random().nextInt(999 - 100 + 1) + 100));
        cardEntity.setUserEntity(userService.findByEmail(email));
        cardEntity.setStatus(Status.NOT_ACTIVE);

        cardRepository.save(cardEntity);
        return cardEntity;
    }

    private String generateCardNumberBeforeLunhAlgorithm(PaymentSystem paymentSystem){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paymentSystem.getCode());
        stringBuilder.append(BIN);
        String lastCardNumberInDB = cardRepository.findTopByOrderByIdDesc().getCardNumber();
        Long cardId = Long.parseLong(lastCardNumberInDB.substring(6, 15)) + 1;
        stringBuilder.append(cardId);
        stringBuilder.append(0);
        return stringBuilder.toString();
    }

    private String luhnAlgorithmForGenerateLastDigit(String cardNumber) {
        int sum = 0;
        int nDigits = cardNumber.length();
        int parity = nDigits % 2;
        for (int i = 0; i < nDigits; i++) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (i % 2 == parity) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        if (sum % 10 != 0) {
            int lastDigit = sum % 10;
            int checksum = 10 - lastDigit;
            return cardNumber.substring(0, nDigits - 1).concat(String.valueOf(checksum));
        }
        return cardNumber;
    }

    private boolean checkCorrectnessCardNumber(String cardNumber) {
        int sum = 0;
        int nDigits = cardNumber.length();
        int parity = nDigits % 2;
        for (int i = 0; i < nDigits; i++) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (i % 2 == parity) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        if (sum % 10 == 0) return true;

        return false;
    }
}
