package com.example.bank.service;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.Role;
import com.example.bank.model.UserStatus;
import com.example.bank.model.UserEntity;
import com.example.bank.repository.UserRepository;
import com.example.bank.service.exception.DAOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonalDetailsService personalDetailsService;
    @Mock
    private SessionService sessionService;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void findAll_shouldCallRepository() throws Exception {
        userService.findAll();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnUser_whenExists() throws Exception {
        final var id=1L;
        final var user=mock(UserEntity.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        final var actual = userService.findById(id);

        assertNotNull(actual);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void findById_shouldThrowsDAOException_whenNotExists() throws Exception {
        final var id=1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->userService.findById(id));

        verify(userRepository, times(1)).findById(id);
    }
    @Test
    public void findByEmail_shouldReturnUser_whenExists() throws Exception {
        final var email="some@email.com";
        final var user=mock(UserEntity.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        final var actual = userService.findByEmail(email);

        assertNotNull(actual);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void findByEmail_shouldThrowsDAOException_whenNotExists() throws Exception {
        final var email="some@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(DAOException.class,()->userService.findByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
    }



    @Test
    public void existsByEmail_shouldReturnTrue_whenExists() throws Exception {
        final var email="some@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(userService.existsByEmail(email));

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void existsByEmail_shouldReturnFalse_whenNotExists() throws Exception {
        final var email="some@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(userService.existsByEmail(email));

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void update_shouldCallSaveAndReturnSavedUser() throws Exception {
        final var user = mock(UserEntity.class);
        when(userRepository.save(user)).thenReturn(user);

        final var actual = userService.update(user);

        assertNotNull(actual);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void delete_shouldCallSessionServiceAndUserRepo() throws Exception {
        final var user = mock(UserEntity.class);

        userService.delete(user);

        verify(sessionService,times(1)).destroySessionByUsername(user.getEmail());
        verify(userRepository, times(1)).delete(user);
    }


    @Test
    public void createNewUserAfterRegistration_shouldCreateNewUserAndSave() throws Exception {
        final var user =new UserEntity();
        final var profile = new PersonalDetailsEntity();
        final var password="some_password";
        user.setEmail("some@email.com");
        user.setPassword(password);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encode");
        when(userRepository.save(user)).thenReturn(user);
        when(personalDetailsService.createPersonalDetails(user)).thenReturn(profile);

        userService.createNewUserAfterRegistration(user);

        assertEquals("some@email.com", user.getEmail());
        assertEquals("encode",user.getPassword());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
        assertEquals(Role.UNCONFIRMED_USER, user.getRole());
        assertNotNull(user.getConfirmationCode());
        assertNotNull(user.getPersonalDetailsEntity());
        verify(passwordEncoder,times(1)).encode(password);
        verify(userRepository,times(1)).save(user);
        verify(personalDetailsService,times(1)).createPersonalDetails(user);

    }

    @Test
    public void alterRole_shouldAlterRoleAndCallSessionService() throws Exception {
        final var user=new UserEntity();

        userService.alterRole(user,Role.ADMIN);

        assertEquals(Role.ADMIN,user.getRole());
        verify(sessionService,times(1)).updateUserRole(user.getEmail(),Role.ADMIN);
    }


}
