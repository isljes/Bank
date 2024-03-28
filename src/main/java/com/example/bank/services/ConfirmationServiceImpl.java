package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationServiceImpl implements ConfirmationService {

    private final UserService userService;
    private final SessionService sessionService;

    @Override
    @ManualLogging
    public boolean confirmEmail(String email, String confirmationCode) {
        log.trace("Execute public void com.example.bank.services.UserService.confirmEmail(String,String)");
        UserEntity user = userService.findByEmail(email);
        String actualConfirmationCode = user.getConfirmationCode();

        boolean equalsConfirmationCode = Objects.equals(actualConfirmationCode, confirmationCode);

        if (equalsConfirmationCode) {
            user.setRole(Role.USER);
            user.setConfirmationCode(UUID.randomUUID().toString());
            userService.updateUser(user);
            sessionService.updateUserRole(user.getEmail(), Role.USER);
            log.trace("Completed public void com.example.bank.services.UserService.confirmEmail(String,String)");
            return true;
        }
        log.warn("public void com.example.bank.services.UserService.confirmEmail(String,String) -> Wrong confirmation code");
        return false;
    }
}
