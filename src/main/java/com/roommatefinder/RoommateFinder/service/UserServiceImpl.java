package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.exception.ResourceNotFoundException;
import com.roommatefinder.RoommateFinder.exception.UsernameAlreadyExistsException;
import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

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
        // ✅ Throw specific exception for username conflict
        userRepository.findByUsername(user.getUsername()).ifPresent(s -> {
            throw new UsernameAlreadyExistsException("Username '" + user.getUsername() + "' is already taken!");
        });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserByUsername(String username) {
        // Optional: Check if user exists before deleting
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
        // ✅ Throw specific exception if user to update is not found
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
