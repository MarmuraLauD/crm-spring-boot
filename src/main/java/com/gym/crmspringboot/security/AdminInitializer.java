package com.gym.crmspringboot.security;

import com.gym.crmspringboot.model.Role;
import com.gym.crmspringboot.model.User;
import com.gym.crmspringboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);

            log.info("Super admin created successfully with username: {}", adminUsername);
        } else {
            log.info("Super admin already exists in the database. Skipping creation.");
        }
    }

}
