package com.example.bank.services;

import com.example.bank.services.exception.DAOException;
import com.example.bank.services.exception.UsernameNotFoundException;
import com.example.bank.logging.ManualLogging;
import com.example.bank.model.Role;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final PersonalDetailsService personalDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final String CONFIRM_CODE = UUID.randomUUID().toString();

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DAOException(
                new UsernameNotFoundException(String.format("User with %s id does`t exist", id))));
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->new DAOException(
                new UsernameNotFoundException(String.format("User with %s email does`t exist", email))));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity updateUser(UserEntity updateUser) {
        return userRepository.save(updateUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void createNewUserAfterRegistration(UserEntity userEntity) {
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setRole(Role.UNCONFIRMED_USER);
        userEntity.setConfirmationCode(CONFIRM_CODE);
        UserEntity savedUser = userRepository.save(userEntity);
        personalDetailsService.createPersonalDetails(savedUser);
    }

    @Override
    @ManualLogging
    public void changePassword(String email, String password) {
        log.trace("Execute public void com.example.bank.services.UserService.changePassword(String,String)");
        UserEntity user = findByEmail(email);
        user.setConfirmationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(password));
        updateUser(user);
        log.trace("Completed public void com.example.bank.services.UserService.changePassword(String,String)");
    }

    @Override
    @ManualLogging
    public boolean confirmEmail(String email, String confirmationCode) {
        log.trace("Execute public void com.example.bank.services.UserService.confirmEmail(String,String)");
        UserEntity user = findByEmail(email);
        String actualConfirmationCode = user.getConfirmationCode();

        boolean equalsConfirmationCode = Objects.equals(actualConfirmationCode, confirmationCode);

        if (equalsConfirmationCode) {
            user.setRole(Role.USER);
            user.setConfirmationCode(UUID.randomUUID().toString());
            updateUser(user);
            sessionService.updateUserRole(user.getEmail(), Role.USER);
            log.trace("Completed public void com.example.bank.services.UserService.confirmEmail(String,String)");
            return true;
        }
        log.warn("public void com.example.bank.services.UserService.confirmEmail(String,String) -> Wrong confirmation code");
        return false;
    }
}
