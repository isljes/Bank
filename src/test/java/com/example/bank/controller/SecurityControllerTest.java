package com.example.bank.controller;

import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import com.example.bank.service.SecurityService;
import com.example.bank.service.UserService;
import com.example.bank.validator.RegistrationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest({SecurityController.class})
@AutoConfigureMockMvc(addFilters = false)
class SecurityControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationValidator registrationValidator;
    @MockBean
    private UserService userService;
    @MockBean
    private SecurityService securityService;



    @Test
    public void getLoginPage_shouldReturnLoginPage()  throws Exception{
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("login")
                );
    }

    @Test
    public void getRegistrationPage_shouldReturnRegistrationPage()  throws Exception{
        this.mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("registration"),
                        model().attributeExists("userEntity")
                );
    }

    @Test
    public void createNewUser_shouldReturnRegistrationPage_whenIncorrectData()  throws Exception{
        this.mockMvc.perform(post("/registration").requestAttr("userEntity",new UserEntity()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("registration")
                );

        verify(registrationValidator,times(1)).validate(any(),any());
        verify(userService,times(0)).createNewUserAfterRegistration(any());
        verify(securityService,times(0)).autoLogin(any(),any(),any(),any());

    }


    @Test
    public void createNewUser_shouldRedirect_whenCorrectData()  throws Exception{
        final var email="some@email.com";
        final var password="some_password";
        this.mockMvc.perform(post("/registration")
                        .param("email",email)
                        .param("password",password)
                        .param("confirmPassword",password)
                        .param("role", Role.USER.name()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/")
                );

        verify(registrationValidator,times(1)).validate(any(),any());
        verify(userService,times(1)).createNewUserAfterRegistration(any());
        verify(securityService,times(1)).autoLogin(eq(email),eq(password),eq(Role.USER.getAuthorities()),any());

    }
}