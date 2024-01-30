package com.example.bank.exceptionhandling;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public record UIExceptionDTO(HttpStatus status, String message) implements Serializable {
}
