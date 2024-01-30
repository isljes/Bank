package com.example.bank.customexception;

public abstract class DAOException extends RuntimeException{
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
