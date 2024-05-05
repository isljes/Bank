package com.example.bank.dto;

import com.example.bank.model.CardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyDTO implements Serializable {
    private String toCardNumber;
    private long amount;
    private CardEntity fromCardEntity;
}

