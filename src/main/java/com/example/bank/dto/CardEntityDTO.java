package com.example.bank.dto;

import com.example.bank.model.CardType;
import com.example.bank.model.PaymentSystem;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CardEntityDTO implements Serializable {
    private long id;
    private String cardNumber;
    private String cvv;
    private PaymentSystem paymentSystem;
    private CardType cardType;
    private long balance;

}
