package com.example.bank.controller;

import com.example.bank.dto.TransferMoneyDto;
import com.example.bank.model.CardEntity;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.service.CardService;
import com.example.bank.service.TransferMoneyService;
import com.example.bank.validator.TransferMoneyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final CardService cardService;
    private final TransferMoneyValidator transferMoneyValidator;
    private final TransferMoneyService transferMoneyService;

    @ModelAttribute
    public void modelInit(Model model,
                          @AuthenticationPrincipal UserDetails userDetails){
        model.addAttribute("selectCard", cardService.findAllByUserEntityEmail(userDetails.getUsername()));
    }

    @GetMapping("/by-card-number")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String getTransferByCardNumberPage(Model model,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("transferMoneyDTO",new TransferMoneyDto());

        return "transfer";
    }


    @GetMapping("/by-card-number/confirm")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String getConfirmTransferPage(@ModelAttribute("transferMoneyDTO") TransferMoneyDto transferMoneyDTO,
                                            BindingResult bindingResult,
                                            Model model) {
        transferMoneyValidator.validate(transferMoneyDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return "transfer";
        }
        CardEntity toCard=cardService.findByCardNumber(transferMoneyDTO.getFromCardNumber());
        PersonalDetailsEntity profile=toCard.getUserEntity().getPersonalDetailsEntity();
        model.addAttribute("profile", profile);

        return "confirm-transfer";
    }


    @PostMapping("/by-card-number/confirm")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String transferMoney(@ModelAttribute("transferMoneyDTO") TransferMoneyDto transferMoneyDTO) {
        transferMoneyService.transferMoney(transferMoneyDTO);
        return "redirect:/";
    }

}
