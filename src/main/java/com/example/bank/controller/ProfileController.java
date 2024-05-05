package com.example.bank.controller;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.*;
import com.example.bank.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final PersonalDetailsService personalDetailsService;
    private final MailSenderService mailSenderService;
    private final SecurityService securityService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('VIEW_ONLY_HIS_PROFILE')")
    public String getProfilePage(Model model,@AuthenticationPrincipal UserDetails userDetails) {
        PersonalDetailsEntity personalDetailsEntity= personalDetailsService.findByUserEntityEmail(userDetails.getUsername());
        model.addAttribute("profile", personalDetailsEntity);
        return "profile";
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('VIEW_ONLY_HIS_PROFILE')")
    public String changePersonalDetails(@ModelAttribute("profile") PersonalDetailsEntity personalDetails) {
        personalDetailsService.update(personalDetails);
        return "redirect:/profile";
    }

    @PostMapping("/confirm-email")
    @PreAuthorize("hasAuthority('CONFIRM_EMAIL')")
    public String sendConfirmationCode(@AuthenticationPrincipal UserDetails userDetails) {
        PersonalDetailsEntity personalDetails= personalDetailsService.findByUserEntityEmail(userDetails.getUsername());
        String email=personalDetails.getUserEntity().getEmail();
        mailSenderService.sendLinkToConfirmEmail(email);
        return "redirect:/profile";
    }

    @GetMapping("/confirm-email")
    @PreAuthorize("hasAuthority('CONFIRM_EMAIL')")
    public String confirmEmail(@RequestParam(name = "confirmation-code") String confirmationCode,
                               @RequestParam(name = "email") String email,
                               Model model){
        boolean isConfirmed=securityService.confirmEmail(email, confirmationCode);

        if(!isConfirmed){
            model.addAttribute("response","Confirmation code is expired");
            return "response";
        }
        return "redirect:/profile";
    }


}
