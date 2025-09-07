package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.exception.ResourceNotFoundException;
import com.roommatefinder.RoommateFinder.exception.UsernameAlreadyExistsException;
import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set; // 👈 Import Set

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(s -> {
            throw new UsernameAlreadyExistsException("Username '" + user.getUsername() + "' is already taken!");
        });

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ CHANGED: This now correctly sets the default role for a new user.
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("ROLE_USER"));
        }

        return userRepository.save(user);
    }

    // --- All other methods remain the same ---

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserByUsername(String username) {
        if (!userRepository.findByUsername(username).isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        userRepository.deleteByUsername(username);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(String username, User userDetails) {
        return userRepository.findByUsername(username).map(user -> {
            if (StringUtils.hasText(userDetails.getEmail())) {
                user.setEmail(userDetails.getEmail());
            }
            if (StringUtils.hasText(userDetails.getPhoneNumber())) {
                user.setPhoneNumber(userDetails.getPhoneNumber());
            }
            if (StringUtils.hasText(userDetails.getPreferredLocation())) {
                user.setPreferredLocation(userDetails.getPreferredLocation());
            }
            if (userDetails.getBudget() > 0) {
                user.setBudget(userDetails.getBudget());
            }
            if (StringUtils.hasText(userDetails.getPreferredGender())) {
                user.setPreferredGender(userDetails.getPreferredGender());
            }
            if (StringUtils.hasText(userDetails.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
}