package com.example.bank.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(Set.of(Permission.VIEW_ONLY_HIS_PROFILE,Permission.ISSUE_CARD,Permission.MONEY_TRANSFER)),
    ADMIN(Set.of(Permission.ACCEPT_REQUEST,Permission.VIEW_ALL_PROFILES,Permission.ALTER_ROLE)),
    UNCONFIRMED_USER(Set.of(Permission.CONFIRM_EMAIL,Permission.VIEW_ONLY_HIS_PROFILE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream().map(permission ->
                new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }
}
