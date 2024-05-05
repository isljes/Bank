package com.example.bank.controller;

import com.example.bank.configuration.SecurityConfig;
import com.example.bank.dto.TransferMoneyDTO;
import com.example.bank.model.*;
import com.example.bank.security.UserDetailsServiceImpl;
import com.example.bank.service.CardService;
import com.example.bank.service.TransferMoneyService;
import com.example.bank.service.UserService;
import com.example.bank.validator.TransferMoneyValidator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.runtime.ObjectMethods;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@Import(TransferMoneyValidator.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransferMoneyValidator transferMoneyValidator;
    @MockBean
    private UserService userService;
    @MockBean
    private CardService cardService;
    @MockBean
    private TransferMoneyService transferMoneyService;

    private CardEntity fromCard;
    private CardEntity toCard;


    @BeforeEach
    public void init() {
        final var fromUser = new UserEntity();
        fromCard = CardEntity.builder()
                .cardNumber("1000000000000000")
                .cardType(CardType.DEBIT)
                .paymentSystem(PaymentSystem.MIR)
                .cardStatus(CardStatus.ACTIVE)
                .userEntity(fromUser)
                .balance(1000).build();
        fromUser.setCardEntities(List.of(fromCard));

        final var toUser = new UserEntity();
        toUser.setPersonalDetailsEntity(new PersonalDetailsEntity());
        toCard = CardEntity.builder()
                .cardNumber("2000000000000000")
                .cardType(CardType.DEBIT)
                .paymentSystem(PaymentSystem.MIR)
                .cardStatus(CardStatus.ACTIVE)
                .userEntity(toUser)
                .balance(0).build();
    }

    @Test
    @WithMockUser(authorities = "MONEY_TRANSFER")
    public void getTransferByCardNumberPage_shouldReturnPage() throws Exception {
        when(cardService.findAllByUserEntityEmail(any())).thenReturn(fromCard.getUserEntity().getCardEntities());
        this.mockMvc.perform(get("/transfer/by-card-number"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("transferMoneyDTO", "selectCard"),
                        view().name("transfer")
                );
        verify(cardService, times(1)).findAllByUserEntityEmail(any());
    }

    @Test
    @WithMockUser(authorities = "MONEY_TRANSFER")
    public void getConfirmTransferPage_shouldReturnTransferPage_whenIncorrectData() throws Exception {
        final TransferMoneyDTO content = TransferMoneyDTO.builder()
                .toCardNumber(toCard.getCardNumber())
                .amount(10000)
                .fromCardEntity(fromCard).build();
        this.mockMvc.perform(get("/transfer/by-card-number/confirm")
                        .flashAttr("transferMoneyDTO", content))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeDoesNotExist("profile"),
                        view().name("transfer")
                );
    }

    @Test
    @WithMockUser(authorities = "MONEY_TRANSFER")
    public void getConfirmTransferPage_shouldReturnConfirmTransferPage_whenCorrectData() throws Exception {
        when(cardService.checkCorrectnessCardNumber(any())).thenReturn(true);
        final TransferMoneyDTO content = TransferMoneyDTO.builder()
                .toCardNumber(toCard.getCardNumber())
                .amount(100)
                .fromCardEntity(fromCard).build();
        when(cardService.findByCardNumber(toCard.getCardNumber())).thenReturn(toCard);
        this.mockMvc.perform(get("/transfer/by-card-number/confirm")
                        .flashAttr("transferMoneyDTO", content))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("profile"),
                        view().name("confirm-transfer")
                );
    }

    @Test
    @WithMockUser(authorities = "MONEY_TRANSFER")
    public void transferMoney_shouldRedirect() throws Exception {
        TransferMoneyDTO transferMoneyDTO = TransferMoneyDTO.builder()
                .toCardNumber(toCard.getCardNumber())
                .amount(100)
                .fromCardEntity(fromCard)
                .build();
        this.mockMvc.perform(post("/transfer/by-card-number/confirm")
                        .with(csrf())
                        .flashAttr("transferMoneyDTO", transferMoneyDTO))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/")
                );

        verify(transferMoneyService, times(1)).transferMoney(fromCard.getCardNumber(), toCard.getCardNumber(), 100);

    }

}