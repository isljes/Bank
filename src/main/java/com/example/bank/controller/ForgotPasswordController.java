package com.example.bank.controller;

import com.example.bank.model.UserEntity;
import com.example.bank.service.MailSenderService;
import com.example.bank.service.SecurityService;
import com.example.bank.service.UserService;
import com.example.bank.validator.ChangePasswordValidator;
import com.example.bank.validator.ForgotPasswordValidator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordValidator forgotPasswordValidator;
    private final ChangePasswordValidator changePasswordValidator;
    private final UserService userService;
    private final SecurityService securityService;
    private final MailSenderService mailSenderService;


    @GetMapping("/forgot-password")
    public String getForgotPasswordPage(Model model) {
        model.addAttribute("userEntity", new UserEntity());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendLinkForChangePassword(@ModelAttribute UserEntity user,
                                            BindingResult bindingResult,
                                            Model model) {

        forgotPasswordValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }
        model.addAttribute("response", "Link to change your password has been sent to your email");
        mailSenderService.sendLinkToChangePassword(user.getEmail());
        return "response";
    }

    @GetMapping("/forgot-password/reset-password")
    public String getResetPasswordPage(@RequestParam(value = "email") String email,
                                        @RequestParam(value = "confirmation-code") String confirmationCode,
                                        Model model) {
        UserEntity user = userService.findByEmail(email);

        if (!confirmationCode.equals(user.getConfirmationCode())) {
            model.addAttribute("response", "Confirmation code is expired");
            return "response";
        }
        user.setPassword("");
        model.addAttribute("userEntity", user);
        return "reset-password";
    }

    @PostMapping("/forgot-password/reset-password")
    public String resetPassword(@ModelAttribute @Valid UserEntity userEntity,
                                BindingResult bindingResult,
                                Model model) {
        changePasswordValidator.validate(userEntity, bindingResult);
        if (bindingResult.hasErrors()) {
            return "reset-password";
        }
        securityService.changePassword(userEntity.getEmail(), userEntity.getPassword());
        model.addAttribute("response", "Password successful changed ");
        return "response";
    }
}
