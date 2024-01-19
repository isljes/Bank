package com.example.bank.controllers;

import com.example.bank.model.UserEntity;
import com.example.bank.services.CardService;
import com.example.bank.services.SecurityService;
import com.example.bank.services.UserService;
import com.example.bank.validation.RegistrationValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class SecurityController {

    private final RegistrationValidator registrationValidator;
    private final UserService userService;
    private final SecurityService securityService;
    private final CardService cardService;


    @GetMapping("/login")
    String login(HttpServletRequest request, Model model) {
        model.addAttribute("sessionId", request.getSession().getId());
        return "login";
    }


    @GetMapping("/welcome")
    @PreAuthorize("authentication.authenticated")
    public String getWelcomePage(@AuthenticationPrincipal UserDetails userDetails,
                                 Model model,
                                 HttpServletRequest request) {
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("allCards", cardService.findAll());
        model.addAttribute("userEntityUsername", userDetails.getUsername());
        model.addAttribute("currentID", userService.findByEmail(userDetails.getUsername()).getId());
        model.addAttribute("sessionID", request.getSession().getId());
        return "welcome";
    }


    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("userEntity", new UserEntity());
        return "registration";
    }


    @PostMapping("/registration")
    public String createNewUser(@Valid UserEntity userEntity,
                                BindingResult bindingResult,
                                HttpServletRequest request) {
        registrationValidator.validate(userEntity, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        String email = userEntity.getEmail();
        String password = userEntity.getPassword();
        userService.createNewUserAfterRegistration(userEntity);
        securityService.autoLogin(email, password, userEntity.getRole().getAuthorities(), request);
        return "redirect:/welcome";
    }





}
