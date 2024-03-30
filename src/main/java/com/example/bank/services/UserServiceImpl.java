package com.example.bank.services;

import com.example.bank.services.exception.DAOException;
import com.example.bank.services.exception.UsernameNotFoundException;
import com.example.bank.model.Role;
import com.example.bank.model.UserStatus;
import com.example.bank.model.UserEntity;
import com.example.bank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final PersonalDetailsService personalDetailsService;
    private final SessionService sessionService;
    private final String CONFIRM_CODE = UUID.randomUUID().toString();

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Cacheable(value = "UserServiceImpl::findById", key = "#id")
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DAOException(
                new UsernameNotFoundException(String.format("User with %s id does`t exist", id))));
    }

    @Override
    @Cacheable(value = "UserServiceImpl::findByEmail", key = "#email")
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new DAOException(
                new UsernameNotFoundException(String.format("User with %s email does`t exist", email))));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "UserServiceImpl::findById", key = "#result.id"),
            @CachePut(value = "UserServiceImpl::findByEmail", key = "#result.email")
    })
    public UserEntity updateUser(UserEntity updateUser) {
        return userRepository.save(updateUser);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "UserServiceImpl::findById", key = "#user.id"),
            @CacheEvict(value = "UserServiceImpl::findByEmail", key = "#user.email"),
    })
    public void delete(UserEntity user) {
        userRepository.delete(user);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "UserServiceImpl::findById", key = "#result.id"),
            @CachePut(value = "UserServiceImpl::findByEmail", key = "#result.email")
    })
    public UserEntity createNewUserAfterRegistration(UserEntity userEntity) {
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userEntity.setRole(Role.UNCONFIRMED_USER);
        userEntity.setConfirmationCode(CONFIRM_CODE);
        UserEntity savedUser = userRepository.save(userEntity);
        personalDetailsService.createPersonalDetails(savedUser);
        return savedUser;
    }

    @Override
    @Caching(put = {
            @CachePut(value = "UserServiceImpl::findById", key = "#result.id"),
            @CachePut(value = "UserServiceImpl::findByEmail", key = "#result.email")
    })
    public UserEntity alterRole(UserEntity user, Role role) {
        user.setRole(role);
        var updated = userRepository.save(user);
        sessionService.updateUserRole(user.getEmail(), role);
        return updated;
    }

}
