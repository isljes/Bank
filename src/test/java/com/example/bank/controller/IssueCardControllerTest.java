package com.example.bank.controller;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardType;
import com.example.bank.model.PaymentSystem;
import com.example.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(IssueCardController.class)
@WithMockUser(authorities = "ISSUE_CARD")
class IssueCardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IssueCardController issueCardController;
    @MockBean
    private CardService cardService;

    @Test
    public void getCardPage_shouldReturnCardPage()  throws Exception{
        this.mockMvc.perform(get("/card-issue"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("card"),
                        view().name("card-issue")
                );
    }

    @Test
    public void issueCard_shouldRedirect()  throws Exception{
        final var issueCardDTO=new IssueCardDTO(PaymentSystem.MIR, CardType.DEBIT);
        this.mockMvc.perform(post("/card-issue")
                        .flashAttr("card",issueCardDTO)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/")
                );
        verify(cardService,atLeastOnce()).createNewCard(anyString(),eq(issueCardDTO));
    }

}