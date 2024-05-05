package com.example.bank.controller;

import com.example.bank.model.PersonalDetailsEntity;
import com.example.bank.model.Role;
import com.example.bank.model.UserEntity;
import com.example.bank.service.CardService;
import com.example.bank.service.SessionService;
import com.example.bank.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdminController adminController;
    @MockBean
    private UserService userService;
    @MockBean
    private CardService cardService;
    @MockBean
    private SessionService sessionService;

    @Test
    @WithMockUser(authorities = "CARD_MANAGEMENT")
    public void getCardManagementPage_shouldReturnCardManagementPage() throws Exception {
        when(cardService.findAllByCardStatusOrderByExpirationAsc(any())).thenReturn(anyList());
        this.mockMvc.perform(get("/admin/card-management"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("allUnderConsiderationCards"),
                        view().name("issuance-requests")
                );
        verify(cardService, atLeastOnce()).findAllByCardStatusOrderByExpirationAsc(any());
    }

    @Test
    @WithMockUser(authorities = "CARD_MANAGEMENT")
    public void activateCard_shouldActivateCard() throws Exception {
        this.mockMvc.perform(post("/admin/card-management/activate-card/1")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/card-management")
                );
        verify(cardService, atLeastOnce()).activateCard(1L);
    }

    @Test
    @WithMockUser(authorities = "CARD_MANAGEMENT")
    public void blockCard_shouldDeactivateCard() throws Exception {
        this.mockMvc.perform(post("/admin/card-management/block-card/1")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/card-management")
                );
        verify(cardService, atLeastOnce()).deactivateCard(1L);
    }

    @Test
    @WithMockUser(authorities = "VIEW_ALL_PROFILES")
    public void getUserProfilePage_shouldReturnUserProfilePage() throws Exception {
        final var personalDetails=new PersonalDetailsEntity();
        final var user =new UserEntity();
        user.setEmail("some@email.com");
        personalDetails.setUserEntity(user);
        user.setPersonalDetailsEntity(personalDetails);

        when(userService.findById(anyLong())).thenReturn(user);
        this.mockMvc.perform(get("/admin/profile/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("profile"),
                        view().name("profile")
                );
        verify(userService, atLeastOnce()).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "USER_MANAGEMENT")
    public void alterUserRole_shouldRedirect() throws Exception {
        final var user =new UserEntity();

        when(userService.findById(anyLong())).thenReturn(user);
        this.mockMvc.perform(post("/admin/profile/1/alter-role")
                        .flashAttr("userEntity.role", Role.ADMIN)
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/card-management")
                );
        verify(userService, atLeastOnce()).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "USER_MANAGEMENT")
    public void blockUser_shouldRedirect() throws Exception {
        final var user =new UserEntity();
        user.setEmail("some@email.com");

        when(userService.findById(anyLong())).thenReturn(user);
        this.mockMvc.perform(post("/admin/profile/1/block-user")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/card-management")
                );
        verify(userService, atLeastOnce()).findById(1L);
        verify(userService,atLeastOnce()).update(any());
        verify(sessionService,atLeastOnce()).destroySessionByUsername(user.getEmail());
    }

    @Test
    @WithMockUser(authorities = "USER_MANAGEMENT")
    public void activateUser_shouldRedirect() throws Exception {
        final var user =new UserEntity();

        when(userService.findById(anyLong())).thenReturn(user);
        this.mockMvc.perform(post("/admin/profile/1/activate-user")
                        .with(csrf()))
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/admin/card-management")
                );
        verify(userService, atLeastOnce()).findById(1L);
        verify(userService,atLeastOnce()).update(any());
    }
}