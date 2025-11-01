package com.teamwork.api.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.teamwork.api.model.Role;
import com.teamwork.api.model.User;
import com.teamwork.api.repository.RoleRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AdminConfig {

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@teamwork.com";

            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseGet(() -> roleRepository.save(new Role(0, "ADMIN")));

                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseGet(() -> roleRepository.save(new Role(0, "USER")));

                User admin = User.builder()
                        .username("admin")
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode("Admin@123456"))
                        .firstName("Admin")
                        .lastName("User")
                        .roles(List.of(adminRole, userRole))
                        .active(true)
                        .build();

                userRepository.save(admin);
                log.info("Admin user created successfully with email: {}", adminEmail);
            } else {
                log.info("Admin user already exists");
            }
        };
    }
}