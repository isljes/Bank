package com.example.bank.service;

import com.example.bank.dto.TransferMoneyDto;
import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService{

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final CardService cardService;


    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryEntity> findAllByCardAndTimestampBetween(CardEntity card, Timestamp from, Timestamp to) {
       return transactionHistoryRepository.findAllByCardAndTimestampBetween(card,from,to);
    }

    @Override
    @Transactional
    public void save(TransactionHistoryEntity transactionHistoryEntity) {
        transactionHistoryRepository.save(transactionHistoryEntity);
    }


    @Transactional
    public void processLogTransaction(TransferMoneyDto transferMoneyDto,Timestamp timestamp){

        CardEntity fromCard = cardService.findByCardNumber(transferMoneyDto.getFromCardNumber());
        CardEntity toCard = cardService.findByCardNumber(transferMoneyDto.getToCardNumber());

        var transactionHistoryEntityFrom = TransactionHistoryEntity.builder()
                .amount(-transferMoneyDto.getAmount())
                .timestamp(timestamp)
                .card(fromCard)
                .build();

        var transactionHistoryEntityTo = TransactionHistoryEntity.builder()
                .amount(transferMoneyDto.getAmount())
                .timestamp(timestamp)
                .card(toCard)
                .build();

        transactionHistoryRepository.save(transactionHistoryEntityFrom);
        transactionHistoryRepository.save(transactionHistoryEntityTo);
    }

}
