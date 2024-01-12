package com.example.bank.services;

import com.example.bank.model.Role;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonalDetailsService personalDetailsService;
    @InjectMocks
    private UserService userService;

    @Test
    public void findAllUsersTest() throws Exception{
        userService.findAll();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void findUserByIdTest_Positive() throws Exception{
        var id=1L;
        doReturn(Optional.of(new UserEntity()))
                        .when(userRepository)
                                .findById(id);
        UserEntity result=userService.findById(id);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void findUserByIdTestNegative_ThrowsUserNotFoundException() throws Exception{
        var id=1L;
        doReturn(Optional.empty())
                        .when(userRepository)
                                .findById(id);
        assertThrows(UsernameNotFoundException.class,()->userService.findById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void findUserByEmailTest_Positive() throws Exception{
        var email="some_email";
        doReturn(Optional.of(new UserEntity()))
                        .when(userRepository)
                                .findByEmail(email);
        UserEntity result=userService.findByEmail(email);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void findUserByEmailTestNegative_ThrowsUserNoFoundException() throws Exception{
        var email="some_email";
        doReturn(Optional.empty())
                        .when(userRepository)
                                .findByEmail(email);
        assertThrows(UsernameNotFoundException.class,()->userService.findByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void existsUserByEmailTest() throws Exception{
        assertFalse(userService.existsByEmail("some_email"));
        verify(userRepository,times(1)).existsByEmail("some_email");
    }

    @Test
    public void UpdateUserTest() throws Exception{
        userService.updateUser(new UserEntity());
        verify(userRepository,times(1)).save(new UserEntity());
    }

    @Test
    public void DeleteUserByIdTest() throws Exception{
        var id=1L;
        userService.deleteUserById(id);
        verify(userRepository,times(1)).deleteById(id);
    }


    @Test
    public void addUserTest() throws Exception{
        UserEntity user=new UserEntity();
        user.setEmail("some_email");
        user.setPassword("helll");

        doReturn("encode_password")
                        .when(passwordEncoder)
                                .encode(user.getPassword());

        userService.createNewUserAfterRegistration(user);

        assertEquals("some_email",user.getEmail());
        assertNotNull(user.getPassword());
        assertEquals(Status.ACTIVE,user.getStatus());
        assertEquals(Role.UNCONFIRMED_USER,user.getRole());
        assertNotNull(user.getConfirmationCode());

        verify(userRepository, times(1)).save(user);

        verify(personalDetailsService, times(1)).createPersonalDetails(user);
    }



}
