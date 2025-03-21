package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyRepository extends MongoRepository<Property, String> {
}
