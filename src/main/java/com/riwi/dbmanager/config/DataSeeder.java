package com.riwi.dbmanager.config;

import com.riwi.dbmanager.model.User;
import com.riwi.dbmanager.model.enums.Role;
import com.riwi.dbmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Admin",
                    "User",
                    "admin@talentboard.com",
                    "Admin123*",
                    Role.ADMIN
            );

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Recruiter",
                    "User",
                    "recruiter@talentboard.com",
                    "Recruiter123*",
                    Role.RECRUITER
            );

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "Candidate",
                    "User",
                    "candidate@talentboard.com",
                    "Candidate123*",
                    Role.CANDIDATE
            );
        };
    }

    private void createUserIfNotExists(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String lastName,
            String email,
            String password,
            Role role
    ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = User.builder()
                .name(name)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(user);
    }
}
