package com.example.bank.services;

import com.example.bank.services.exception.DAOException;
import com.example.bank.services.exception.PersonalDetailsNotFoundException;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.PersonalDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "PersonalDetailsServiceImpl::findById",key = "id")
    public PersonalDetailsEntity findById(Long id){
        return personalDetailsRepository.findById(id).
                orElseThrow(()-> new DAOException(
                        new PersonalDetailsNotFoundException(String.format("PersonalDetails with %s id does`t exist",id))));

    }

    @Override
    @CachePut(value = "PersonalDetailsServiceImpl::findById",key = "#result.id",unless = "#result.id==null")
    public PersonalDetailsEntity createPersonalDetails(UserEntity user){
        PersonalDetailsEntity personalDetailsEntity=new PersonalDetailsEntity();
        personalDetailsEntity.setUserEntity(user);
        return personalDetailsRepository.save(personalDetailsEntity);
    }

    @Override
    @CachePut(value = "PersonalDetailsServiceImpl::findById",key = "#result.id",unless = "#result.id==null")
    public PersonalDetailsEntity update(PersonalDetailsEntity personalDetailsEntity) {
        return personalDetailsRepository.save(personalDetailsEntity);
    }

    @Override
    @CacheEvict(value = "PersonalDetailsServiceImpl::findById",key = "personalDetailsEntity.id")
    public void delete(PersonalDetailsEntity personalDetailsEntity) {
        personalDetailsRepository.delete(personalDetailsEntity);
    }
}
