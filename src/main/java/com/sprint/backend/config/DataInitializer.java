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
        log.info("Checking users and credentials in database...");

        // Ensure default student user exists with credentials
        userRepository.findByUsernameIgnoreCase("student").ifPresentOrElse(
                student -> {
                    if (student.getPassword() == null || student.getPassword().isBlank()) {
                        student.setPassword("student123");
                        userRepository.save(student);
                        log.info("Updated password for student user.");
                    }
                },
                () -> {
                    // Check if an existing student user has no username
                    userRepository.findAll().stream()
                            .filter(u -> u.getRole() == User.Role.STUDENT && (u.getUsername() == null || u.getUsername().isBlank()))
                            .findFirst()
                            .ifPresentOrElse(
                                    u -> {
                                        u.setUsername("student");
                                        u.setPassword("student123");
                                        userRepository.save(u);
                                        log.info("Configured credentials for existing student user {}.", u.getName());
                                    },
                                    () -> {
                                        userRepository.save(User.builder()
                                                .username("student")
                                                .password("student123")
                                                .name("Student")
                                                .role(User.Role.STUDENT)
                                                .build());
                                        log.info("Seeded default student user: student / student123");
                                    }
                            );
                }
        );

        // Ensure default admin user exists with credentials
        userRepository.findByUsernameIgnoreCase("admin").ifPresentOrElse(
                admin -> {
                    if (admin.getPassword() == null || admin.getPassword().isBlank()) {
                        admin.setPassword("admin123");
                        userRepository.save(admin);
                        log.info("Updated password for admin user.");
                    }
                },
                () -> {
                    // Check if an existing admin user has no username
                    userRepository.findAll().stream()
                            .filter(u -> u.getRole() == User.Role.ADMIN && (u.getUsername() == null || u.getUsername().isBlank()))
                            .findFirst()
                            .ifPresentOrElse(
                                    u -> {
                                        u.setUsername("admin");
                                        u.setPassword("admin123");
                                        userRepository.save(u);
                                        log.info("Configured credentials for existing admin user {}.", u.getName());
                                    },
                                    () -> {
                                        userRepository.save(User.builder()
                                                .username("admin")
                                                .password("admin123")
                                                .name("Admin")
                                                .role(User.Role.ADMIN)
                                                .build());
                                        log.info("Seeded default admin user: admin / admin123");
                                    }
                            );
                }
        );

        log.info("Default users initialization completed.");
    }

}
