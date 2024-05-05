package com.example.bank.service;

import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import org.reactivestreams.Publisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface UserService {
    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);
    @Transactional
    UserEntity update(UserEntity updateUser);
    @Transactional
    void delete(UserEntity user);
    @Transactional
    UserEntity createNewUserAfterRegistration(UserEntity userEntity);
    @Transactional
    UserEntity alterRole(UserEntity user,Role role);
}
