package com.example.bank.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public interface SecurityService {
    void autoLogin(String username, String password, Set<SimpleGrantedAuthority> authorities, HttpServletRequest request);
}
