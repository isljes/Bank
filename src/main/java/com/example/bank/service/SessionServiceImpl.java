package com.example.bank.service;

import com.example.bank.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public void updateUserRole(String username, Role role) {
        if (sessionRepository instanceof FindByIndexNameSessionRepository) {
            Map<String, Session> map =
                    ((FindByIndexNameSessionRepository<Session>) sessionRepository).findByPrincipalName(username);
            for (Session session : map.values()) {
                if (!session.isExpired()) {
                    SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        Collection<GrantedAuthority> newPermissions = new HashSet<>(role.getAuthorities());
                        Object principalToUpdate = authentication.getPrincipal();
                        if (principalToUpdate instanceof User) {
                            securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principalToUpdate
                                    , authentication.getCredentials(), newPermissions));
                            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
                            sessionRepository.save(session);
                        }
                    }
                }
            }
        }
    }

    public void destroySessionByUsername(String username) {
        if (sessionRepository instanceof FindByIndexNameSessionRepository) {
            Map<String, Session> map =
                    ((FindByIndexNameSessionRepository<Session>) sessionRepository).findByPrincipalName(username);
            for (Session session : map.values()) {
                sessionRepository.deleteById(session.getId());
            }
        }
    }
}

