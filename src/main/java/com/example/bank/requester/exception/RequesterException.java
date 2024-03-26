package com.example.bank.requester.exception;

public class RequesterException extends RuntimeException {
    public RequesterException(String message) {
        super(message);
    }
    public RequesterException(String message, Throwable cause) {
        super(message, cause);
    }
    public RequesterException(Throwable cause) {
        super(cause);
    }
}
