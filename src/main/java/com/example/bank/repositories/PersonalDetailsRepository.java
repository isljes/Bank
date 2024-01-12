package com.example.bank.repositories;

import com.example.bank.model.PersonalDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDetailsRepository extends JpaRepository<PersonalDetailsEntity,Long> {

}
