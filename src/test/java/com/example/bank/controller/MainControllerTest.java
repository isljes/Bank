package com.example.bank.controller;

import com.example.bank.dto.CurrencyRateDTO;
import com.example.bank.dto.SelectedCurrenciesDTO;
import com.example.bank.model.*;
import com.example.bank.service.CardService;
import com.example.bank.service.CurrencyRateService;
import com.example.bank.service.TransactionHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
@Import(SpringDataJacksonConfiguration.class)
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MainController mainController;
    @MockBean
    private CurrencyRateService currencyRateService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TransactionHistoryService transactionHistoryService;
    @MockBean
    private CardService cardService;

    private UserEntity userEntity;
    private TransactionHistoryEntity transactionHistoryEntity;

    @BeforeEach
    public void init() {
        final var email = "some@email.com";

        final var card = CardEntity.builder()
                .cardNumber("1111111111111111")
                .cardStatus(CardStatus.ACTIVE)
                .cardType(CardType.DEBIT)
                .balance(100)
                .paymentSystem(PaymentSystem.MIR).build();

        final var transactionHistoryEntity = TransactionHistoryEntity.builder().card(card).build();

        card.setTransactionHistoryEntity(List.of(transactionHistoryEntity));

        final var user = UserEntity.builder()
                .email(email)
                .confirmationCode(UUID.randomUUID().toString())
                .cardEntities(List.of(card)).build();

        final var personalDetails = PersonalDetailsEntity.builder().userEntity(user).build();
        user.setPersonalDetailsEntity(personalDetails);
        userEntity = user;
        this.transactionHistoryEntity = transactionHistoryEntity;
    }

    @Test
    @WithMockUser
    public void getWelcomePage_shouldReturnPageAndSetCookie_whenNotExists() throws Exception {
        final CurrencyRateDTO.Currency eur = new CurrencyRateDTO.Currency("eur", "euro", 1.0, 98.00, 97.00);
        final CurrencyRateDTO.Currency usd = new CurrencyRateDTO.Currency("usd", "usd", 1.0, 68.00, 67.00);
        Map<String, CurrencyRateDTO.Currency> allCurrencies = Map.of("eur", eur, "usd", usd);

        when(cardService.findAllByUserEntityEmail(anyString())).thenReturn(userEntity.getCardEntities());
        when(currencyRateService.findAll()).thenReturn(allCurrencies);
        when(currencyRateService.findCurrencyByCharCode("EUR")).thenReturn(eur);
        when(currencyRateService.findCurrencyByCharCode("USD")).thenReturn(usd);
        when(transactionHistoryService.findAllByCardAndTimestampBetween(any(), any(), any())).thenReturn(List.of(transactionHistoryEntity));
        setField(mainController, "timeStampPatternOnlyTime", "HH:mm");

        this.mockMvc.perform(
                        get("/"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("welcome"),
                        cookie().exists("selectedCurrencyRates"),
                        model().attributeExists("myCards", "allCurrenciesCharCodes", "selectedCurrenciesDTO", "selectedCurrencies",
                                "transactionHistoryLast24h", "timePattern")

                );
        verify(cardService, atLeastOnce()).findAllByUserEntityEmail(anyString());
        verify(currencyRateService, atLeastOnce()).findAll();
        verify(currencyRateService, atLeastOnce()).findCurrencyByCharCode(anyString());
        verify(transactionHistoryService, atLeastOnce()).findAllByCardAndTimestampBetween(any(), any(), any());

    }

    @Test
    @WithMockUser
    public void getWelcomePage_shouldReturnPageAndReadCookie_whenExists() throws Exception {
        final CurrencyRateDTO.Currency eur = new CurrencyRateDTO.Currency("eur", "euro", 1.0, 98.00, 97.00);
        final CurrencyRateDTO.Currency usd = new CurrencyRateDTO.Currency("usd", "usd", 1.0, 68.00, 67.00);
        Map<String, CurrencyRateDTO.Currency> allCurrencies = Map.of("eur", eur, "usd", usd);

        when(cardService.findAllByUserEntityEmail(anyString())).thenReturn(userEntity.getCardEntities());
        when(currencyRateService.findAll()).thenReturn(allCurrencies);
        when(currencyRateService.findCurrencyByCharCode("EUR")).thenReturn(eur);
        when(currencyRateService.findCurrencyByCharCode("USD")).thenReturn(usd);
        when(transactionHistoryService.findAllByCardAndTimestampBetween(any(), any(), any())).thenReturn(List.of(transactionHistoryEntity));
        setField(mainController, "timeStampPatternOnlyTime", "HH:mm");

        Cookie cookie = new Cookie("selectedCurrencyRates", URLEncoder.encode(
                objectMapper.writeValueAsString(new SelectedCurrenciesDTO(
                        new String[]{"EUR", "USD"})
                ), StandardCharsets.UTF_8)
        );


        this.mockMvc.perform(
                        get("/")
                                .cookie(cookie))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("welcome"),
                        model().attributeExists("myCards", "allCurrenciesCharCodes",
                                "selectedCurrenciesDTO", "selectedCurrencies", "transactionHistoryLast24h",
                                "timePattern")
                );
        verify(cardService, atLeastOnce()).findAllByUserEntityEmail(anyString());
        verify(currencyRateService, atLeastOnce()).findAll();
        verify(currencyRateService, atLeastOnce()).findCurrencyByCharCode(anyString());
        verify(transactionHistoryService, atLeastOnce()).findAllByCardAndTimestampBetween(any(), any(), any());

    }

    @Test
    @WithMockUser
    public void updateCookie_shouldUpdateCurrencyRateCookie() throws Exception {
        final var newCookieContent = new SelectedCurrenciesDTO(new String[]{"USD"});
        final Cookie oldCookie = new Cookie("selectedCurrencyRates",
                URLEncoder.encode(objectMapper.writeValueAsString(
                        new SelectedCurrenciesDTO(new String[]{"EUR", "USD"})
                ), StandardCharsets.UTF_8)
        );

        this.mockMvc.perform(
                        post("/update-currency")
                                .cookie(oldCookie)
                                .flashAttr("selectedCurrenciesDTO", newCookieContent)
                                .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/"),
                        cookie().value("selectedCurrencyRates",
                                URLEncoder.encode(objectMapper.writeValueAsString(newCookieContent)))
                );

    }

}
