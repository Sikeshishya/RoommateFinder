package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.Property;
import com.roommatefinder.RoommateFinder.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(@RequestBody Property property, Principal principal) {
        // Set the owner of the property to the currently authenticated user
        property.setUserId(principal.getName());
        Property savedProperty = propertyService.saveProperty(property);
        return ResponseEntity.ok(savedProperty);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable String id) {
        Property property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Property>> getPropertiesByUser(Principal principal) {
        // Get properties for the currently authenticated user
        List<Property> properties = propertyService.getPropertiesByUserId(principal.getName());
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable String id,
                                                   @RequestBody Property propertyDetails,
                                                   Principal principal) {
        // The service layer now handles the authorization check
        Property updatedProperty = propertyService.updateProperty(id, propertyDetails, principal.getName());
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePropertyById(@PathVariable String id, Principal principal) {
        // The service layer now handles the authorization check
        propertyService.deletePropertyById(id, principal.getName());
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }

    @GetMapping("/filter/advanced")
    public ResponseEntity<List<Property>> advancedFilter(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) String preferredGender) {

        List<Property> properties = propertyService.filterPropertiesByAllCriteria(
                location,
                minBudget != null ? minBudget : 0,
                maxBudget != null ? maxBudget : Double.MAX_VALUE,
                preferredGender
        );
        return ResponseEntity.ok(properties);
    }
}
