package com.example.bank.repository;

import com.example.bank.model.CardEntity;
import com.example.bank.model.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CardRepository extends JpaRepository<CardEntity,Long> {
    CardEntity findTopByOrderByIdDesc();
    Optional<CardEntity> findByCardNumber(String cardNumber);
    List<CardEntity> findAllByUserEntityEmail(String email);
    List<CardEntity> findAllByCardStatusOrderByExpirationAsc(CardStatus cardStatus);
}
