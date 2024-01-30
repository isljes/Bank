package com.example.bank.customexception;

public class CardNotFoundException extends DAOException {
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
