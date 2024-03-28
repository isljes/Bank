package com.example.bank.services;

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
    private String username;

    @Value("${spring.domain}")
    private String domain;


    @Override
    public SimpleMailMessage sendLinkToConfirmEmail(String toEmail) {
        UserEntity user = userService.findByEmail(toEmail);
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.updateUser(user);

        String messageText = String.format(
                "Hello, %s!\n Please, visit next link: http://%s/confirm-email?confirmation-code=%s&email=%s"
                , user.getEmail()
                , domain
                , user.getConfirmationCode()
                , user.getEmail()
        );
        String subject = "Confirm your Email";
        SimpleMailMessage message = createMailMessage(subject, messageText, toEmail);
        send(message);
        return message;
    }

    @Override
    public SimpleMailMessage sendLinkToChangePassword(String toEmail) {
        UserEntity user = userService.findByEmail(toEmail);
        user.setConfirmationCode(UUID.randomUUID().toString());
        userService.updateUser(user);

        String messageText = String.format(
                "Hello, %s!\n Please, visit next link to reset your password: " +
                        "http://%s/forgot-password/reset-password?confirmation-code=%s&email=%s"
                , user.getEmail()
                , domain
                , user.getConfirmationCode()
                , user.getEmail()
        );
        String subject = "Change password";
        SimpleMailMessage message = createMailMessage(subject, messageText, toEmail);
        send(message);
        return message;
    }

    void send(SimpleMailMessage message) {
        mailSender.send(message);
    }

    SimpleMailMessage createMailMessage(String subject, String message, String emailTo) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        return mailMessage;
    }
}
