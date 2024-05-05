package com.example.bank.service;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService{

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public List<TransactionHistoryEntity> findAllByCardAndTimestampBetween(CardEntity card, Timestamp from, Timestamp to) {
       return transactionHistoryRepository.findAllByCardAndTimestampBetween(card,from,to);
    }

    @Override
    public void save(TransactionHistoryEntity transactionHistoryEntity){
        transactionHistoryRepository.save(transactionHistoryEntity);
    }

}
