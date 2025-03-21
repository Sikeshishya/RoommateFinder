package com.roommatefinder.RoommateFinder.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "properties")
@Data
public class Property {

    @Id
    private String id;
    private String title;
    private String description;
    private String location;
    private double budget;
    private String preferredGender;

    public Property() {}

    public Property(String title, String description, String location, double budget, String preferredGender) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.budget = budget;
        this.preferredGender = preferredGender;
    }
}
