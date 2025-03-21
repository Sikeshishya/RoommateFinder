package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.Property;

import java.util.List;

public interface PropertyService {
    Property saveProperty(Property property);
    List<Property> getAllProperties();
    Property getPropertyById(String id);
    void deletePropertyById(String id);
    Property updateProperty(String id, Property updatedProperty);
}
