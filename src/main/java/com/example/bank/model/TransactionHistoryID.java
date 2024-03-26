package com.example.bank.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class TransactionHistoryID implements Serializable {
    @ToString.Exclude
    private CardEntity card;
    private Timestamp timestamp;
}
