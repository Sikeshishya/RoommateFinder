package com.roommatefinder.RoommateFinder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "properties")
public class Property {

    @Id
    private String id;
    private String title;
    private String description;
    private String location;
    private double budget;
    private String preferredGender;
    private String userId;  // 🆕 Added userId field

    public Property() {}

    public Property(String title, String description, String location, double budget, String preferredGender, String userId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.budget = budget;
        this.preferredGender = preferredGender;
        this.userId = userId; // 🆕 Initialize userId
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(String preferredGender) {
        this.preferredGender = preferredGender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
