package com.roommatefinder.RoommateFinder.config; // Or any appropriate package

import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (!userRepository.existsByRolesContaining("ROLE_ADMIN")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpassword123")); // Use a strong password!
            adminUser.setEmail("admin@example.com");
            // 👈 Set the role explicitly to ADMIN
            adminUser.setRoles(Set.of("ROLE_ADMIN"));

            userRepository.save(adminUser);
            System.out.println("✅ Initial ADMIN user created!");
        }
    }
}