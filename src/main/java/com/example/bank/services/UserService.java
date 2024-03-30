package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface UserService {
    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);
    @Transactional
    UserEntity updateUser(UserEntity updateUser);
    @Transactional
    void delete(UserEntity user);
    @Transactional
    UserEntity createNewUserAfterRegistration(UserEntity userEntity);
    @Transactional
    UserEntity alterRole(UserEntity user,Role role);
}
