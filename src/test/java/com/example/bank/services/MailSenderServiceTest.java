package com.example.bank.services;

import com.example.bank.model.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MailSenderServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JavaMailSender mailSender;
    @Spy
    @InjectMocks
    private MailSenderService mailSenderService;

    @Value("${spring.mail.username}")
    private String username;


    @Test
    public void sendLinkToConfirmEmailTest() throws Exception {
        var oldConfirmationCode = "old_confirmationCode";
        var toEmail = "toEmail";
        var user = new UserEntity();
        user.setConfirmationCode(oldConfirmationCode);
        user.setEmail(toEmail);
        doReturn(user).when(userService).findByEmail(toEmail);
        SimpleMailMessage mailMessage = mailSenderService.sendLinkToConfirmEmail(toEmail);

        assertNotNull(user.getConfirmationCode());
        assertNotEquals(oldConfirmationCode, user.getConfirmationCode());

        assertEquals(username, mailMessage.getFrom());
        assertEquals(toEmail, mailMessage.getTo()[0]);
        assertNotNull(mailMessage.getSubject());

        var contains = mailMessage.getText().contains("http://localhost:8080/confirm-email?confirmation-code=%s&email=%s"
                .formatted(user.getConfirmationCode(),user.getEmail()));
        assertTrue(contains);

        verify(userService,times(1)).findByEmail(toEmail);
        verify(userService,times(1)).updateUser(user);
        verify(mailSenderService,times(1)).send(mailMessage);

    }

    @Test
    public void sendLinkToChangePasswordTest() throws Exception {
        var oldConfirmationCode = "old_confirmationCode";
        var toEmail = "toEmail";
        var user = new UserEntity();
        user.setConfirmationCode(oldConfirmationCode);
        user.setEmail(toEmail);
        doReturn(user).when(userService).findByEmail(toEmail);
        SimpleMailMessage mailMessage = mailSenderService.sendLinkToChangePassword(toEmail);

        assertNotNull(user.getConfirmationCode());
        assertNotEquals(oldConfirmationCode, user.getConfirmationCode());

        assertEquals(username, mailMessage.getFrom());
        assertEquals(toEmail, mailMessage.getTo()[0]);
        assertNotNull(mailMessage.getSubject());

        var contains = mailMessage.getText().contains("http://localhost:8080/forgot-password/reset-password?confirmation-code=%s&email=%s"
                .formatted(user.getConfirmationCode(),user.getEmail()));
        assertTrue(contains);

        verify(userService,times(1)).findByEmail(toEmail);
        verify(userService,times(1)).updateUser(user);
        verify(mailSenderService,times(1)).send(mailMessage);

    }
}
