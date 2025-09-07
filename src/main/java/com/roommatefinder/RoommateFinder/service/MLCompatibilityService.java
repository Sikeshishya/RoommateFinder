package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MLCompatibilityService {

    // Big Five personality traits
    private static final String[] PERSONALITY_TRAITS = {
            "openness", "conscientiousness", "extraversion", "agreeableness", "neuroticism"
    };

    public double calculatePersonalityMatch(UserCompatibilityProfile profile1, UserCompatibilityProfile profile2) {
        Map<String, Double> traits1 = profile1.getPersonalityTraits();
        Map<String, Double> traits2 = profile2.getPersonalityTraits();

        if (traits1 == null || traits2 == null) {
            return 0.5;
        }

        double totalScore = 0.0;
        int validTraits = 0;

        for (String trait : PERSONALITY_TRAITS) {
            if (traits1.containsKey(trait) && traits2.containsKey(trait)) {
                double score1 = traits1.get(trait);
                double score2 = traits2.get(trait);

                // Calculate compatibility based on trait-specific logic
                double compatibility = calculateTraitCompatibility(trait, score1, score2);
                totalScore += compatibility;
                validTraits++;
            }
        }

        if (validTraits == 0) return 0.5;

        // Add other compatibility factors
        double baseScore = totalScore / validTraits;
        double communicationBonus = calculateCommunicationCompatibility(profile1, profile2) * 0.1;
        double responsibilityBonus = calculateResponsibilityCompatibility(profile1, profile2) * 0.1;

        return Math.min(1.0, baseScore + communicationBonus + responsibilityBonus);
    }

    private double calculateTraitCompatibility(String trait, double score1, double score2) {
        switch (trait) {
            case "extraversion":
                // Similar extraversion levels work well
                double diff = Math.abs(score1 - score2);
                return Math.max(0.3, 1.0 - (diff / 10.0));

            case "conscientiousness":
                // Both should be reasonably conscientious
                double avgConsc = (score1 + score2) / 2;
                return Math.min(1.0, avgConsc / 7.0); // Reward higher conscientiousness

            case "agreeableness":
                // Higher agreeableness is better for roommates
                double avgAgree = (score1 + score2) / 2;
                return Math.min(1.0, avgAgree / 8.0);

            case "neuroticism":
                // Lower neuroticism is better
                double avgNeuro = (score1 + score2) / 2;
                return Math.max(0.2, 1.0 - (avgNeuro / 10.0));

            case "openness":
                // Moderate difference in openness can be good
                double openDiff = Math.abs(score1 - score2);
                return openDiff <= 3 ? 1.0 : Math.max(0.4, 1.0 - (openDiff / 10.0));

            default:
                return 0.5;
        }
    }

    private double calculateCommunicationCompatibility(UserCompatibilityProfile profile1, UserCompatibilityProfile profile2) {
        double comm1 = profile1.getCommunicationScore();
        double comm2 = profile2.getCommunicationScore();

        // Both should have decent communication scores
        double avgComm = (comm1 + comm2) / 2;
        return avgComm / 10.0;
    }

    private double calculateResponsibilityCompatibility(UserCompatibilityProfile profile1, UserCompatibilityProfile profile2) {
        double resp1 = profile1.getResponsibilityScore();
        double resp2 = profile2.getResponsibilityScore();

        // Both should be responsible
        double avgResp = (resp1 + resp2) / 2;
        return avgResp / 10.0;
    }

    public List<MatchResult> reRankMatches(User currentUser, List<MatchResult> matches) {
        // Simple ML re-ranking based on user history and preferences
        // In production, this would use actual ML models

        return matches.stream()
                .map(match -> {
                    // Apply ML adjustments
                    double adjustedScore = applyMLAdjustments(currentUser, match);
                    match.setOverallScore(adjustedScore);
                    return match;
                })
                .sorted((m1, m2) -> Double.compare(m2.getOverallScore(), m1.getOverallScore()))
                .collect(Collectors.toList());
    }

    private double applyMLAdjustments(User currentUser, MatchResult match) {
        double originalScore = match.getOverallScore();
        double adjustment = 0.0;

        // Boost score based on user preferences pattern
        // This is a simplified version - in production, use trained models

        // Example adjustments:
        // - If user consistently matches with certain personality types
        // - If user prefers certain lifestyle patterns
        // - If user has had successful matches with similar profiles

        // For demo purposes, add small random variation to simulate ML
        Random random = new Random(currentUser.getId().hashCode() + match.getPotentialRoommateId().hashCode());
        adjustment = (random.nextDouble() - 0.5) * 0.1; // ±5% adjustment

        return Math.max(0.0, Math.min(1.0, originalScore + adjustment));
    }
}