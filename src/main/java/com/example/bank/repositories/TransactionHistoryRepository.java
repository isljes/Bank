package com.example.bank.repositories;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.model.TransactionHistoryID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistoryEntity, TransactionHistoryID> {
    List<TransactionHistoryEntity> findAllByCardAndTimestampBetween(CardEntity card, Timestamp from,Timestamp to);
}
