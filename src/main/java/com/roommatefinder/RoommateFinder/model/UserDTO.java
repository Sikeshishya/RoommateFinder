package com.roommatefinder.RoommateFinder.model;

import com.roommatefinder.RoommateFinder.model.User;
import java.util.Set; // 👈 Import Set

public class UserDTO {
    private String id;
    private String username;
    private String email;

    // ✅ CHANGED: The role field is now a Set to match the User entity.
    private Set<String> roles;

    // Constructor to convert User entity to UserDTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        // ✅ CHANGED: It now gets the entire set of roles.
        this.roles = user.getRoles();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ✅ CHANGED: Getter and setter for the new 'roles' Set.
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}