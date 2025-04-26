package com.blog.blogapp.config;

import com.blog.blogapp.entity.User;
import com.blog.blogapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin already exists to avoid duplicates
            if (!userRepository.existsByEmail("admin@abc.com")) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@abc.com");
                admin.setPassword(passwordEncoder.encode("admin1"));
                admin.setRole("ADMIN");

                // Save the admin user
                userRepository.save(admin);

                System.out.println("Admin user created successfully!");
            } else {
                System.out.println("Admin user already exists. Skipping creation.");
            }
        };
    }
}