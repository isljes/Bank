package com.example.bank.model;

public enum PaymentSystem {
    MIR("2"),
    AMERICAN_EXPRESS("3"),
    VISA("4"),
    MASTERCARD("5"),
    MAESTRO("6");
    final String code;

    public String getCode() {
        return code;
    }

    PaymentSystem(String code) {
        this.code = code;
    }
}
