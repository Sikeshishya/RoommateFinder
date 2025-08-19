package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.service.PropertyService;
import com.roommatefinder.RoommateFinder.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final JwtUtil jwtUtil;

    public PropertyController(PropertyService propertyService, JwtUtil jwtUtil) {
        this.propertyService = propertyService;
        this.jwtUtil = jwtUtil;
    }

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
            // It's better to log the exception here
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable String id) {
        Property property = propertyService.getPropertyById(id);
        return property != null ? ResponseEntity.ok(property) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Property>> getPropertiesByUser(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String userId = jwtUtil.extractUsername(jwtToken);
            List<Property> properties = propertyService.getPropertiesByUserId(userId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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

    @GetMapping("/filter/advanced")
    public ResponseEntity<List<Property>> advancedFilter(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) String preferredGender) {

        // Simplified logic by calling the service method that handles all criteria
        List<Property> properties = propertyService.filterPropertiesByAllCriteria(location, minBudget != null ? minBudget : 0, maxBudget != null ? maxBudget : Double.MAX_VALUE, preferredGender);

        return ResponseEntity.ok(properties);
    }
}