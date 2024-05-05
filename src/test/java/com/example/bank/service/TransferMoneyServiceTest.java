package com.example.bank.service;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceTest {

    @Mock
    private CardService cardService;
    @Mock
    private TransactionHistoryService transactionHistoryService;
    @InjectMocks
    private TransferMoneyServiceImpl transferMoneyService;

    @Test
    public void transferMoney_shouldExecuteSuccess() {
        final var fromCard= CardEntity.builder()
                .balance(100).cardNumber("from").build(); 
        final var toCard= CardEntity.builder()
                .balance(0).cardNumber("to").build();
        when(cardService.findByCardNumber("from")).thenReturn(fromCard);
        when(cardService.findByCardNumber("to")).thenReturn(toCard);

        transferMoneyService.transferMoney("from","to",100);

        final var captor = ArgumentCaptor.forClass(TransactionHistoryEntity.class);
        verify(transactionHistoryService,times(2)).save(captor.capture());
        final var fromTHE=captor.getAllValues().get(0);
        final var toTHE=captor.getAllValues().get(1);

        assertEquals("from",fromTHE.getCard().getCardNumber());
        assertEquals("to",toTHE.getCard().getCardNumber());
        assertEquals(-100,fromTHE.getAmount());
        assertEquals(+100,toTHE.getAmount());
        assertNotNull(fromTHE.getTimestamp());
        assertNotNull(toTHE.getTimestamp());

        assertEquals(0,fromCard.getBalance());
        assertEquals(100,toCard.getBalance());

        verify(cardService,times(1)).findByCardNumber(fromCard.getCardNumber());
        verify(cardService,times(1)).findByCardNumber(toCard.getCardNumber());
    }


    @Test
    public void transferMoney_shouldExecuteNegatively_whenBalanceLessAmount(){
        final var fromCard = CardEntity.builder()
                .balance(100).cardNumber("from").build();
        final var toCard = CardEntity.builder()
                .balance(0).cardNumber("to").build();
        when(cardService.findByCardNumber("from")).thenReturn(fromCard);
        when(cardService.findByCardNumber("to")).thenReturn(toCard);

        transferMoneyService.transferMoney("from", "to", 101);

        final var captor = ArgumentCaptor.forClass(TransactionHistoryEntity.class);
        verify(transactionHistoryService, times(0)).save(captor.capture());
        assertEquals(100, fromCard.getBalance());
        assertEquals(0, toCard.getBalance());

        verify(cardService,times(1)).findByCardNumber(fromCard.getCardNumber());
        verify(cardService,times(1)).findByCardNumber(toCard.getCardNumber());
    }

}