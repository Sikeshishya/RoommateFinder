package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {

    // Basic filtering methods
    List<Property> findByUserId(String userId);

    @Query("{ 'preferredGender' : { $regex : ?0, $options: 'i' } }")
    List<Property> findByPreferredGenderIgnoreCase(String gender);

    @Query("{ 'location' : { $ne: null, $regex : ?0, $options: 'i' } }")
    List<Property> findByLocationIgnoreCase(String location);

    @Query("{ 'budget' : { $gte: ?0, $lte: ?1 } }")
    List<Property> findByBudgetBetween(double minBudget, double maxBudget);

    // Combined filter method with all parameters
    @Query("{" +
            " '$and': [" +
            "   { 'location': { $ne: null, $regex: ?0, $options: 'i' } }," +
            "   { 'budget': { $gte: ?1, $lte: ?3 } }," +
            "   { 'preferredGender': { $regex: ?2, $options: 'i' } }" +
            " ]" +
            "}")
    List<Property> findByLocationBudgetAndGender(
            String location,
            double minBudget,
            String preferredGender,
            double maxBudget
    );
}