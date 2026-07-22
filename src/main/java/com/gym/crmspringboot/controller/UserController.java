package com.gym.crmspringboot.controller;

import com.gym.crmspringboot.controller.api.UserApi;
import com.gym.crmspringboot.facade.GymFacade;
import com.gym.crmspringboot.service.security.RequireAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final GymFacade gymFacade;

    @Override
    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @RequireAuth
    public void login(@RequestParam String username, @RequestParam String password) {
        // No further action is needed here; the aspect will handle authentication.
    }
    @Override
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        gymFacade.changeUserPassword(username, oldPassword, newPassword);
    }

    @Override
    @PatchMapping("/{username}/status")
    @ResponseStatus(HttpStatus.OK)
    public void toggleActive(
            @PathVariable String username,
            @RequestParam String password,
            @RequestParam Boolean status) {
        gymFacade.toggleUserActive(username, password, status);
    }

}