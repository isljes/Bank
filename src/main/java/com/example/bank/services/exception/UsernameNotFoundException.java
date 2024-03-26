package com.example.bank.services.exception;

public class UsernameNotFoundException extends DAOException {
    public UsernameNotFoundException(String message) {
        super(message);
    }

    public UsernameNotFoundException(Throwable cause) {
        super(cause);
    }

    public UsernameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
