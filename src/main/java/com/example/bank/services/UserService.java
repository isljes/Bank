package com.example.bank.services;

import com.example.bank.model.Role;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final PersonalDetailsService personalDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final String CONFIRM_CODE = UUID.randomUUID().toString();

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s id does`t exist", id)));
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s email does`t exist", email)));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateUser(UserEntity updateUser) {
        userRepository.save(updateUser);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void createNewUserAfterRegistration(UserEntity userEntity) {
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setRole(Role.UNCONFIRMED_USER);
        userEntity.setConfirmationCode(CONFIRM_CODE);
        UserEntity savedUser = userRepository.save(userEntity);
        personalDetailsService.createPersonalDetails(savedUser);
    }

    public void changePassword(String email, String password) {
        UserEntity user = findByEmail(email);
        user.setConfirmationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(password));
        updateUser(user);
    }

    public boolean confirmEmail(String email, String confirmationCode) {

        UserEntity user = findByEmail(email);
        String actualConfirmationCode = user.getConfirmationCode();

        boolean equalsConfirmationCode = Objects.equals(actualConfirmationCode, confirmationCode);

        if (equalsConfirmationCode) {
            user.setRole(Role.USER);
            user.setConfirmationCode(UUID.randomUUID().toString());
            updateUser(user);
            sessionService.updateUserRole(user.getEmail(), Role.USER);
            return true;
        }

        return false;
    }
}
