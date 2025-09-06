package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.RefreshToken;
import com.roommatefinder.RoommateFinder.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
