package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MatchingCacheService {

    @Autowired
    private SmartMatchingService smartMatchingService;

    @Cacheable(value = "userMatches", key = "#userId + '_' + #limit")
    public List<MatchResult> getCachedMatches(String userId, int limit) {
        return smartMatchingService.findCompatibleRoommates(userId, limit);
    }

    @CacheEvict(value = "userMatches", key = "#userId + '*'")
    public void evictUserMatches(String userId) {
        // Cache will be cleared for this user
    }

    @CacheEvict(value = "userMatches", allEntries = true)
    public void evictAllMatches() {
        // Clear all cached matches
    }

    // Async match calculation for better performance
    public CompletableFuture<List<MatchResult>> calculateMatchesAsync(String userId, int limit) {
        return CompletableFuture.supplyAsync(() ->
                smartMatchingService.findCompatibleRoommates(userId, limit)
        );
    }
}