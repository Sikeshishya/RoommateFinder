package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.repository.PropertyRepository;
import com.roommatefinder.RoommateFinder.service.PropertyService;
import com.roommatefinder.RoommateFinder.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private final PropertyRepository propertyRepository;


    @Autowired
    private JwtUtil jwtUtil;

    public PropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Existing endpoints
    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestBody Property property,
                                                   @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7); // Remove "Bearer "
            String userId = jwtUtil.extractUsername(jwtToken);
            property.setUserId(userId);
            Property savedProperty = propertyService.saveProperty(property);
            return ResponseEntity.ok(savedProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable String id) {
        Optional<Property> property = Optional.ofNullable(propertyService.getPropertyById(id));
        return property.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<Property>> getPropertiesByUser(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String userId = jwtUtil.extractUsername(jwtToken);
            List<Property> properties = propertyService.getPropertiesByUserId(userId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable String id, @RequestBody Property updatedProperty) {
        Property updated = propertyService.updateProperty(id, updatedProperty);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePropertyById(@PathVariable String id) {
        boolean deleted = propertyService.deletePropertyById(id);
        return deleted ? ResponseEntity.ok("Property deleted successfully") : ResponseEntity.notFound().build();
    }

    // New filtering endpoints
    @GetMapping("/filter/budget")
    public ResponseEntity<List<Property>> filterByBudgetRange(
            @RequestParam double min,
            @RequestParam double max) {
        List<Property> properties = propertyService.filterPropertiesByBudget(min, max);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/filter/preferences")
    public ResponseEntity<List<Property>> filterByPreferences(
            @RequestParam String gender) {
        List<Property> properties = propertyService.filterPropertiesByPreferences(gender);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/filter/location")
    public ResponseEntity<List<Property>> filterByLocation(
            @RequestParam String location) {
        List<Property> properties = propertyService.filterPropertiesByLocation(location);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/filter/advanced")
    public ResponseEntity<List<Property>> advancedFilter(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) String preferredGender) {

        List<Property> properties;

        if (location != null && minBudget != null && maxBudget != null && preferredGender != null) {
            properties = propertyService.filterPropertiesByAllCriteria(location, minBudget, maxBudget, preferredGender);
        } else if (location != null && minBudget != null && maxBudget != null) {
            properties = propertyService.filterPropertiesByLocationAndBudget(location, minBudget, maxBudget);
        } else if (minBudget != null && maxBudget != null && preferredGender != null) {
            properties = propertyService.filterPropertiesByBudgetAndPreferences(minBudget, maxBudget, preferredGender);
        } else if (location != null && preferredGender != null) {
            properties = propertyService.filterPropertiesByLocationAndPreferences(location, preferredGender);
        } else if (minBudget != null && maxBudget != null) {
            properties = propertyService.filterPropertiesByBudget(minBudget, maxBudget);
        } else if (location != null) {
            properties = propertyService.filterPropertiesByLocation(location);
        } else if (preferredGender != null) {
            properties = propertyService.filterPropertiesByPreferences(preferredGender);
        } else {
            properties = propertyService.getAllProperties();
        }

        return ResponseEntity.ok(properties);
    }

}