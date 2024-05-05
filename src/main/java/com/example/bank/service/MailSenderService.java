package com.example.bank.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MailSenderService {
    SimpleMailMessage sendLinkToConfirmEmail(String toEmail);

    SimpleMailMessage sendLinkToChangePassword(String toEmail);
}
