package com.example.bank.services;

import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface TransferMoneyService {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void transferMoney(String from, String to,long amount);
}
