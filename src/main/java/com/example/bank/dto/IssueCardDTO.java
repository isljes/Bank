package com.example.bank.dto;

import com.example.bank.model.CardType;
import com.example.bank.model.PaymentSystem;
import lombok.Data;

import java.io.Serializable;

public record IssueCardDTO(PaymentSystem paymentSystem,
                           CardType cardType) implements Serializable {
}
