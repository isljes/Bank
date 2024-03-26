package com.example.bank.services;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;

import java.util.List;

public interface PersonalDetailsService {
    List<PersonalDetailsEntity> findAll();

    PersonalDetailsEntity findById(Long id);

    PersonalDetailsEntity createPersonalDetails(UserEntity user);

    PersonalDetailsEntity update(PersonalDetailsEntity personalDetailsEntity);
}
