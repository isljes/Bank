package com.example.bank.service;

import com.example.bank.service.exception.DAOException;
import com.example.bank.service.exception.PersonalDetailsNotFoundException;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.repository.PersonalDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalDetailsServiceImpl implements PersonalDetailsService {

    private final PersonalDetailsRepository personalDetailsRepository;

    @Override
    public List<PersonalDetailsEntity> findAll(){
        return personalDetailsRepository.findAll();
    }

    @Override
    @Cacheable(value = "PersonalDetailsServiceImpl::findById",key = "#id")
    public PersonalDetailsEntity findById(Long id){
        return personalDetailsRepository.findById(id).
                orElseThrow(()-> new DAOException(
                        new PersonalDetailsNotFoundException(String.format("PersonalDetails with %s id does`t exist",id))));

    }

    @Override
    @Cacheable(value = "PersonalDetailsServiceImpl::findByUserEntityEmail",key = "#email")
    public PersonalDetailsEntity findByUserEntityEmail(String email) {
        return personalDetailsRepository.findByUserEntityEmail(email).orElseThrow(()->new DAOException(
                new PersonalDetailsNotFoundException(String.format("PersonalDetails with %s email does`t exist",email))
        ));
    }

    @Override
    @CachePut(value = "PersonalDetailsServiceImpl::findById",key = "#result.id")
    public PersonalDetailsEntity createPersonalDetails(UserEntity user){
        PersonalDetailsEntity personalDetailsEntity=new PersonalDetailsEntity();
        personalDetailsEntity.setUserEntity(user);
        return  personalDetailsRepository.save(personalDetailsEntity);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "PersonalDetailsServiceImpl::findById",key = "#result.id"),
            @CachePut(value = "PersonalDetailsServiceImpl::findByUserEntityEmail",key = "#personalDetailsEntity.userEntity.email")})
    public PersonalDetailsEntity update(PersonalDetailsEntity personalDetailsEntity) {
        return personalDetailsRepository.save(personalDetailsEntity);
    }

}
