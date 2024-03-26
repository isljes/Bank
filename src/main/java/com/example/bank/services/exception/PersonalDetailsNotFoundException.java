package com.example.bank.services.exception;

public class PersonalDetailsNotFoundException extends DAOException {
    public PersonalDetailsNotFoundException(String message) {
        super(message);
    }

    public PersonalDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
