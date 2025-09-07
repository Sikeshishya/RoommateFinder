package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import com.roommatefinder.RoommateFinder.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BatchMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(BatchMatchingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private SmartMatchingService smartMatchingService;

    @Autowired
    private NotificationService notificationService;

    // Run daily at 2 AM to update matches for all users
    @Scheduled(cron = "0 0 2 * * *")
    public void updateAllUserMatches() {
        logger.info("Starting daily batch matching process");

        List<User> activeUsers = userRepository.findAll().stream()
                .filter(user -> user.getLastActive() != null &&
                        user.getLastActive().isAfter(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.toList());

        logger.info("Processing {} active users", activeUsers.size());

        // Process users in batches of 50
        int batchSize = 50;
        for (int i = 0; i < activeUsers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, activeUsers.size());
            List<User> batch = activeUsers.subList(i, endIndex);

            processBatchAsync(batch);
        }

        logger.info("Completed daily batch matching process");
    }

    @Async
    public CompletableFuture<Void> processBatchAsync(List<User> users) {
        return CompletableFuture.runAsync(() -> {
            for (User user : users) {
                try {
                    updateUserMatches(user);
                } catch (Exception e) {
                    logger.error("Error processing matches for user {}: {}", user.getId(), e.getMessage());
                }
            }
        });
    }

    private void updateUserMatches(User user) {
        // Clean old matches (older than 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        matchResultRepository.deleteByUserIdAndCalculatedAtBefore(user.getId(), thirtyDaysAgo);

        // Generate new matches
        List<MatchResult> newMatches = smartMatchingService.findCompatibleRoommates(user.getId(), 20);

        // Filter out existing matches
        List<MatchResult> uniqueMatches = newMatches.stream()
                .filter(match -> !matchResultRepository.existsByUserIdAndPotentialRoommateId(
                        user.getId(), match.getPotentialRoommateId()))
                .collect(Collectors.toList());

        if (!uniqueMatches.isEmpty()) {
            matchResultRepository.saveAll(uniqueMatches);

            // Send notification for high-quality matches
            List<MatchResult> highQualityMatches = uniqueMatches.stream()
                    .filter(match -> match.getOverallScore() >= 0.8)
                    .limit(3)
                    .collect(Collectors.toList());

            if (!highQualityMatches.isEmpty()) {
                notificationService.sendMatchNotifications(user, highQualityMatches);
            }
        }
    }

    // Clean up expired matches weekly
    @Scheduled(cron = "0 0 3 * * SUN")
    public void cleanupExpiredMatches() {
        LocalDateTime expiredDate = LocalDateTime.now().minusDays(60);

        List<MatchResult> expiredMatches = matchResultRepository.findRecentMatches(expiredDate);
        expiredMatches.forEach(match -> {
            if ("pending".equals(match.getStatus())) {
                match.setStatus("expired");
            }
        });

        matchResultRepository.saveAll(expiredMatches);
        logger.info("Marked {} matches as expired", expiredMatches.size());
    }
}