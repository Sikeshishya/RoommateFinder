package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.exception.AccessDeniedException;
import com.roommatefinder.RoommateFinder.exception.ResourceNotFoundException;
import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Property getPropertyById(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    @Override
    public boolean deletePropertyById(String id, String currentUsername) {
        // Find the property first to verify ownership.
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        // ✅ Authorization Check: Ensure the user owns the property.
        if (!property.getUserId().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have permission to delete this property.");
        }

        propertyRepository.delete(property);
        return true;
    }

    @Override
    public Property updateProperty(String id, Property updatedProperty, String currentUsername) {
        // Find the property to ensure it exists.
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        // ✅ Authorization Check: Ensure the user owns the property.
        if (!existingProperty.getUserId().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have permission to update this property.");
        }

        // Update fields from the request body.
        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setDescription(updatedProperty.getDescription());
        existingProperty.setLocation(updatedProperty.getLocation());
        existingProperty.setBudget(updatedProperty.getBudget());
        existingProperty.setPreferredGender(updatedProperty.getPreferredGender());

        return propertyRepository.save(existingProperty);
    }

    @Override
    public List<Property> getPropertiesByUserId(String userId) {
        return propertyRepository.findByUserId(userId);
    }

    // --- Filtering Methods from original code ---

    @Override
    public List<Property> filterPropertiesByBudget(double minBudget, double maxBudget) {
        return propertyRepository.findByBudgetBetween(minBudget, maxBudget);
    }

    @Override
    public List<Property> filterPropertiesByPreferences(String preferredGender) {
        if (preferredGender == null || preferredGender.isEmpty()) {
            return Collections.emptyList();
        }
        return propertyRepository.findByPreferredGenderIgnoreCase(preferredGender);
    }

    @Override
    public List<Property> filterPropertiesByLocation(String location) {
        if (location == null || location.isEmpty()) {
            return Collections.emptyList();
        }
        return propertyRepository.findByLocationIgnoreCase(location);
    }

    @Override
    public List<Property> filterPropertiesByBudgetAndPreferences(double minBudget, double maxBudget, String preferredGender) {
        return propertyRepository.findByBudgetBetweenAndPreferredGenderIgnoreCase(minBudget, maxBudget, preferredGender);
    }

    @Override
    public List<Property> filterPropertiesByLocationAndBudget(String location, double minBudget, double maxBudget) {
        return propertyRepository.findByLocationIgnoreCaseAndBudgetBetween(location, minBudget, maxBudget);
    }

    @Override
    public List<Property> filterPropertiesByLocationAndPreferences(String location, String preferredGender) {
        return propertyRepository.findByLocationIgnoreCaseAndPreferredGenderIgnoreCase(location, preferredGender);
    }

    @Override
    public List<Property> filterPropertiesByAllCriteria(String location, double minBudget, double maxBudget, String preferredGender) {
        return propertyRepository.findByLocationIgnoreCaseAndBudgetBetweenAndPreferredGenderIgnoreCase(location, minBudget, maxBudget, preferredGender);
    }
}
