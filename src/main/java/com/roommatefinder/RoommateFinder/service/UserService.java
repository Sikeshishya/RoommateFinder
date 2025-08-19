package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    List<User> getAllUsers();

    void deleteUserByUsername(String username);

    Optional<User> findUserByUsername(String username);

    User updateUser(String username, User userDetails);
}