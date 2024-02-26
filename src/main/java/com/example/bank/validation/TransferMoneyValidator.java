package com.example.bank.validation;

import com.example.bank.customexception.CardNotFoundException;
import com.example.bank.customexception.DAOException;
import com.example.bank.dto.TransferMoneyDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardType;
import com.example.bank.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class TransferMoneyValidator implements Validator {

    private final CardService cardService;

    @Override
    public boolean supports(Class<?> clazz) {
        return TransferMoneyDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TransferMoneyDTO transferMoneyDTO=(TransferMoneyDTO) target;
        String cardNumber=transferMoneyDTO.getCardNumber();
        Long amount=transferMoneyDTO.getAmount();
        CardEntity card=transferMoneyDTO.getCardEntity();
        if(cardService.checkCorrectnessCardNumber(cardNumber)){
            try {
                cardService.findByCardNumber(cardNumber);
            }catch (DAOException ex){
                errors.rejectValue("cardNumber","","User with such card number does not exist");
                transferMoneyDTO.setCardNumber(null);
            }
            if(card.getCardType()==CardType.DEBIT&&card.getBalance()<amount){
                errors.rejectValue("cardEntity","","Insufficient funds");
            }

        }else{
            errors.rejectValue("cardNumber","","Incorrect card number");
            transferMoneyDTO.setCardNumber(null);
        }

    }
}
