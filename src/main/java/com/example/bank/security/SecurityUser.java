package com.example.bank.security;

import com.example.bank.model.UserStatus;
import com.example.bank.model.UserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {
    private String email;
    private String password;
    private List<SimpleGrantedAuthority> authorities;
    private Boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(UserEntity userEntity){
       return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRole().getAuthorities())
                .disabled(userEntity.getUserStatus().equals(UserStatus.NOT_ACTIVE))
                .accountExpired(userEntity.getUserStatus().equals(UserStatus.NOT_ACTIVE))
                .credentialsExpired(userEntity.getUserStatus().equals(UserStatus.NOT_ACTIVE))
                .accountLocked(userEntity.getUserStatus().equals(UserStatus.NOT_ACTIVE))
                .build();
    }
}
