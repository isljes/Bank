package com.example.bank.custom_exception;

public class CardNotFoundException extends NotFoundException{
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
