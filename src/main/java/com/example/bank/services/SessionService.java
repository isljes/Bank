package com.example.bank.services;

import com.example.bank.model.Role;
import org.springframework.transaction.annotation.Transactional;

public interface SessionService {
    @Transactional
    void updateUserRole(String username, Role role);
    @Transactional
    void destroySession(String username);
}
