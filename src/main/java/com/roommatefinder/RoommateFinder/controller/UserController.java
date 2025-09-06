package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.model.UserDTO; // 👈 Import UserDTO
import com.roommatefinder.RoommateFinder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors; // 👈 Import Collectors

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Note: It's better practice to only inject the UserService and not the repository directly.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    // ✅ Change the return type to UserDTO
    public ResponseEntity<UserDTO> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        // ✨ Convert the saved User to a UserDTO before returning
        return ResponseEntity.ok(new UserDTO(savedUser));
    }

    @GetMapping("/all")
    // ✅ Change the return type to a List of UserDTOs
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // ✨ Convert the list of User entities to a list of UserDTOs
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PutMapping("/update/{username}")
    // ✅ Change the return type to UserDTO
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username,
                                              @RequestBody User userDetails,
                                              Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User updatedUser = userService.updateUser(username, userDetails);
        // ✨ Convert the updated User to a UserDTO before returning
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }

    @GetMapping("/{username}")
    // ✅ Change the return type to UserDTO
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        // ✨ Find the user and map it to a UserDTO for the response
        return userService.findUserByUsername(username)
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}