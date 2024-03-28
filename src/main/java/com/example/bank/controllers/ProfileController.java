package com.example.bank.controllers;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.*;
import com.example.bank.services.*;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CardService cardService;
    private final UserService userService;
    private final PersonalDetailsService personalDetailsService;
    private final MailSenderService mailSenderService;
    private final ConfirmationService confirmationService;


    @GetMapping("/card-issue")
    @PreAuthorize("hasAuthority('ISSUE_CARD')")
    public String getCardPage(Model model) {
        model.addAttribute("card",new IssueCardDTO(PaymentSystem.MIR, CardType.CREDIT));
        return "card-issue";
    }


    @PostMapping("/card-issue")
    @PreAuthorize("hasAuthority('ISSUE_CARD')")
    public String saveCard(@AuthenticationPrincipal UserDetails userDetails,
                           @ModelAttribute("card") IssueCardDTO issueCardDTO) {
        cardService.createNewCard(userDetails.getUsername(), issueCardDTO);
        return "redirect:/welcome";

    }

    @ModelAttribute
    public void profilePage(Model model,@AuthenticationPrincipal UserDetails userDetails){
        UserEntity user= userService.findByEmail(userDetails.getUsername());
        model.addAttribute("profile", user.getPersonalDetailsEntity());
        model.addAttribute("cards",user.getCardEntities());
    }


    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('VIEW_ONLY_HIS_PROFILE')")
    public String getProfilePage() {
        return "profile";
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('VIEW_ONLY_HIS_PROFILE')")
    public String alterProfile(@ModelAttribute("profile") PersonalDetailsEntity personalDetails) {
        personalDetailsService.update(personalDetails);
        return "redirect:/profile";
    }

    @PostMapping("/confirm-email")
    @PreAuthorize("hasAuthority('CONFIRM_EMAIL')")
    public String sendConfirmationCode(@ModelAttribute("profile") PersonalDetailsEntity personalDetails) {
        String email=personalDetails.getUserEntity().getEmail();
        mailSenderService.sendLinkToConfirmEmail(email);
        return "redirect:/profile";
    }

    @GetMapping("/confirm-email")
    @PreAuthorize("hasAuthority('CONFIRM_EMAIL')")
    public String confirmEmail(@RequestParam(name = "confirmation-code") String confirmationCode,
                               @RequestParam(name = "email") String email,
                               Model model){
        boolean isConfirmed=confirmationService.confirmEmail(email, confirmationCode);

        if(!isConfirmed){
            model.addAttribute("response","Confirmation code is expired");
            return "response";
        }
        return "redirect:/profile";
    }





}
