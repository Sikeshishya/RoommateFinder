package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByRolesContaining(String role);

}
