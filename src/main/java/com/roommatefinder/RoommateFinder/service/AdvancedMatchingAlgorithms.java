package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvancedMatchingAlgorithms {

    // Implement collaborative filtering based on user interactions
    public List<MatchResult> collaborativeFiltering(String userId, List<User> allUsers, List<MatchResult> existingMatches) {
        Map<String, Set<String>> userInteractions = buildUserInteractionMatrix(existingMatches);

        // Find users with similar interaction patterns
        List<String> similarUsers = findSimilarUsers(userId, userInteractions);

        // Recommend roommates that similar users liked
        return generateCollaborativeRecommendations(userId, similarUsers, existingMatches);
    }

    private Map<String, Set<String>> buildUserInteractionMatrix(List<MatchResult> matches) {
        Map<String, Set<String>> interactions = new HashMap<>();

        for (MatchResult match : matches) {
            if (match.isFavorited() || "accepted".equals(match.getStatus())) {
                interactions.computeIfAbsent(match.getUserId(), k -> new HashSet<>())
                        .add(match.getPotentialRoommateId());
            }
        }

        return interactions;
    }

    private List<String> findSimilarUsers(String userId, Map<String, Set<String>> interactions) {
        Set<String> userLikes = interactions.getOrDefault(userId, new HashSet<>());

        return interactions.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(userId))
                .map(entry -> {
                    double similarity = calculateJaccardSimilarity(userLikes, entry.getValue());
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), similarity);
                })
                .filter(entry -> entry.getValue() > 0.2) // Minimum similarity threshold
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private List<MatchResult> generateCollaborativeRecommendations(String userId, List<String> similarUsers, List<MatchResult> existingMatches) {
        Map<String, Integer> candidateScores = new HashMap<>();

        // Count how many similar users liked each candidate
        for (MatchResult match : existingMatches) {
            if (similarUsers.contains(match.getUserId()) &&
                    (match.isFavorited() || "accepted".equals(match.getStatus()))) {
                candidateScores.merge(match.getPotentialRoommateId(), 1, Integer::sum);
            }
        }

        // Convert to MatchResult objects
        return candidateScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    MatchResult result = new MatchResult(userId, entry.getKey(), 0.0);
                    // Set score based on collaborative filtering strength
                    double collaborativeScore = Math.min(1.0, entry.getValue() / 5.0);
                    result.setOverallScore(collaborativeScore);
                    result.setMatchReason("Recommended based on similar users' preferences");
                    return result;
                })
                .collect(Collectors.toList());
    }

    // Implement location-based clustering for better geographical matching
    public List<MatchResult> locationClusteringMatch(User targetUser, List<User> candidates) {
        // Group users by location clusters
        Map<String, List<User>> locationClusters = candidates.stream()
                .filter(u -> u.getPreferredLocation() != null)
                .collect(Collectors.groupingBy(u -> extractLocationCluster(u.getPreferredLocation())));

        String targetCluster = extractLocationCluster(targetUser.getPreferredLocation());
        List<User> nearbyUsers = locationClusters.getOrDefault(targetCluster, Collections.emptyList());

        return nearbyUsers.stream()
                .filter(u -> !u.getId().equals(targetUser.getId()))
                .map(u -> {
                    MatchResult result = new MatchResult(targetUser.getId(), u.getId(), 0.0);
                    double locationScore = calculateLocationProximityScore(targetUser, u);
                    result.setOverallScore(locationScore);
                    result.setMatchReason("Located in same area: " + targetCluster);
                    return result;
                })
                .sorted((r1, r2) -> Double.compare(r2.getOverallScore(), r1.getOverallScore()))
                .limit(15)
                .collect(Collectors.toList());
    }

    private String extractLocationCluster(String location) {
        if (location == null) return "unknown";

        // Simple clustering by first part of location
        String[] parts = location.toLowerCase().split(",");
        return parts.length > 0 ? parts[0].trim() : "unknown";
    }

    private double calculateLocationProximityScore(User user1, User user2) {
        String loc1 = user1.getPreferredLocation();
        String loc2 = user2.getPreferredLocation();

        if (loc1 == null || loc2 == null) return 0.3;

        // In production, use actual geographical distance calculation
        if (loc1.equalsIgnoreCase(loc2)) return 1.0;

        // Simple string similarity for demo
        return calculateStringSimilarity(loc1.toLowerCase(), loc2.toLowerCase());
    }

    private double calculateStringSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;

        int editDistance = calculateEditDistance(s1, s2);
        return (maxLength - editDistance) / (double) maxLength;
    }

    private int calculateEditDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}