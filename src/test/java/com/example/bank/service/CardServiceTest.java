package com.example.bank.service;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.*;
import com.example.bank.repository.CardRepository;
import com.example.bank.service.exception.DAOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    public void findAllCards_shouldCallRepo() throws Exception{
        cardService.findAll();
        verify(cardRepository, times(1)).findAll();
    }
    @Test
    public void findAllByCardStatusOrderByExpirationAscTest_shouldCallRepo() throws Exception{
        cardService.findAllByCardStatusOrderByExpirationAsc(CardStatus.UNDER_CONSIDERATION);
        verify(cardRepository, times(1)).findAllByCardStatusOrderByExpirationAsc(CardStatus.UNDER_CONSIDERATION);
    }

    @Test
    public void findAllByUserEntityEmail_shouldCallRepo() throws Exception{
        final var email="some@email.com";
        cardService.findAllByUserEntityEmail(email);
        verify(cardRepository, times(1)).findAllByUserEntityEmail(email);
    }

    @Test
    public void findByCardNumber_shouldReturnCard_whenExists() throws Exception{
        final var card=mock(CardEntity.class);
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));

        final var result = cardService.findByCardNumber(anyString());

        assertNotNull(result);
        verify(cardRepository,times(1)).findByCardNumber(anyString());
    }

    @Test
    public void findByCardNumber_shouldThrowDAOException_whenNotExists() throws Exception{
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->cardService.findByCardNumber(anyString()));

        verify(cardRepository,times(1)).findByCardNumber(anyString());
    }
    @Test
    public void findById_shouldReturnCard_whenExists() throws Exception{
        final var card=mock(CardEntity.class);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));

        final var result = cardService.findById(anyLong());

        assertNotNull(result);
        verify(cardRepository,times(1)).findById(anyLong());
    }

    @Test
    public void findById_shouldThrowDAOException_whenNotExists() throws Exception{
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->cardService.findById(anyLong()));

        verify(cardRepository,times(1)).findById(anyLong());
    }

    @Test
    public void activateCard_shouldChangeCardStatus_whenExists() throws Exception{
        final var card=new CardEntity();
        card.setCardStatus(CardStatus.BANNED);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));

        final var result = cardService.activateCard(anyLong());

        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE,card.getCardStatus());
        verify(cardRepository,times(1)).findById(anyLong());
    }

    @Test
    public void activateCard_shouldThrowDAOException_whenNotExists() throws Exception{
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->cardService.activateCard(anyLong()));

        verify(cardRepository,times(1)).findById(anyLong());
    }

    @Test
    public void deactivateCard_shouldChangeCardStatus_whenExists() throws Exception{
        final var card=new CardEntity();
        card.setCardStatus(CardStatus.ACTIVE);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card));

        final var result = cardService.deactivateCard(anyLong());

        assertNotNull(result);
        assertEquals(CardStatus.BANNED,card.getCardStatus());
        verify(cardRepository,times(1)).findById(anyLong());
    }

    @Test
    public void deactivateCard_shouldThrowDAOException_whenNotExists() throws Exception{
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->cardService.deactivateCard(anyLong()));

        verify(cardRepository,times(1)).findById(anyLong());
    }



    @Test
    public void delete_shouldCallRepo(){
        final var card=mock(CardEntity.class);
        cardService.delete(card);
        verify(cardRepository, times(1)).delete(card);
    }


    @Test
    public void createNewCard_shouldReturnNewCard() throws Exception{

        final var lastCardInDB=new CardEntity();
        final var BIN="82390";
        final var lastCardID="1000000010";
        lastCardInDB.setCardNumber(PaymentSystem.MIR.getCode()+BIN+lastCardID);

        final var email="some@email";
        final var user=mock(UserEntity.class);

        when(cardRepository.findTopByOrderByIdDesc()).thenReturn(lastCardInDB);
        when(userService.findByEmail(email)).thenReturn(user);

        cardService.createNewCard(email,new IssueCardDTO(PaymentSystem.MIR,CardType.DEBIT));

        final ArgumentCaptor<CardEntity> captor=ArgumentCaptor.forClass(CardEntity.class);
        verify(cardRepository).save(captor.capture());
        final CardEntity resultCard=captor.getValue();

        assertNotNull(resultCard.getCardNumber());
        assertNotNull(resultCard.getCardStatus());
        assertNotNull(resultCard.getExpiration());
        assertNotNull(resultCard.getCvv());
        assertNotNull(resultCard.getUserEntity());

        //correctCardNumber
        final var expected=PaymentSystem.MIR.getCode()+BIN+"1000000026";
        assertEquals(expected,resultCard.getCardNumber());

        //correctLength
        assertEquals(16,resultCard.getCardNumber().length());

        verify(userService,times(1)).findByEmail(email);
        verify(cardRepository,times(1)).save(resultCard);
        verify(cardRepository,times(1)).findTopByOrderByIdDesc();

    }

    @Test
    public void checkCorrectnessCardNumber_shouldReturnTrue() throws Exception{
        final var correctCardNumber="6823901000000027";
        final boolean result = cardService.checkCorrectnessCardNumber(correctCardNumber);
        assertTrue(result);
    }
    @Test
    public void checkCorrectnessCardNumber_shouldReturnFalse() throws Exception{
        final var incorrectCheckSum="6823901000000026";
        final boolean result = cardService.checkCorrectnessCardNumber(incorrectCheckSum);
        assertFalse(result);
    }


}
