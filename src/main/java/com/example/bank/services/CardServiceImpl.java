package com.example.bank.services;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.PaymentSystem;
import com.example.bank.model.Status;
import com.example.bank.repositories.CardRepository;
import com.example.bank.services.exception.CardNotFoundException;
import com.example.bank.services.exception.DAOException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "CardServiceImpl::findByCardNumber", key = "#cardNumber")
    public CardEntity findByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s card number does`t exist", cardNumber))));
    }

    @Override
    @Cacheable(value = "CardServiceImpl::findById", key = "#id")
    public CardEntity findById(Long id) {
        System.out.println("CardServiceImpl::findById "+id);
        return cardRepository.findById(id).orElseThrow(() -> new DAOException(
                new CardNotFoundException(String.format("Card with %s id does`t exist", id))));
    }

    @Override
    @Caching(put = {
            @CachePut(value = "CardServiceImpl::findById", key = "#id"),
            @CachePut(value = "CardServiceImpl::findByCardNumber", key = "#result.cardNumber"),
    })
    public CardEntity activateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.ACTIVE);
        return cardRepository.save(cardEntity);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "CardServiceImpl::findById", key = "#id"),
            @CachePut(value = "CardServiceImpl::findByCardNumber", key = "#result.cardNumber"),
    })
    public CardEntity deactivateCard(Long id) {
        CardEntity cardEntity = findById(id);
        cardEntity.setStatus(Status.BANNED);
        return cardRepository.save(cardEntity);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "CardServiceImpl::findById",key = "#result.id",unless ="#result.id==null" ),
            @CachePut(value = "CardServiceImpl::findByCardNumber",key ="#result.cardNumber",unless ="#result.cardNumber==null")
    })
    public CardEntity createNewCard(String email, IssueCardDTO issueCardDTO) {
        CardEntity newCard = CardEntity.builder()
                .cardNumber(generateCardNumberLuhnAlgorithm(issueCardDTO.paymentSystem()))
                .date(new Date(new java.util.Date().getTime()))
                .cvv(String.valueOf(new Random().nextInt(999 - 100 + 1) + 100))
                .status(Status.UNDER_CONSIDERATION)
                .userEntity(userService.findByEmail(email))
                .paymentSystem(issueCardDTO.paymentSystem())
                .cardType(issueCardDTO.cardType())
                .balance(0).build();
        return cardRepository.save(newCard);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "CardServiceImpl::findById",key = "#card.id"),
            @CacheEvict(value = "CardServiceImpl::findByCardNumber",key ="#card.cardNumber")
    })
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
