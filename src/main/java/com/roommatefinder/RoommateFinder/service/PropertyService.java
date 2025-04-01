package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.Property;
import java.util.List;

public interface PropertyService {
    // Existing methods
    Property saveProperty(Property property);
    List<Property> getAllProperties();
    Property getPropertyById(String id);
    boolean deletePropertyById(String id);
    Property updateProperty(String id, Property updatedProperty);
    List<Property> getPropertiesByUserId(String userId);

    // New filtering methods
    List<Property> filterPropertiesByBudget(double minBudget, double maxBudget);
    List<Property> filterPropertiesByPreferences(String preferredGender);
    List<Property> filterPropertiesByLocation(String location);
    List<Property> filterPropertiesByBudgetAndPreferences(double minBudget, double maxBudget, String preferredGender);
    List<Property> filterPropertiesByLocationAndBudget(String location, double minBudget, double maxBudget);
    List<Property> filterPropertiesByLocationAndPreferences(String location, String preferredGender);
    List<Property> filterPropertiesByAllCriteria(String location, double minBudget, double maxBudget, String preferredGender);
}