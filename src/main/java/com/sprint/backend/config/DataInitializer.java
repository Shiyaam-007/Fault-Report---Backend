package com.sprint.backend.config;

import com.sprint.backend.entity.User;
import com.sprint.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("No users found in database. Seeding default student and admin users...");
            userRepository.save(User.builder()
                    .name("Alice Student")
                    .role(User.Role.STUDENT)
                    .build());
            userRepository.save(User.builder()
                    .name("Bob Admin")
                    .role(User.Role.ADMIN)
                    .build());
            log.info("Default users seeded successfully.");
        }
    }
}
