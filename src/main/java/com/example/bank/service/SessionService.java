package com.example.bank.service;

import com.example.bank.model.Role;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public interface SessionService {

    void updateUserRole(String username, Role role);

    void destroySessionByUsername(String username);
}
