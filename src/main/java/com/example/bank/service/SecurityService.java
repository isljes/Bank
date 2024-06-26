package com.example.bank.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface SecurityService {
    void autoLogin(String username, String password, Set<SimpleGrantedAuthority> authorities, HttpServletRequest request);
    @Transactional
    void changePassword(String email, String password);
    boolean confirmEmail(String email, String confirmationCode);
}
