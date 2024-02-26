package com.example.bank.controllers;

import com.example.bank.customexception.CardNotFoundException;
import com.example.bank.customexception.DAOException;
import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.services.CardService;
import com.example.bank.services.PersonalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final PersonalDetailsService personalDetailsService;
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<PersonalDetailsEntity> getProfileById(@RequestParam(value = "id") long id){
        cardService.findById(id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
