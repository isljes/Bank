package com.example.bank.dto;

import com.example.bank.model.CardEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class TransferMoneyDTO implements Serializable {
    private String cardNumber;
    private long amount;
    private CardEntity cardEntity;
}

