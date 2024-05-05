package com.example.bank.controller;

import com.example.bank.dto.IssueCardDTO;
import com.example.bank.model.*;
import com.example.bank.service.CardService;
import com.example.bank.service.SessionService;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CardService cardService;
    private final SessionService sessionService;


    @GetMapping("/card-management")
    @PreAuthorize("hasAuthority('CARD_MANAGEMENT')")
    public String getCardManagementPage(Model model){
        List<CardEntity> allUnderConsiderationCardsAsc =
        cardService.findAllByCardStatusOrderByExpirationAsc(CardStatus.UNDER_CONSIDERATION);
        model.addAttribute("allUnderConsiderationCards",allUnderConsiderationCardsAsc);
        return "issuance-requests";
    }

    @PostMapping("/card-management/activate-card/{id}")
    @PreAuthorize("hasAuthority('CARD_MANAGEMENT')")
    public String activateCard(@PathVariable Long id) {
        cardService.activateCard(id);
        return "redirect:/admin/card-management";
    }

    @PostMapping("/card-management/block-card/{id}")
    @PreAuthorize("hasAuthority('CARD_MANAGEMENT')")
    public String blockCard(@PathVariable Long id) {
        cardService.deactivateCard(id);
        return "redirect:/admin/card-management";
    };


    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAuthority('VIEW_ALL_PROFILES')")
    public String getUserProfilePage(@PathVariable Long id,
                               Model model) {
         var userEntity = userService.findById(id);
        model.addAttribute("profile",userEntity.getPersonalDetailsEntity());
        return "profile";
    }

    @PostMapping("/profile/{id}/alter-role")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    public String alterUserRole(@PathVariable Long id,
                                @ModelAttribute("userEntity.role") Role role){
        var userEntity = userService.findById(id);
        userService.alterRole(userEntity,role);
        return "redirect:/admin/card-management";
    }
    @PostMapping("/profile/{id}/block-user")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    public String blockUser(@PathVariable Long id){
        var userEntity=userService.findById(id);
        userEntity.setUserStatus(UserStatus.NOT_ACTIVE);
        userService.update(userEntity);
        sessionService.destroySessionByUsername(userEntity.getEmail());
        return "redirect:/admin/card-management";
    }

    @PostMapping("/profile/{id}/activate-user")
    @PreAuthorize("hasAuthority('USER_MANAGEMENT')")
    public String activateUser(@PathVariable Long id){
        var userEntity=userService.findById(id);
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userService.update(userEntity);
        return "redirect:/admin/card-management";
    }
}
