package com.example.bank.service.exception;

public class CardNotFoundException extends DAOException {
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
