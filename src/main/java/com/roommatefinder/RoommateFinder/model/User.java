package com.roommatefinder.RoommateFinder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashSet; // 👈 Import HashSet
import java.util.List;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String email;
    private String password;

    // ✅ CHANGED: The role field is now a Set to hold multiple roles.
    private Set<String> roles = new HashSet<>();

    private String phoneNumber;
    private String preferredLocation;
    private double budget;
    private String preferredGender;

    // Enhanced fields for ML matching
    private UserPreferences preferences;
    private UserLifestyle lifestyle;
    private List<String> interests;
    private UserCompatibilityProfile compatibilityProfile;
    private LocalDateTime lastActive;
    private boolean isVerified = false;
    private double averageRating = 0.0;
    private int totalRatings = 0;

    // Getters and setters for existing fields...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ✅ CHANGED: Getter and setter for the new 'roles' Set.
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getPreferredGender() { return preferredGender; }
    public void setPreferredGender(String preferredGender) { this.preferredGender = preferredGender; }

    // --- Other getters and setters remain the same ---
    public UserPreferences getPreferences() { return preferences; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }

    public UserLifestyle getLifestyle() { return lifestyle; }
    public void setLifestyle(UserLifestyle lifestyle) { this.lifestyle = lifestyle; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public UserCompatibilityProfile getCompatibilityProfile() { return compatibilityProfile; }
    public void setCompatibilityProfile(UserCompatibilityProfile compatibilityProfile) {
        this.compatibilityProfile = compatibilityProfile;
    }

    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }



    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    // ❌ REMOVED: The old single-string 'role' getter/setter and the empty 'setRoles' method.
}