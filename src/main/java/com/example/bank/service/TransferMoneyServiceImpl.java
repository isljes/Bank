package com.example.bank.service;

import com.example.bank.dto.TransferMoneyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferMoneyServiceImpl implements TransferMoneyService {

    private final CardService cardService;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void transferMoney(TransferMoneyDto transferMoneyDto){
        var key=transferMoneyDto.getFromCardNumber()+transferMoneyDto.getToCardNumber();
        kafkaTemplate.send("transfer-money", key,transferMoneyDto);

    }

}
