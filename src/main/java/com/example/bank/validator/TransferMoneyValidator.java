package com.example.bank.validator;

import com.example.bank.service.exception.DAOException;
import com.example.bank.dto.TransferMoneyDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardType;
import com.example.bank.service.CardService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
        String cardNumber=transferMoneyDTO.getToCardNumber();
        long amount=transferMoneyDTO.getAmount();
        CardEntity card=transferMoneyDTO.getFromCardEntity();
        if(cardService.checkCorrectnessCardNumber(cardNumber)){
            try {
                cardService.findByCardNumber(cardNumber);
            }catch (DAOException ex){
                errors.rejectValue("toCardNumber","","User with such card number does not exist");
                transferMoneyDTO.setToCardNumber(null);
            }
            if(card.getCardType()==CardType.DEBIT&&card.getBalance()<amount){
                errors.rejectValue("fromCardEntity","","Insufficient funds");
            }

        }else{
            errors.rejectValue("toCardNumber","","Incorrect card number");
            transferMoneyDTO.setToCardNumber(null);
        }

    }
}
