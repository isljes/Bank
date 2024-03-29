package com.example.bank.controllers;

import com.example.bank.model.PaymentSystem;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import com.example.bank.services.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CardService cardService;
    private final UserService userService;
    private final PersonalDetailsService personalDetailsService;
    private final MailSenderService mailSenderService;
    private final SecurityService securityService;


    @GetMapping("/addCard")
    @PreAuthorize("hasAuthority('ISSUE_CARD')")
    public String getCardPage() {
        return "card-issue";
    }


    @PostMapping("/addCard")
    @PreAuthorize("hasAuthority('ISSUE_CARD')")
    public String saveCard(@AuthenticationPrincipal UserDetails userDetails,
                           @ModelAttribute("pay") PaymentSystem paymentSystem) {
        cardService.createNewCard(userDetails.getUsername(), paymentSystem);
        return "redirect:/welcome";
    }
    @ModelAttribute
    public void profilePage(Model model,@AuthenticationPrincipal UserDetails userDetails){
        UserEntity user= userService.findByEmail(userDetails.getUsername());
        model.addAttribute("profile", user.getPersonalDetailsEntity());
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
        //mailSenderService.sendLinkToConfirmEmail(email);
        return "redirect:/profile";
    }

    @GetMapping("/confirm-email")
    @PreAuthorize("hasAuthority('CONFIRM_EMAIL')")
    public String confirmEmail(@RequestParam String confirmationCode,
                               @RequestParam String email,
                               Model model){
        boolean isConfirmed=userService.confirmEmail(email, confirmationCode);

        if(!isConfirmed){
            model.addAttribute("response","Confirmation code is expired");
            return "response";
        }
        return "redirect:/profile";
    }





}
