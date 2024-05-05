package com.example.bank.controller;

import com.example.bank.model.UserEntity;
import com.example.bank.service.MailSenderService;
import com.example.bank.service.SecurityService;
import com.example.bank.service.UserService;
import com.example.bank.validator.ChangePasswordValidator;
import com.example.bank.validator.ForgotPasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForgotPasswordController.class)
@Import({ChangePasswordValidator.class, ForgotPasswordValidator.class})
@WithMockUser(roles = "ANONYMOUS")
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ForgotPasswordValidator forgotPasswordValidator;
    @Autowired
    private ChangePasswordValidator changePasswordValidator;
    @MockBean
    private UserService userService;
    @MockBean
    private SecurityService securityService;
    @MockBean
    private MailSenderService mailSenderService;


    @Test
    void getForgotPasswordPage_shouldReturnForgotPasswordPage() throws Exception {
        this.mockMvc.perform(get("/forgot-password"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("userEntity"),
                        view().name("forgot-password")
                );
    }

    @Test
    void sendLinkForChangePassword_shouldSendLinkAndReturnResponsePage_whenCorrectData() throws Exception {
        final var user = UserEntity.builder().email("exists_user").build();
        when(userService.existsByEmail(anyString())).thenReturn(true);

        this.mockMvc.perform(post("/forgot-password")
                        .flashAttr("userEntity", user)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("response"),
                        view().name("response")
                );
        verify(mailSenderService, atLeastOnce()).sendLinkToChangePassword(anyString());
    }

    @Test
    void sendLinkForChangePassword_shouldReturnForgotPasswordPage_whenIncorrectData() throws Exception {
        final var user = UserEntity.builder().email("exists_user").build();
        when(userService.existsByEmail(anyString())).thenReturn(false);

        this.mockMvc.perform(post("/forgot-password")
                        .flashAttr("userEntity", user)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeDoesNotExist("response"),
                        view().name("forgot-password")
                );
        verify(mailSenderService, times(0)).sendLinkToChangePassword(anyString());
    }

    @Test
    void getResetPasswordPage_shouldReturnResetPasswordPage_whenCorrectData() throws Exception {
        final var confirmationCode = UUID.randomUUID().toString();
        final var user = UserEntity.builder()
                .email("exists_user")
                .confirmationCode(confirmationCode)
                .build();
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        this.mockMvc.perform(get("/forgot-password/reset-password")
                        .param("email", user.getEmail())
                        .param("confirmation-code", confirmationCode))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("userEntity"),
                        model().attributeDoesNotExist("response"),
                        view().name("reset-password")
                );

        verify(userService, atLeastOnce()).findByEmail(user.getEmail());
    }

    @Test
    void getResetPasswordPage_shouldReturnResponsePage_whenIncorrectData() throws Exception {
        final var oldConfirmationCode = UUID.randomUUID().toString();
        final var user = UserEntity.builder()
                .email("exists_user")
                .confirmationCode(UUID.randomUUID().toString())
                .build();
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        this.mockMvc.perform(get("/forgot-password/reset-password")
                        .param("email", user.getEmail())
                        .param("confirmation-code", oldConfirmationCode))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("response"),
                        model().attributeDoesNotExist("userEntity"),
                        view().name("response")
                );

        verify(userService, atLeastOnce()).findByEmail(user.getEmail());
    }

    @Test
    void resetPassword_shouldReturnResponsePage_whenCorrectData() throws Exception {
        final var password = "password";
        final var user = UserEntity.builder()
                .email("exists_user@email.com")
                .password(password)
                .confirmPassword(password)
                .confirmationCode(UUID.randomUUID().toString())
                .build();

        this.mockMvc.perform(post("/forgot-password/reset-password")
                        .flashAttr("userEntity",user)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("response"),
                        view().name("response")
                );

        verify(securityService, atLeastOnce()).changePassword(user.getEmail(),user.getPassword());
    }

    @Test
    void resetPassword_shouldReturnResetPasswordPage_whenIncorrectData() throws Exception {
        final var password = "password";
        final var user = UserEntity.builder()
                .email("exists_user@email.com")
                .password(password)
                .confirmPassword("old"+password)
                .confirmationCode(UUID.randomUUID().toString())
                .build();

        this.mockMvc.perform(post("/forgot-password/reset-password")
                        .flashAttr("userEntity",user)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeDoesNotExist("response"),
                        view().name("reset-password")
                );

        verify(securityService, times(0)).changePassword(user.getEmail(),user.getPassword());
    }


}