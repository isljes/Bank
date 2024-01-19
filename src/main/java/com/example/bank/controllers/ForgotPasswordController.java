package com.example.bank.controllers;

import com.example.bank.model.UserEntity;
import com.example.bank.services.MailSenderService;
import com.example.bank.services.UserService;
import com.example.bank.validation.ChangePasswordValidator;
import com.example.bank.validation.ForgotPasswordValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final MailSenderService mailSenderService;



    @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        UserEntity userEntity=new UserEntity();
        userEntity.setPassword(UUID.randomUUID().toString());
        model.addAttribute("userEntity",userEntity);
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendLinkForChangePassword(@Valid UserEntity user,
                                            BindingResult bindingResult,
                                            Model model){

        forgotPasswordValidator.validate(user,bindingResult);
        if(bindingResult.hasErrors()){
            return "forgot-password";
        }
        model.addAttribute("response","Link to change your password has been sent to your email");
        mailSenderService.sendLinkToChangePassword(user.getEmail());
        return "response";
    }

    @GetMapping("forgot-password/reset-password")
    public String checkValidConfirmCode(@RequestParam(value = "email") String email,
                                        @RequestParam(value = "confirmation-code") String confirmationCode,
                                        Model model){
        UserEntity user=userService.findByEmail(email);

        if(!confirmationCode.equals(user.getConfirmationCode())){
            model.addAttribute("response","Confirmation code is expired");
            return "response";
        }
        model.addAttribute("user",user);
        return "reset-password";
    }

    @PostMapping("forgot-password/reset-password")
    public String resetPassword(@ModelAttribute("user") @Valid UserEntity userEntity,
                                BindingResult bindingResult,
                                Model model){
        changePasswordValidator.validate(userEntity,bindingResult);
        if(bindingResult.hasErrors()){
            return "reset-password";
        }
        userService.changePassword(userEntity.getEmail(), userEntity.getPassword());
        model.addAttribute("response","Password successful changed ");
        return "response";
    }
}
