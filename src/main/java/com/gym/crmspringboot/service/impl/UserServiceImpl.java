package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import com.gym.crmspringboot.service.security.RequireAuth;
import com.gym.crmspringboot.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @RequireAuth
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for user with username: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            log.info("Password changed successfully for user with username: {}", username);
        } else {
            log.warn("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

    @Override
    @RequireAuth
    @Transactional
    public void toggleActive(String username, String password) {
        log.info("Toggling active status for user with username: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(!user.isActive());
            log.info("User with username: {} is now {}", username, user.isActive() ? "active" : "inactive");
        } else {
            log.warn("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }
}