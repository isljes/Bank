package com.example.bank.services;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
@Transactional(readOnly = true)
public interface TransactionHistoryService {
    List<TransactionHistoryEntity> findAllByCardAndTimestampBetween(CardEntity card, Timestamp from, Timestamp to);
    @Transactional
    void save(TransactionHistoryEntity transactionHistoryEntity);

}
