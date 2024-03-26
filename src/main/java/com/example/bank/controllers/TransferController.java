package com.example.bank.controllers;

import com.example.bank.dto.TransferMoneyDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.services.CardService;
import com.example.bank.services.TransferMoneyService;
import com.example.bank.services.UserService;
import com.example.bank.validation.TransferMoneyValidator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final UserService userService;
    private final CardService cardService;
    private final TransferMoneyValidator transferMoneyValidator;
    private final TransferMoneyService transferMoneyService;

    @ModelAttribute
    public void transferPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity currentUser = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("transferMoneyDTO", new TransferMoneyDTO());
        model.addAttribute("selectCard", currentUser.getCardEntities());
    }

    @GetMapping("/by-card-number")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String getTransferPageByCardNumber() {
        return "transfer";
    }


    @PostMapping("/by-card-number")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String validateAndGetConfirmPage(@ModelAttribute("transferMoneyDTO") TransferMoneyDTO transferMoneyDTO,
                                            BindingResult bindingResult,
                                            Model model) {
        transferMoneyValidator.validate(transferMoneyDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return "transfer";
        }
        CardEntity toCard = cardService.findByCardNumber(transferMoneyDTO.getCardNumber());
        model.addAttribute("profile", toCard.getUserEntity().getPersonalDetailsEntity());
        return "confirm-transfer";
    }


    @PostMapping("/by-card-number/confirm")
    @PreAuthorize("hasAuthority('MONEY_TRANSFER')")
    public String transferMoney(@ModelAttribute("transferMoneyDTO") TransferMoneyDTO transferMoneyDTO,
                                RedirectAttributes redirectAttributes) {
        String from = transferMoneyDTO.getCardEntity().getCardNumber();
        String to = transferMoneyDTO.getCardNumber();
        long amount = transferMoneyDTO.getAmount();
        transferMoneyService.transferMoney(from, to, amount);
        return "redirect:/welcome";
    }

}
