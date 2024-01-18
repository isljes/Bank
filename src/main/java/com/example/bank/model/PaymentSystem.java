package com.example.bank.model;

import lombok.Getter;

@Getter
public enum PaymentSystem {
    MIR("2"),
    AMERICAN_EXPRESS("3"),
    VISA("4"),
    MASTERCARD("5"),
    MAESTRO("6");
    final String code;

    PaymentSystem(String code) {
        this.code = code;
    }
}
