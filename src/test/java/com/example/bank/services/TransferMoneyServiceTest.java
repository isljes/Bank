package com.example.bank.services;

import com.example.bank.model.CardEntity;
import com.example.bank.repositories.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceTest {

    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private TransferMoneyService transferMoneyService;

    @Test
    public void transferMoneyTest_Should_Return_True() {
        var fromCard=new CardEntity();
        var toCard=new CardEntity();
        fromCard.setBalance(100);
        toCard.setBalance(0);
        doReturn(Optional.of(fromCard)).when(cardRepository).findByCardNumber("from");
        doReturn(Optional.of(toCard)).when(cardRepository).findByCardNumber("to");

        boolean isTransferred=transferMoneyService.transferMoney("from","to",100);

        assertEquals(0,fromCard.getBalance());
        assertEquals(100,toCard.getBalance());
        assertTrue(isTransferred);

        verify(cardRepository,times(1)).findByCardNumber("from");
        verify(cardRepository,times(1)).findByCardNumber("to");
    }

    @Test
    public void transferMoneyTest_Should_Return_False_If_Card_IsNotPresent() {
        var toCard=new CardEntity();
        toCard.setBalance(0);
        doReturn(Optional.empty()).when(cardRepository).findByCardNumber("from");
        doReturn(Optional.of(toCard)).when(cardRepository).findByCardNumber("to");

        boolean isTransferred=transferMoneyService.transferMoney("from","to",100);

        assertEquals(0,toCard.getBalance());
        assertFalse(isTransferred);

        verify(cardRepository,times(1)).findByCardNumber("from");
        verify(cardRepository,times(1)).findByCardNumber("to");
    }

    @Test
    public void transferMoneyTest_Should_Return_False_If_Balance_LessAmount() {
        var fromCard=new CardEntity();
        var toCard=new CardEntity();
        fromCard.setBalance(100);
        toCard.setBalance(0);
        doReturn(Optional.of(fromCard)).when(cardRepository).findByCardNumber("from");
        doReturn(Optional.of(toCard)).when(cardRepository).findByCardNumber("to");

        boolean isTransferred=transferMoneyService.transferMoney("from","to",200);

        assertEquals(100,fromCard.getBalance());
        assertEquals(0,toCard.getBalance());
        assertFalse(isTransferred);

        verify(cardRepository,times(1)).findByCardNumber("from");
        verify(cardRepository,times(1)).findByCardNumber("to");
    }
}