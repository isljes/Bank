package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import org.springframework.transaction.annotation.Transactional;


public interface ConfirmationService {
    @Transactional
    boolean confirmEmail(String email, String confirmationCode);
}
