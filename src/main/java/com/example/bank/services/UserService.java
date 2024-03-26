package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import com.example.bank.model.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);

    UserEntity updateUser(UserEntity updateUser);

    void deleteUserById(Long id);

    void createNewUserAfterRegistration(UserEntity userEntity);

    @ManualLogging
    void changePassword(String email, String password);

    @ManualLogging
    boolean confirmEmail(String email, String confirmationCode);
}
