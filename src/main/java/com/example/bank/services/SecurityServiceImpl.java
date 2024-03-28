package com.example.bank.services;

import com.example.bank.logging.ManualLogging;
import com.example.bank.model.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @ManualLogging
    public void autoLogin(String username, String password, Set<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, authorities);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        authenticationToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }

    @Override
    @ManualLogging
    public void changePassword(String email, String password) {
        log.trace("Execute public void com.example.bank.services.UserService.changePassword(String,String)");
        UserEntity user = userService.findByEmail(email);
        user.setConfirmationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(password));
        userService.updateUser(user);
        log.trace("Completed public void com.example.bank.services.UserService.changePassword(String,String)");
    }

}
