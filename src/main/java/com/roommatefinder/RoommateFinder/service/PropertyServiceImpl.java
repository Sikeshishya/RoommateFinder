package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return propertyRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deletePropertyById(String id) {
        propertyRepository.deleteById(id);
        return false;
    }

    @Override
    public Property updateProperty(String id, Property updatedProperty) {
        Property existingProperty = propertyRepository.findById(id).orElse(null);

        if (existingProperty != null) {
            existingProperty.setTitle(updatedProperty.getTitle());
            existingProperty.setDescription(updatedProperty.getDescription());
            existingProperty.setLocation(updatedProperty.getLocation());
            existingProperty.setBudget(updatedProperty.getBudget());
            existingProperty.setPreferredGender(updatedProperty.getPreferredGender());

            return propertyRepository.save(existingProperty);
        }
        return null;
    }

    @Override
    public List<Property> getPropertiesByUserId(String userId) {
        return propertyRepository.findByUserId(userId);  // 🆕 Fetch properties for a user
    }
}
