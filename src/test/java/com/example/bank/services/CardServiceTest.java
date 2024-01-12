package com.example.bank.services;

import com.example.bank.custom_exception.CardNotFoundException;
import com.example.bank.model.CardEntity;
import com.example.bank.model.PaymentSystem;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
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
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CardService cardService;

    @Test
    public void findAllCardsTest() throws Exception{
        cardService.findAll();
        verify(cardRepository, times(1).description("cardRepository.findAll() must be invoke 1 time")).findAll();
    }

    @Test
    public void findCardByCardNumberTest_ReturnsCard() throws Exception{
        var cardNumber="236145543216";
        doReturn(Optional.of(new CardEntity()))
                .when(cardRepository).findByCardNumber(cardNumber);
        CardEntity result = cardService.findByCardNumber(cardNumber);
        assertNotNull(result);
        verify(cardRepository,times(1).description("cardRepository.findByCardNumber() must be invoke 1 time")).findByCardNumber(cardNumber);
    }

    @Test
    public void findCardByCardNumberTest_ThrowsCardNotFoundException() throws Exception{
        var cardNumber="236145543216";
        doReturn(Optional.empty())
                .when(cardRepository).findByCardNumber(cardNumber);
        assertThrows(CardNotFoundException.class,()->cardService.findByCardNumber(cardNumber));
        verify(cardRepository,times(1).description("cardRepository.findByCardNumber() must be invoke 1 time")).findByCardNumber(cardNumber);
    }

    @Test
    public void findCardByIdTest_ReturnsCard() throws Exception{
        var id=1L;
        doReturn(Optional.of(new CardEntity()))
                .when(cardRepository).findById(id);
        CardEntity result = cardService.findById(id);
        assertNotNull(result);
        verify(cardRepository,times(1).description("cardRepository.findById() must be invoke 1 time")).findById(id);
    }

    @Test
    public void findByCardNumberTest_ThrowCardNotFoundException() throws Exception{
        var id=1L;
        doReturn(Optional.empty())
                .when(cardRepository).findById(id);
        assertThrows(CardNotFoundException.class,()->cardService.findById(id));
        verify(cardRepository,times(1).description("cardRepository.findById() must be invoke 1 time")).findById(id);
    }

    @Test
    public void activateCardTest_Positive() throws Exception{
        var id=1L;
        CardEntity cardEntity=new CardEntity();
        doReturn(Optional.of(cardEntity))
                .when(cardRepository)
                .findById(id);
        cardService.activateCard(id);
        assertEquals(cardEntity.getStatus(), Status.ACTIVE);
        verify(cardRepository,times(1).description("cardRepository.save() must be invoke 1 time")).save(cardEntity);
    }

    @Test
    public void activateCardTest_ThrowsCardNotFoundException() throws Exception{
        var id=1L;
        doReturn(Optional.empty())
                .when(cardRepository)
                .findById(id);
        assertThrows(CardNotFoundException.class,()->cardService.activateCard(id));
        verify(cardRepository,times(0).description("cardRepository.save() must be invoke 0 time")).save(new CardEntity());
    }

    @Test
    public void deactivateCardTest_Positive() throws Exception{
        var id=1L;
        CardEntity cardEntity=new CardEntity();
        doReturn(Optional.of(cardEntity))
                .when(cardRepository)
                .findById(id);
        cardService.deactivateCard(id);
        assertEquals(cardEntity.getStatus(), Status.NOT_ACTIVE);
        verify(cardRepository,times(1).description("cardRepository.save() must be invoke 1 time")).save(cardEntity);
    }

    @Test
    public void deactivateCardTest_ThrowsCardNotFoundException() throws Exception{
        var id=1L;
        doReturn(Optional.empty())
                .when(cardRepository)
                .findById(id);
        assertThrows(CardNotFoundException.class,()->cardService.deactivateCard(id));
        verify(cardRepository,times(0).description("cardRepository.save() must be invoke 0 time")).save(new CardEntity());
    }


    @Test
    public void CreateNewCard_Test() throws Exception{

        String BIN="82390";
        String lastCardIdInDb="1000000010";
        CardEntity card=new CardEntity();

        card.setCardNumber(PaymentSystem.MIR.getCode()+BIN+lastCardIdInDb);

        doReturn(card)
                .when(cardRepository)
                .findTopByOrderByIdDesc();
        doReturn(new UserEntity())
                .when(userService)
                .findByEmail("some_email");

        CardEntity resultCard=cardService.createNewCard("some_email",PaymentSystem.MIR);

        //setters
        assertNotNull(resultCard.getCardNumber(),"Card number must be not null");
        assertNotNull(resultCard.getStatus(),"Status must be not null");
        assertNotNull(resultCard.getDate(),"Date must be not null");
        assertNotNull(resultCard.getCvv(),"CVV must be not null");
        assertNotNull(resultCard.getUserEntity(),"UserEntity must be not null");

        //correctCheksum
        String expected=PaymentSystem.MIR.getCode()+BIN+"1000000026";
        assertEquals(expected,resultCard.getCardNumber());

        //correctLength
        assertEquals(16,resultCard.getCardNumber().length(),"Generated card number must be 16 character long");

        //verify()
        verify(userService,times(1).description("userService.findByEmail() must be invoke 1 time")).findByEmail("some_email");
        verify(cardRepository,times(1).description("cardRepository.save() must be invoke 1 time")).save(resultCard);
        verify(cardRepository,times(1).description("cardRepository.findTopByOrderByIdDesc() must be invoke 1 time")).findTopByOrderByIdDesc();

    }


}
