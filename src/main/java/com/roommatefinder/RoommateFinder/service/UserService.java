package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);  // Renamed to emphasize user registration
    List<User> getAllUsers();
    void deleteUserByUsername(String username);
    Optional<User> findUserByUsername(String username);  // Returns Optional<User>
    User updateUserDetails(String username, User updatedUser);
}
