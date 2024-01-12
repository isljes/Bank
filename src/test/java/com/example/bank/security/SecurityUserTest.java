package com.example.bank.security;

import com.example.bank.model.Role;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityUserTest {

    @Test
    public void fromUserTest_returnedUserFromSpringSecurity(){
        UserEntity userEntity=new UserEntity();
        userEntity.setEmail("some_email");
        userEntity.setPassword("some_password");
        userEntity.setRole(Role.USER);
        userEntity.setStatus(Status.ACTIVE);

        UserDetails userDetails=SecurityUser.fromUser(userEntity);

        assertEquals(userEntity.getEmail(),userDetails.getUsername(),"Emails don't match");
        assertEquals(userEntity.getPassword(),userDetails.getPassword(),"Passwords don't match");
        assertEquals(userEntity.getRole().getAuthorities(),userDetails.getAuthorities(),"Authorities don't match");

        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());

    }
}
