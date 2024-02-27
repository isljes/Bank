package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import com.example.bank.model.CardEntity;
import com.example.bank.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferMoneyService {

    private final CardRepository cardRepository;

    @ManualLogging
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5,backoff = @Backoff(value = 100,multiplier = 2))
    public boolean transferMoney(String from, String to,long amount) {
        Optional<CardEntity> optionalFrom = cardRepository.findByCardNumber(from);
        Optional<CardEntity> optionalTo = cardRepository.findByCardNumber(to);
        if(optionalFrom.isPresent()&& optionalTo.isPresent()){
            CardEntity fromCard = optionalFrom.get();
            CardEntity toCard = optionalTo.get();
            if(fromCard.getBalance()>=amount){
                fromCard.setBalance(fromCard.getBalance()-amount);
                toCard.setBalance(toCard.getBalance()+amount);
                return true;
            }
        }
        return false;
    }
}
