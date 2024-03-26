package com.example.bank.services;

import org.springframework.mail.SimpleMailMessage;

public interface MailSenderService {
    SimpleMailMessage sendLinkToConfirmEmail(String toEmail);

    SimpleMailMessage sendLinkToChangePassword(String toEmail);
}
