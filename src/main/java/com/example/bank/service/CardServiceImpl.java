package com.example.bank.service;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardStatus;
import com.example.bank.model.PaymentSystem;
import com.example.bank.repository.CardRepository;
import com.example.bank.service.exception.CardNotFoundException;
import com.example.bank.service.exception.DAOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
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
    public List<CardEntity> findAllByCardStatusOrderByExpirationAsc(CardStatus cardStatus) {
        return cardRepository.findAllByCardStatusOrderByExpirationAsc(cardStatus);
    }

    @Override
    public List<CardEntity> findAllByUserEntityEmail(String email) {
        return cardRepository.findAllByUserEntityEmail(email);
    }


    @Override
    public CardEntity findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s card number does`t exist", cardNumber))));
    }

    @Override
    public CardEntity findById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s id does`t exist", id))));
    }

    @Override
    public CardEntity activateCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s id does`t exist", id))));
        cardEntity.setCardStatus(CardStatus.ACTIVE);
        return cardEntity;
    }

    @Override
    public CardEntity deactivateCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s id does`t exist", id))));
        cardEntity.setCardStatus(CardStatus.BANNED);
        return cardEntity;
    }

    @Override
    public CardEntity createNewCard(String email, IssueCardDTO issueCardDTO) {
        LocalDate expirationDate = LocalDate.now().plusYears(5);
        return cardRepository.save(CardEntity.builder()
                .cardNumber(generateCardNumberLuhnAlgorithm(issueCardDTO.paymentSystem()))
                .expiration((Date.valueOf(expirationDate)))
                .cvv(String.valueOf(new Random().nextInt(999 - 100 + 1) + 100))
                .cardStatus(CardStatus.UNDER_CONSIDERATION)
                .userEntity(userService.findByEmail(email))
                .paymentSystem(issueCardDTO.paymentSystem())
                .cardType(issueCardDTO.cardType())
                .balance(0).build());
    }

    @Override
    public void delete(CardEntity card) {
        cardRepository.delete(card);
    }

    private String generateCardNumberLuhnAlgorithm(PaymentSystem paymentSystem) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paymentSystem.getCode());
        stringBuilder.append(BIN);
        String lastCardNumberInDB = cardRepository.findTopByOrderByIdDesc().getCardNumber();
        Long cardId = Long.parseLong(lastCardNumberInDB.substring(6, 15)) + 1;
        stringBuilder.append(cardId);
        stringBuilder.append(0);
        return luhnAlgorithm(stringBuilder.toString());
    }

    private String luhnAlgorithm(String cardNumber) {
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
        return sum % 10 == 0;
    }
}
