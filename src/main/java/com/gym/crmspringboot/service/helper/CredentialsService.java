package com.gym.crmspringboot.service.helper;

import com.gym.crmspringboot.repository.UserRepository;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CredentialsService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();
    private final UserRepository userRepository;

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String finalUsername = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = baseUsername + suffix;
            suffix++;
        }

        return finalUsername;
    }

    public boolean authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(rawPassword))
                .orElse(false);
    }

}
