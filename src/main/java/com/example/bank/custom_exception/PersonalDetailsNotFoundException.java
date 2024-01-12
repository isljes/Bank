package com.example.bank.custom_exception;

public class PersonalDetailsNotFoundException extends NotFoundException{
    public PersonalDetailsNotFoundException(String message) {
        super(message);
    }

    public PersonalDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
