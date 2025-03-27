package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.Property;
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
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestBody Property property, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7); // Remove "Bearer "
            String userId = jwtUtil.extractUsername(jwtToken); // Extract user ID from token

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
        Optional<Property> updated = Optional.ofNullable(propertyService.updateProperty(id, updatedProperty));
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePropertyById(@PathVariable String id) {
        boolean deleted = propertyService.deletePropertyById(id);
        if (deleted) {
            return ResponseEntity.ok("Property with ID '" + id + "' has been deleted successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
