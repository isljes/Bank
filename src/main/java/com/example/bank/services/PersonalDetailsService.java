package com.example.bank.services;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface PersonalDetailsService {
    List<PersonalDetailsEntity> findAll();

    PersonalDetailsEntity findById(Long id);

    @Transactional
    PersonalDetailsEntity createPersonalDetails(UserEntity user);
    @Transactional
    PersonalDetailsEntity update(PersonalDetailsEntity personalDetailsEntity);
    @Transactional
    void delete(PersonalDetailsEntity personalDetailsEntity);
}
