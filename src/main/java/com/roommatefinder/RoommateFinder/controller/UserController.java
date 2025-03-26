package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete/{username}")
    public String deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return "User with username '" + username + "' has been deleted successfully!";
    }

    @PutMapping("/update/{username}")
    public User updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        return userService.updateUser(username, updatedUser);
    }

    @GetMapping("/{username}")
    public User getUserProfile(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}
