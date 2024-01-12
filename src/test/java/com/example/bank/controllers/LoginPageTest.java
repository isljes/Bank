package com.example.bank.controllers;

import com.example.bank.configuration.TestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = TestContainer.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class LoginPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginTest_returnedCorrectPage() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string(containsString("Please log in"))
                );
    }

    @Test
    public void loginTest_redirectionToLoginPage() throws Exception {
        this.mockMvc.perform(get("/welcome"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("http://localhost/login")
                );
    }

    @Test
    public void loginTest_PositiveAuthenticate() throws Exception {
        this.mockMvc.perform(formLogin().user("kenik000000@gmail.com").password("alex230302"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/welcome")
                );
    }

    @Test
    public void loginTest_NegativeAuthenticateAndRedirectToLoginPage() throws Exception {
        this.mockMvc.perform(formLogin().user("non_existent@email.com").password("some_password"))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/login?error")
                );
    }

    @Test
    public void loginTest_NegativeAuthenticateParamError() throws Exception {
        this.mockMvc.perform(get("/login?error"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().string(containsString("Invalid username or password"))
                );
    }



}
