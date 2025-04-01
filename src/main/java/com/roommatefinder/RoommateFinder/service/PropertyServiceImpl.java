package com.roommatefinder.RoommateFinder.service;

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

    // Existing methods remain unchanged...

    @Override
    public Property saveProperty(Property property) {
        return null;
    }

    @Override
    public List<Property> getAllProperties() {
        return List.of();
    }

    @Override
    public Property getPropertyById(String id) {
        return null;
    }

    @Override
    public boolean deletePropertyById(String id) {
        return false;
    }

    @Override
    public Property updateProperty(String id, Property updatedProperty) {
        return null;
    }

    @Override
    public List<Property> getPropertiesByUserId(String userId) {
        return List.of();
    }

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
        if (preferredGender == null || preferredGender.isEmpty()) {
            return propertyRepository.findByBudgetBetween(minBudget, maxBudget);
        }
        return propertyRepository.findByLocationBudgetAndGender(
                null, minBudget, preferredGender, maxBudget
        );
    }

    @Override
    public List<Property> filterPropertiesByLocationAndBudget(String location, double minBudget, double maxBudget) {
        if (location == null || location.isEmpty()) {
            return propertyRepository.findByBudgetBetween(minBudget, maxBudget);
        }
        return propertyRepository.findByLocationBudgetAndGender(
                location, minBudget, ".*", maxBudget
        );
    }

    @Override
    public List<Property> filterPropertiesByLocationAndPreferences(String location, String preferredGender) {
        if (location == null || location.isEmpty()) {
            if (preferredGender == null || preferredGender.isEmpty()) {
                return propertyRepository.findAll();
            }
            return propertyRepository.findByPreferredGenderIgnoreCase(preferredGender);
        }
        if (preferredGender == null || preferredGender.isEmpty()) {
            return propertyRepository.findByLocationIgnoreCase(location);
        }
        return propertyRepository.findByLocationBudgetAndGender(
                location, 0, preferredGender, Double.MAX_VALUE
        );
    }

    @Override
    public List<Property> filterPropertiesByAllCriteria(String location, double minBudget, double maxBudget, String preferredGender) {
        if (location == null || location.isEmpty()) {
            return filterPropertiesByBudgetAndPreferences(minBudget, maxBudget, preferredGender);
        }
        if (preferredGender == null || preferredGender.isEmpty()) {
            return filterPropertiesByLocationAndBudget(location, minBudget, maxBudget);
        }
        return propertyRepository.findByLocationBudgetAndGender(
                location, minBudget, preferredGender, maxBudget
        );
    }
}