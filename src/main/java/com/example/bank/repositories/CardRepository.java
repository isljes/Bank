package com.example.bank.repositories;

import com.example.bank.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity,Long> {
    CardEntity findTopByOrderByIdDesc();
    Optional<CardEntity> findByCardNumber(String cardNumber);
}
