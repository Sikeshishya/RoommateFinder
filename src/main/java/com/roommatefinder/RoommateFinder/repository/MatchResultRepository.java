package com.roommatefinder.RoommateFinder.repository;

import com.roommatefinder.RoommateFinder.model.MatchResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchResultRepository extends MongoRepository<MatchResult, String> {

    List<MatchResult> findByUserIdOrderByOverallScoreDesc(String userId);

    List<MatchResult> findByUserIdAndStatusOrderByOverallScoreDesc(String userId, String status);

    List<MatchResult> findByPotentialRoommateId(String potentialRoommateId);

    @Query("{ 'userId': ?0, 'overallScore': { $gte: ?1 } }")
    List<MatchResult> findByUserIdAndMinScore(String userId, double minScore);

    @Query("{ 'calculatedAt': { $gte: ?0 } }")
    List<MatchResult> findRecentMatches(LocalDateTime since);

    void deleteByUserIdAndCalculatedAtBefore(String userId, LocalDateTime before);

    boolean existsByUserIdAndPotentialRoommateId(String userId, String potentialRoommateId);
}