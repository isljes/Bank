package com.example.bank.service;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.service.exception.DAOException;
import com.example.bank.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TransferMoneyServiceImpl implements TransferMoneyService {

    private final CardService cardService;

    private final TransactionHistoryService transactionHistoryService;


    @Retryable(maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 1.5),
            noRetryFor = DAOException.class,
            recover = "transferMoneyRecover")
    public void transferMoney(String from, String to, long amount) {
        CardEntity fromCard = cardService.findByCardNumber(from);
        CardEntity toCard = cardService.findByCardNumber(to);
        if (fromCard.getBalance() >= amount) {
            fromCard.setBalance(fromCard.getBalance() - amount);
            toCard.setBalance(toCard.getBalance() + amount);
            var transactionHistoryEntityFrom =
                    TransactionHistoryEntity.builder()
                            .card(fromCard).amount(-amount)
                            .timestamp(new Timestamp(new Date().getTime()))
                            .build();
            var transactionHistoryEntityTo =
                    TransactionHistoryEntity.builder()
                            .card(toCard).amount(amount)
                            .timestamp(new Timestamp(new Date().getTime()))
                            .build();
            transactionHistoryService.save(transactionHistoryEntityFrom);
            transactionHistoryService.save(transactionHistoryEntityTo);
        }
    }

    @Recover
    public void transferMoneyRecover(RuntimeException ex, String from, String to, long amount) {
        throw new ServiceException("Transfer money recover method is executed");
    }

}
