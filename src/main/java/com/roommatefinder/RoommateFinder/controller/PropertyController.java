package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping("/create")
    public Property createProperty(@RequestBody Property property) {
        return propertyService.saveProperty(property);
    }

    @GetMapping("/all")
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable String id) {
        return propertyService.getPropertyById(id);
    }

    @PutMapping("/update/{id}")
    public Property updateProperty(@PathVariable String id, @RequestBody Property updatedProperty) {
        return propertyService.updateProperty(id, updatedProperty);
    }

    @DeleteMapping("/delete/{id}")
    public String deletePropertyById(@PathVariable String id) {
        propertyService.deletePropertyById(id);
        return "Property with ID '" + id + "' has been deleted successfully!";
    }
}
