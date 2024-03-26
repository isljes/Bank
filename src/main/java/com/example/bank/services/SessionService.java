package com.example.bank.services;

import com.example.bank.model.Role;

public interface SessionService {
    void updateUserRole(String username, Role role);
}
