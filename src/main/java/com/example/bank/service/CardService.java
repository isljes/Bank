package com.example.bank.service;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.CardEntity;
import com.example.bank.model.CardStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface CardService {
    List<CardEntity> findAll();
    List<CardEntity> findAllByCardStatusOrderByExpirationAsc(CardStatus status);
    List<CardEntity> findAllByUserEntityEmail(String email);

    CardEntity findByCardNumber(String cardNumber);
    CardEntity findById(Long id);

    @Transactional
    CardEntity activateCard(Long id);

    @Transactional
    CardEntity deactivateCard(Long id);
    @Transactional
    CardEntity createNewCard(String email, IssueCardDTO issueCardDTO);
    @Transactional
    void delete(CardEntity card);
    boolean checkCorrectnessCardNumber(String cardNumber);


}
