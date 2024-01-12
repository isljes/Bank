package com.example.bank.controllers;

import com.example.bank.configuration.TestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.postgresql.gss.MakeGSS.authenticate;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = TestContainer.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@WithUserDetails("user@gmail.com")
public class UserWelcomePageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void welcomeTest_successfulAuthentication() throws Exception{
        this.mockMvc.perform(get("/welcome"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        authenticated(),
                        xpath("/html/body/p[1]").string("user@gmail.com")
                );
    }



}
