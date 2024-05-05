package com.example.bank.repository;

import com.example.bank.model.PersonalDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetailsEntity,Long> {
    Optional<PersonalDetailsEntity> findByUserEntityEmail(String email);
}
