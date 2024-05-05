package com.example.bank.service;

import com.example.bank.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MailSenderServiceTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private JavaMailSender mailSender;
    @Spy
    @InjectMocks
    private MailSenderServiceImpl mailSenderService;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.domain}")
    private String domain;


    @Test
    public void sendLinkToConfirmEmail_Test() throws Exception {
        final var toEmail = "toEmail";
        final var oldConfirmationCode=UUID.randomUUID().toString();

        final var user = new UserEntity();
        user.setConfirmationCode(oldConfirmationCode);
        user.setEmail(toEmail);

        when(userService.findByEmail(toEmail)).thenReturn(user);

        SimpleMailMessage mailMessage = mailSenderService.sendLinkToConfirmEmail(toEmail);

        assertNotNull(user.getConfirmationCode());
        assertNotEquals(oldConfirmationCode, user.getConfirmationCode());

        assertEquals(username, mailMessage.getFrom());
        assertEquals(toEmail, mailMessage.getTo()[0]);
        assertNotNull(mailMessage.getSubject());

        final var contains = mailMessage.getText().contains("http://%s/confirm-email?confirmation-code=%s&email=%s"
                .formatted(
                        domain,
                        user.getConfirmationCode(),
                        user.getEmail()));
        assertTrue(contains);

        verify(userService,times(1)).findByEmail(toEmail);
        verify(userService,times(1)).update(user);
        verify(mailSenderService,times(1)).send(anyString(),anyString(),anyString());

    }

    @Test
    public void sendLinkToChangePassword_Test() throws Exception {
        final var toEmail = "toEmail";
        final var oldConfirmationCode=UUID.randomUUID().toString();

        final var user = new UserEntity();
        user.setConfirmationCode(oldConfirmationCode);
        user.setEmail(toEmail);

        when(userService.findByEmail(toEmail)).thenReturn(user);

        SimpleMailMessage mailMessage = mailSenderService.sendLinkToChangePassword(toEmail);

        assertNotNull(user.getConfirmationCode());
        assertNotEquals(oldConfirmationCode, user.getConfirmationCode());

        assertEquals(username, mailMessage.getFrom());
        assertEquals(toEmail, mailMessage.getTo()[0]);
        assertNotNull(mailMessage.getSubject());

        final var contains = mailMessage.getText().contains("http://%s/forgot-password/reset-password?confirmation-code=%s&email=%s"
                .formatted(
                        domain,
                        user.getConfirmationCode(),
                        user.getEmail()));
        assertTrue(contains);

        verify(userService,times(1)).findByEmail(toEmail);
        verify(userService,times(1)).update(user);
        verify(mailSenderService,times(1)).send(anyString(),anyString(),anyString());

    }
}
