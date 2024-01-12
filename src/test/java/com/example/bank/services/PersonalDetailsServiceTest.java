package com.example.bank.services;

import com.example.bank.custom_exception.CardNotFoundException;
import com.example.bank.custom_exception.PersonalDetailsNotFoundException;
import com.example.bank.model.CardEntity;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.PersonalDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PersonalDetailsServiceTest {
    @Mock
    private PersonalDetailsRepository personalDetailsRepository;
    @InjectMocks
    private PersonalDetailsService personalDetailsService;

    @Test
    public void findAllPersonalDetailsTest() throws Exception{
        List<PersonalDetailsEntity> personalDetailsEntityList
                =List.of(new PersonalDetailsEntity(),new PersonalDetailsEntity());

        doReturn(personalDetailsEntityList)
                .when(personalDetailsRepository).findAll();

        List<PersonalDetailsEntity> resultList=personalDetailsService.findAll();

        assertNotNull(resultList,"findAll() from personalDetailsService must be return List<PersonalDetailsEntity>");
        assertEquals(2,resultList.size());
        verify(personalDetailsRepository, times(1)).findAll();
    }

    @Test
    public void findPersonalDetailsByIdTest_ReturnsPersonalDetails() throws Exception{
        var id=1L;
        doReturn(Optional.of(new PersonalDetailsEntity()))
                .when(personalDetailsRepository).findById(id);
        PersonalDetailsEntity result = personalDetailsService.findById(id);
        assertNotNull(result,"findById() from personalDetailsService must be return PersonalDetailsEntity");
        verify(personalDetailsRepository,times(1)).findById(id);
    }

    @Test
    public void findCardByCardNumberTest_ThrowsCardNotFoundException() throws Exception{
        var id=1L;
        doReturn(Optional.empty())
                .when(personalDetailsRepository).findById(id);
        assertThrows(PersonalDetailsNotFoundException.class,()->
                personalDetailsService.findById(id),"findById() from PersonalDetailsService must be throws PersonalDetailsNotFoundException");
        verify(personalDetailsRepository,times(1)).findById(id);
    }

    @Test
    public void createPersonDetailsTest() throws Exception{
        personalDetailsService.createPersonalDetails(new UserEntity());
        verify(personalDetailsRepository,times(1)).save(new PersonalDetailsEntity());
    }
    @Test
    public void updatePersonDetailsTest() throws Exception{
        var personalDetailsEntity=new PersonalDetailsEntity();
        personalDetailsService.update(personalDetailsEntity);
        verify(personalDetailsRepository,times(1)).save(personalDetailsEntity);
    }

}
