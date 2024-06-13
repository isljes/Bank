package com.example.bank.controller;

import com.example.bank.dto.TransferMoneyDto;

import com.example.bank.service.TransferMoneyService;
import com.example.bank.service.TransferMoneyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    public final KafkaTemplate<String,Object> template;
    public final TransferMoneyService transferMoneyService;
    @GetMapping
    public void method(@RequestBody TransferMoneyDto transferMoneyDto){
        transferMoneyService.transferMoney(transferMoneyDto);
    }
}
