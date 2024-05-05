package com.example.bank.controller;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardType;
import com.example.bank.model.PaymentSystem;
import com.example.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@PreAuthorize("hasAuthority('ISSUE_CARD')")
@RequiredArgsConstructor
public class IssueCardController {
    private final CardService cardService;

    @GetMapping("/card-issue")
    public String getCardPage(Model model) {
        model.addAttribute("card",new IssueCardDTO(PaymentSystem.MIR, CardType.CREDIT));
        return "card-issue";
    }


    @PostMapping("/card-issue")
    public String issueCard(@AuthenticationPrincipal UserDetails userDetails,
                           @ModelAttribute("card") IssueCardDTO issueCardDTO) {
        cardService.createNewCard(userDetails.getUsername(), issueCardDTO);
        return "redirect:/";
    }
}
