package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import com.roommatefinder.RoommateFinder.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchAnalyticsService {

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getUserMatchingStats(String userId) {
        Map<String, Object> stats = new HashMap<>();

        List<MatchResult> userMatches = matchResultRepository.findByUserIdOrderByOverallScoreDesc(userId);

        stats.put("totalMatches", userMatches.size());
        stats.put("averageScore", calculateAverageScore(userMatches));
        stats.put("highQualityMatches", userMatches.stream()
                .filter(m -> m.getOverallScore() >= 0.8).count());
        stats.put("recentMatches", userMatches.stream()
                .filter(m -> m.getCalculatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count());
        stats.put("favoritedMatches", userMatches.stream()
                .filter(MatchResult::isFavorited).count());

        return stats;
    }

    public Map<String, Object> getSystemMatchingMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<MatchResult> recentMatches = matchResultRepository.findRecentMatches(weekAgo);

        metrics.put("totalMatchesThisWeek", recentMatches.size());
        metrics.put("averageCompatibilityScore", calculateAverageScore(recentMatches));
        metrics.put("userEngagementRate", calculateEngagementRate(recentMatches));
        metrics.put("topCompatibilityFactors", getTopCompatibilityFactors(recentMatches));
        metrics.put("matchingSuccessRate", calculateSuccessRate(recentMatches));

        return metrics;
    }

    public List<String> getPersonalizedRecommendations(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) return Collections.emptyList();

        User user = userOpt.get();
        List<String> recommendations = new ArrayList<>();

        // Analyze user's profile completeness
        if (user.getPreferences() == null) {
            recommendations.add("Complete your preferences to get better matches");
        }

        if (user.getLifestyle() == null) {
            recommendations.add("Add lifestyle information to improve compatibility scoring");
        }

        if (user.getInterests() == null || user.getInterests().isEmpty()) {
            recommendations.add("Add your interests to find like-minded roommates");
        }

        // Analyze matching patterns
        List<MatchResult> userMatches = matchResultRepository.findByUserIdOrderByOverallScoreDesc(userId);
        if (userMatches.isEmpty()) {
            recommendations.add("Update your preferences to see potential matches");
        } else if (userMatches.stream().noneMatch(m -> m.getOverallScore() >= 0.8)) {
            recommendations.add("Consider broadening your search criteria for better matches");
        }

        return recommendations;
    }

    private double calculateAverageScore(List<MatchResult> matches) {
        return matches.stream()
                .mapToDouble(MatchResult::getOverallScore)
                .average()
                .orElse(0.0);
    }

    private double calculateEngagementRate(List<MatchResult> matches) {
        if (matches.isEmpty()) return 0.0;

        long viewedMatches = matches.stream().filter(MatchResult::isViewed).count();
        return (double) viewedMatches / matches.size();
    }

    private List<String> getTopCompatibilityFactors(List<MatchResult> matches) {
        Map<String, Double> factorSums = new HashMap<>();
        Map<String, Integer> factorCounts = new HashMap<>();

        for (MatchResult match : matches) {
            if (match.getCategoryScores() != null) {
                match.getCategoryScores().forEach((factor, score) -> {
                    factorSums.merge(factor, score, Double::sum);
                    factorCounts.merge(factor, 1, Integer::sum);
                });
            }
        }

        return factorSums.entrySet().stream()
                .filter(entry -> factorCounts.get(entry.getKey()) > 0)
                .sorted((e1, e2) -> {
                    double avg1 = e1.getValue() / factorCounts.get(e1.getKey());
                    double avg2 = e2.getValue() / factorCounts.get(e2.getKey());
                    return Double.compare(avg2, avg1);
                })
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateSuccessRate(List<MatchResult> matches) {
        if (matches.isEmpty()) return 0.0;

        long successfulMatches = matches.stream()
                .filter(m -> "accepted".equals(m.getStatus()) || m.isFavorited())
                .count();

        return (double) successfulMatches / matches.size();
    }
}