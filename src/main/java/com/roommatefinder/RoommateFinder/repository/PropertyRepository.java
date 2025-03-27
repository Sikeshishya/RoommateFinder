package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByUserId(String userId);  // 🆕 Fetch properties by userId
}
