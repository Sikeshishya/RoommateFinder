package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {

    // Basic filtering methods
    List<Property> findByUserId(String userId);
    List<Property> findByPreferredGenderIgnoreCase(String gender);
    List<Property> findByLocationIgnoreCase(String location);
    List<Property> findByBudgetBetween(double minBudget, double maxBudget);

    // Combined filter methods
    List<Property> findByBudgetBetweenAndPreferredGenderIgnoreCase(double minBudget, double maxBudget, String preferredGender);
    List<Property> findByLocationIgnoreCaseAndBudgetBetween(String location, double minBudget, double maxBudget);
    List<Property> findByLocationIgnoreCaseAndPreferredGenderIgnoreCase(String location, String preferredGender);
    List<Property> findByLocationIgnoreCaseAndBudgetBetweenAndPreferredGenderIgnoreCase(String location, double minBudget, double maxBudget, String preferredGender);
}