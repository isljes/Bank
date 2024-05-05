package com.example.bank.service;

import com.example.bank.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.domain}")
    private String domain;


    @Override
    public SimpleMailMessage sendLinkToConfirmEmail(String toEmail) {
        UserEntity user = userService.findByEmail(toEmail);
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.update(user);
        String messageText = String.format(
                "Hello, %s!\n Please, visit next link: http://%s/confirm-email?confirmation-code=%s&email=%s"
                , user.getEmail()
                , domain
                , user.getConfirmationCode()
                , user.getEmail()
        );
        String subject = "Confirm your Email";
        return send(messageText,subject,toEmail);
    }

    @Override
    public SimpleMailMessage sendLinkToChangePassword(String toEmail) {
        UserEntity user = userService.findByEmail(toEmail);
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.update(user);
        String messageText = String.format(
                "Hello, %s!\n Please, visit next link to reset your password: " +
                        "http://%s/forgot-password/reset-password?confirmation-code=%s&email=%s"
                , user.getEmail()
                , domain
                , user.getConfirmationCode()
                , user.getEmail()
        );
        String subject = "Change password";
        return send(messageText,subject,toEmail);
    }


    SimpleMailMessage send(String messageText,String subject,String... toEmail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageText);
        mailSender.send(mailMessage);
        return mailMessage;
    }

}
