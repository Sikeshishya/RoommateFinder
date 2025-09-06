package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.Property;
import java.util.List;

public interface PropertyService {
    Property saveProperty(Property property);
    List<Property> getAllProperties();
    Property getPropertyById(String id);

    // ✅ Method signature updated to include username for authorization
    boolean deletePropertyById(String id, String currentUsername);

    // ✅ Method signature updated to include username for authorization
    Property updateProperty(String id, Property updatedProperty, String currentUsername);

    List<Property> getPropertiesByUserId(String userId);

    // Filtering methods from original code
    List<Property> filterPropertiesByBudget(double minBudget, double maxBudget);
    List<Property> filterPropertiesByPreferences(String preferredGender);
    List<Property> filterPropertiesByLocation(String location);
    List<Property> filterPropertiesByBudgetAndPreferences(double minBudget, double maxBudget, String preferredGender);
    List<Property> filterPropertiesByLocationAndBudget(String location, double minBudget, double maxBudget);
    List<Property> filterPropertiesByLocationAndPreferences(String location, String preferredGender);
    List<Property> filterPropertiesByAllCriteria(String location, double minBudget, double maxBudget, String preferredGender);
}
