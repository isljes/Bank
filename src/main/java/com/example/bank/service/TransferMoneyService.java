package com.example.bank.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface TransferMoneyService {
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void transferMoney(String from, String to,long amount);
}
