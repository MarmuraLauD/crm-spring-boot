package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.controller.api.UserApi;
import com.gym.crmspringboot.facade.GymFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final GymFacade gymFacade;

    @Override
    @PutMapping("/password")
    public void changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        gymFacade.changeUserPassword(username, oldPassword, newPassword);
    }

    @Override
    @PatchMapping("/{username}/status")
    @PreAuthorize("hasRole('TRAINER')")
    public void toggleActive(
            @PathVariable String username,
            @RequestParam Boolean status) {
        gymFacade.toggleUserActive(username, status);
    }

}