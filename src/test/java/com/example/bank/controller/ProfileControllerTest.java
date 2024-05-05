package com.example.bank.controller;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.service.MailSenderService;
import com.example.bank.service.PersonalDetailsService;
import com.example.bank.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonalDetailsService personalDetailsService;
    @MockBean
    private MailSenderService mailSenderService;
    @MockBean
    private SecurityService securityService;

    private UserEntity userEntity;

    @BeforeEach
    public void init() {
        final var email = "some@email.com";
        final var user = UserEntity.builder().email(email)
                .confirmationCode(UUID.randomUUID().toString())
                .build();
        final var personalDetails = PersonalDetailsEntity.builder()
                .userEntity(user).build();
        user.setPersonalDetailsEntity(personalDetails);
        userEntity = user;
    }


    @Test
    @WithMockUser(authorities = "VIEW_ONLY_HIS_PROFILE")
    public void getProfilePage_shouldReturnPage() throws Exception {

        when(personalDetailsService.findByUserEntityEmail(anyString())).thenReturn(userEntity.getPersonalDetailsEntity());

        this.mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("profile"),
                        model().attributeExists("profile")
                );
        verify(personalDetailsService, atLeastOnce()).findByUserEntityEmail(anyString());
    }

    @Test
    @WithMockUser(authorities = "VIEW_ONLY_HIS_PROFILE")
    public void changePersonalDetails_shouldRedirect() throws Exception {
        this.mockMvc.perform(post("/profile")
                        .flashAttr("profile", userEntity.getPersonalDetailsEntity())
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/profile")
                );
        verify(personalDetailsService, atLeastOnce()).update(any());
    }

    @Test
    @WithMockUser(authorities = "CONFIRM_EMAIL")
    public void sendConfirmationCode_shouldRedirect() throws Exception {
        final var user=new UserEntity();
        user.setEmail("user");
        final var personalDetails=new PersonalDetailsEntity();
        personalDetails.setUserEntity(userEntity);

        when(personalDetailsService.findByUserEntityEmail(anyString())).thenReturn(personalDetails);
        this.mockMvc.perform(post("/confirm-email")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/profile")
                );
        verify(mailSenderService, atLeastOnce()).sendLinkToConfirmEmail(anyString());
    }

    @Test
    @WithMockUser(authorities = "CONFIRM_EMAIL")
    public void confirmEmail_shouldRedirect_whenRequestParamCorrect() throws Exception {
        when(securityService.confirmEmail(anyString(), anyString())).thenReturn(true);

        this.mockMvc.perform(get("/confirm-email")
                        .param("confirmation-code", userEntity.getConfirmationCode())
                        .param("email", userEntity.getEmail()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/profile"),
                        model().attributeDoesNotExist("response")
                );
        verify(securityService, atLeastOnce()).confirmEmail(anyString(), anyString());
    }


    @Test
    @WithMockUser(authorities = "CONFIRM_EMAIL")
    public void confirmEmail_shouldRedirect_whenRequestParamIncorrect() throws Exception {

        when(securityService.confirmEmail(anyString(), anyString())).thenReturn(false);

        this.mockMvc.perform(get("/confirm-email")
                        .param("confirmation-code", userEntity.getConfirmationCode())
                        .param("email", userEntity.getEmail()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("response"),
                        model().attributeExists("response")
                );
        verify(securityService, atLeastOnce()).confirmEmail(anyString(), anyString());
    }
}
