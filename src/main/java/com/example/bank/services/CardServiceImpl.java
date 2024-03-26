package com.example.bank.services;

import com.example.bank.model.CardEntity;
import com.example.bank.model.PaymentSystem;
import com.example.bank.model.Status;
import com.example.bank.repositories.CardRepository;
import com.example.bank.services.exception.CardNotFoundException;
import com.example.bank.services.exception.DAOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    private final String BIN = "82390";

    @Override
    public List<CardEntity> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public CardEntity findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() ->new DAOException(
                new CardNotFoundException(String.format("Card with %s card number does`t exist", cardNumber))));
    }

    @Override
    public CardEntity findById(Long id){
        return cardRepository.findById(id).orElseThrow(() ->new DAOException(
                new CardNotFoundException(String.format("Card with %s id does`t exist", id))));
    }

    @Override
    public void activateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.ACTIVE);
        cardRepository.save(cardEntity);
    }

    @Override
    public void deactivateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.BANNED);
        cardRepository.save(cardEntity);
    }

    @Override
    public CardEntity createNewCard(String email, CardEntity card) {
        String cardNumber=generateCardNumberBeforeLuhnAlgorithm(card.getPaymentSystem());
        card.setCardNumber(luhnAlgorithmForGenerateLastDigit(cardNumber));
        card.setDate(new Date(new java.util.Date().getTime()));
        card.setCvv(String.valueOf(new Random().nextInt(999 - 100 + 1) + 100));
        card.setUserEntity(userService.findByEmail(email));
        card.setStatus(Status.UNDER_CONSIDERATION);
        cardRepository.save(card);
        return card;
    }

    private String generateCardNumberBeforeLuhnAlgorithm(PaymentSystem paymentSystem){
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

    @Override
    public boolean checkCorrectnessCardNumber(String cardNumber) {
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
