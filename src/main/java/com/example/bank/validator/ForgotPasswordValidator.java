package com.example.bank.validator;

import com.example.bank.model.UserEntity;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ForgotPasswordValidator implements Validator {

    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
       return UserEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEntity user=(UserEntity) target;
        if (!userService.existsByEmail(user.getEmail())){
            errors.rejectValue("email","","User with such email doesn't exists");
        }
    }
}
