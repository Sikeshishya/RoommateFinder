package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.User;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    void deleteUserByUsername(String username);
    User updateUser(String username, User updatedUser);
    User getUserByUsername(String username);  // New method to fetch user profile
}
