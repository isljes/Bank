package com.example.bank.validator;

import com.example.bank.service.exception.DAOException;
import com.example.bank.dto.TransferMoneyDto;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardType;
import com.example.bank.service.CardService;
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
        return TransferMoneyDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TransferMoneyDto transferMoneyDTO=(TransferMoneyDto) target;

        String toCardNumber=transferMoneyDTO.getToCardNumber();
        String fromCardNumber=transferMoneyDTO.getFromCardNumber();
        long amount=transferMoneyDTO.getAmount();

        CardEntity fromCard = cardService.findByCardNumber(fromCardNumber);

        if(cardService.checkCorrectnessCardNumber(toCardNumber)){
            try {
                cardService.findByCardNumber(toCardNumber);
            }catch (DAOException ex){
                errors.rejectValue("toCardNumber","","User with such card number does not exist");
                transferMoneyDTO.setToCardNumber(null);
            }
            if(fromCard.getBalance()<amount){
                errors.rejectValue("fromCardNumber","","Insufficient funds");
            }

        }else{
            errors.rejectValue("toCardNumber","","Incorrect card number");
            transferMoneyDTO.setToCardNumber(null);
        }

    }
}
