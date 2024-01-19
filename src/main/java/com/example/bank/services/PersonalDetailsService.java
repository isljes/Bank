package com.example.bank.services;

import com.example.bank.custom_exception.PersonalDetailsNotFoundException;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.PersonalDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalDetailsService {

    private final PersonalDetailsRepository personalDetailsRepository;

    public List<PersonalDetailsEntity> findAll(){
        return personalDetailsRepository.findAll();
    }

    public PersonalDetailsEntity findById(Long id){
        return personalDetailsRepository.findById(id).
                orElseThrow(()->
                        new PersonalDetailsNotFoundException(String.format("PersonalDetails with %s id does`t exist",id)));

    }

    public void createPersonalDetails(UserEntity user){
        PersonalDetailsEntity personalDetailsEntity=new PersonalDetailsEntity();
        personalDetailsEntity.setUserEntity(user);
        personalDetailsRepository.save(personalDetailsEntity);
    }

    public void update(PersonalDetailsEntity personalDetailsEntity) {
        personalDetailsRepository.save(personalDetailsEntity);
    }
}
