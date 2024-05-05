package com.example.bank.service;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface PersonalDetailsService {
    List<PersonalDetailsEntity> findAll();

    PersonalDetailsEntity findById(Long id);

    PersonalDetailsEntity findByUserEntityEmail(String email);
    @Transactional
    PersonalDetailsEntity createPersonalDetails(UserEntity user);
    @Transactional
    PersonalDetailsEntity update(PersonalDetailsEntity personalDetailsEntity);

}
