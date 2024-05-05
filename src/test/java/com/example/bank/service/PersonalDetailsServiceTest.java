package com.example.bank.service;

import com.example.bank.service.exception.DAOException;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.UserEntity;
import com.example.bank.repository.PersonalDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PersonalDetailsServiceTest {
    @Mock
    private PersonalDetailsRepository personalDetailsRepository;
    @InjectMocks
    private PersonalDetailsServiceImpl personalDetailsService;

    @Test
    public void findAll_shouldCallRepository() throws Exception{
        personalDetailsService.findAll();
        verify(personalDetailsRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnUser_whenExists() throws Exception {
        final var id=1L;
        final var personalDetails=mock(PersonalDetailsEntity.class);
        when(personalDetailsRepository.findById(id)).thenReturn(Optional.of(personalDetails));

        final var actual = personalDetailsService.findById(id);

        assertNotNull(actual);
        verify(personalDetailsRepository, times(1)).findById(id);
    }

    @Test
    public void findById_shouldThrowsDAOException_whenNotExists() throws Exception {
        final var id=1L;
        when(personalDetailsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->personalDetailsService.findById(id));

        verify(personalDetailsRepository, times(1)).findById(id);
    }
    @Test
    public void findByUserEntityEmail_shouldReturnUser_whenExists() throws Exception {
        final var email="some@email.com";
        final var personalDetails=mock(PersonalDetailsEntity.class);
        when(personalDetailsRepository.findByUserEntityEmail(email)).thenReturn(Optional.of(personalDetails));

        final var actual = personalDetailsService.findByUserEntityEmail(email);

        assertNotNull(actual);
        verify(personalDetailsRepository, times(1)).findByUserEntityEmail(email);
    }

    @Test
    public void findByUserEntityEmail_shouldThrowsDAOException_whenNotExists() throws Exception {
        final var email="some@email.com";
        when(personalDetailsRepository.findByUserEntityEmail(email)).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->personalDetailsService.findByUserEntityEmail(email));

        verify(personalDetailsRepository, times(1)).findByUserEntityEmail(email);
    }



    @Test
    public void create_shouldCreateAndSavePDE() throws Exception {
        final var user=mock(UserEntity.class);

        personalDetailsService.createPersonalDetails(user);

        final ArgumentCaptor<PersonalDetailsEntity> captor =
                ArgumentCaptor.forClass(PersonalDetailsEntity.class);
        verify(personalDetailsRepository,times(1)).save(captor.capture());
    }
    @Test
    public void update_shouldCallRepo() throws Exception {
        final var personalDetails=mock(PersonalDetailsEntity.class);

        personalDetailsService.update(personalDetails);

        verify(personalDetailsRepository,times(1)).save(personalDetails);
    }



}
