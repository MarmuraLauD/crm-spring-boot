package com.gym.crmspringboot.service.impl;

import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import com.gym.crmspringboot.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for user with username: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            log.info("Password changed successfully for user with username: {}", username);
        } else {
            log.warn("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

    @Override
    @Transactional
    public void toggleActive(String username, Boolean status) {
        log.info("Toggling active status for user with username: {}", username);

        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        user.setActive(status);
        log.info("User with username: {} is now {}", username, user.isActive() ? "active" : "inactive");

    }
}