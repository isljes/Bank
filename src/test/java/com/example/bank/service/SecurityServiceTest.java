package com.example.bank.service;

import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    @Mock
    private  AuthenticationManager authenticationManager;
    @Mock
    private  UserService userService;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private SessionService sessionService;
    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    public void changePassword_shouldChangePasswordAndUpdateUser() throws Exception {
        final var email="some@email.com";
        final var password="some_password";
        final var encode_password="e-some_password";
        final var oldConfirmationCode= UUID.randomUUID().toString();
        final var user=new UserEntity();
        user.setEmail(email);
        user.setConfirmationCode(oldConfirmationCode);
        when(userService.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encode_password);

        securityService.changePassword(email,password);

        assertEquals(email,user.getEmail());
        assertEquals(encode_password,user.getPassword());
        assertNotEquals(oldConfirmationCode,user.getConfirmationCode());
        verify(userService,times(1)).update(user);
        verify(userService,times(1)).findByEmail(email);

    }

    @Test
    public void confirmEmail_shouldReturnTrue_whenCodesEquals() throws Exception {
        final var email="some@email.com";
        final var oldConfirmationCode= UUID.randomUUID().toString();
        final var user=new UserEntity();
        user.setEmail(email);
        user.setConfirmationCode(oldConfirmationCode);
        when(userService.findByEmail(email)).thenReturn(user);

        final boolean result = securityService.confirmEmail(email, oldConfirmationCode);

        assertTrue(result);
        assertEquals(Role.USER,user.getRole());
        assertNotNull(user.getConfirmationCode());
        assertNotEquals(oldConfirmationCode,user.getConfirmationCode());

        verify(userService,times(1)).update(user);
        verify(sessionService,times(1)).updateUserRole(email,Role.USER);
    }

    @Test
    public void confirmEmail_shouldReturnFalse_whenCodesNotEquals() throws Exception {
        final var email="some@email.com";
        final var actualConfirmationCode= UUID.randomUUID().toString();
        final var anotherConfirmationCode= UUID.randomUUID().toString();
        final var user=new UserEntity();
        user.setEmail(email);
        user.setConfirmationCode(actualConfirmationCode);
        when(userService.findByEmail(email)).thenReturn(user);

        final boolean result = securityService.confirmEmail(email, anotherConfirmationCode);

        assertFalse(result);
        assertNotEquals(Role.USER,user.getRole());
        assertNotNull(user.getConfirmationCode());
        assertEquals(actualConfirmationCode,user.getConfirmationCode());

        verify(userService,times(0)).update(user);
        verify(sessionService,times(0)).updateUserRole(email,Role.USER);
    }

}
