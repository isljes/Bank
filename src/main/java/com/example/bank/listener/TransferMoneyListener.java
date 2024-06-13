package com.example.bank.listener;

import com.example.bank.dto.TransferMoneyDto;
import com.example.bank.model.CardEntity;
import com.example.bank.service.CardService;
import com.example.bank.service.TransactionHistoryService;
import com.example.bank.service.TransferMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferMoneyListener {

    private final CardService cardService;
    private final TransactionHistoryService transactionHistoryService;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR
    )
    @KafkaListener(topics = "transfer-money", groupId = "transfer_group")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void processTransfer(ConsumerRecord<String,Object> record) {
        TransferMoneyDto transferMoneyDto = (TransferMoneyDto) record.value();
        CardEntity fromCard = cardService.findByCardNumber(transferMoneyDto.getFromCardNumber());
        CardEntity toCard = cardService.findByCardNumber(transferMoneyDto.getToCardNumber());
        long amount = transferMoneyDto.getAmount();

        if (fromCard.getBalance() >= amount) {
            fromCard.setBalance(fromCard.getBalance() - amount);
            toCard.setBalance(toCard.getBalance() + amount);
        }
        transactionHistoryService.processLogTransaction(transferMoneyDto,new Timestamp(record.timestamp()));

    }

    @DltHandler
    public void dlt(ConsumerRecord<String, String> record){
        log.warn("Received message in DLT from transfer-money topic {}",record.value());
    }


}

