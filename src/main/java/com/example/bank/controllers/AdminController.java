package com.example.bank.controllers;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.Status;
import com.example.bank.model.UserEntity;
import com.example.bank.services.CardService;
import com.example.bank.services.PersonalDetailsService;
import com.example.bank.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CardService cardService;
    private final PersonalDetailsService personalDetailsService;


    @GetMapping("/findPerson")
    public String find(@RequestParam(value = "name",required = false) String name
            , @RequestParam(value = "surname",required = false) String surname,
                       Model model) {
        List<PersonalDetailsEntity> searchResult = personalDetailsService.findAll().stream()
                .filter(personalDetailsEntity -> {
                    if(Objects.equals(name, "")) return true;
                    return personalDetailsEntity.getName().equals(name);
                })
                .filter(personalDetailsEntity -> {
                    if(Objects.equals(surname, "")) return true;
                    return personalDetailsEntity.getSurname().equals(surname);
                }).collect(Collectors.toList());
                model.addAttribute("searchResult",searchResult);
        return "find-person";
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAuthority('VIEW_ALL_PROFILES')")
    public String getProfilePage(@PathVariable Long id, Model model) {
        PersonalDetailsEntity personalDetailsEntity =
                userService.findById(id).getPersonalDetailsEntity();
        model.addAttribute("profile", personalDetailsEntity);
        return "profile";
    }

    @PostMapping("/profile/{id}")
    @PreAuthorize("hasAuthority('VIEW_ALL_PROFILES')")
    public String alterProfile(@PathVariable Long id, @ModelAttribute PersonalDetailsEntity personalDetailsEntity) {
        personalDetailsEntity.setUserEntity(userService.findById(id));
        personalDetailsService.update(personalDetailsEntity);
        return "redirect:/welcome";
    }

    @PostMapping("/blockUser/{id}")
    @PreAuthorize("hasAuthority('ALTER_ROLE')")
    public String blockUser(@PathVariable Long id){
        UserEntity userEntity=userService.findById(id);
        userEntity.setStatus(Status.NOT_ACTIVE);
        userService.updateUser(userEntity);
        return "redirect:/findPerson";
    }

    @PostMapping("/activateCard/{id}")
    @PreAuthorize("hasAuthority('ACCEPT_REQUEST')")
    public String activateCard(@PathVariable Long id) {
        cardService.activateCard(id);
        return "redirect:/welcome";
    }
}
