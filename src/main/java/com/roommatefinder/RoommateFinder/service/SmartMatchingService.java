package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import com.roommatefinder.RoommateFinder.repository.UserRepository;
import com.roommatefinder.RoommateFinder.repository.MatchResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.time.Duration;

@Service
public class SmartMatchingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private MLCompatibilityService mlCompatibilityService;

    // Weight constants for different matching criteria
    private static final double BUDGET_WEIGHT = 0.25;
    private static final double LOCATION_WEIGHT = 0.20;
    private static final double LIFESTYLE_WEIGHT = 0.20;
    private static final double PERSONALITY_WEIGHT = 0.15;
    private static final double PREFERENCES_WEIGHT = 0.15;
    private static final double SOCIAL_WEIGHT = 0.05;

    public List<MatchResult> findCompatibleRoommates(String userId, int limit) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found: " + userId);
        }

        User currentUser = userOpt.get();
        List<User> allUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .filter(u -> isBasicCompatible(currentUser, u))
                .collect(Collectors.toList());

        List<MatchResult> matches = new ArrayList<>();

        for (User potentialRoommate : allUsers) {
            double compatibilityScore = calculateOverallCompatibility(currentUser, potentialRoommate);

            if (compatibilityScore >= 0.6) { // Minimum 60% compatibility
                MatchResult match = new MatchResult(userId, potentialRoommate.getId(), compatibilityScore);
                match.setCategoryScores(calculateCategoryScores(currentUser, potentialRoommate));
                match.setMatchReason(generateMatchReason(currentUser, potentialRoommate, match.getCategoryScores()));
                matches.add(match);
            }
        }

        // Sort by compatibility score and apply ML ranking
        matches = matches.stream()
                .sorted((m1, m2) -> Double.compare(m2.getOverallScore(), m1.getOverallScore()))
                .limit(limit)
                .collect(Collectors.toList());

        // Apply ML-based re-ranking
        matches = mlCompatibilityService.reRankMatches(currentUser, matches);

        // Save match results
        matchResultRepository.saveAll(matches);

        return matches;
    }

    private boolean isBasicCompatible(User user1, User user2) {
        // Check for deal breakers
        if (user1.getPreferences() != null && user1.getPreferences().getDealBreakers() != null) {
            for (String dealBreaker : user1.getPreferences().getDealBreakers()) {
                if (violatesDealBreaker(user2, dealBreaker)) {
                    return false;
                }
            }
        }

        // Check gender preferences
        if (!isGenderCompatible(user1, user2)) {
            return false;
        }

        // Check budget compatibility (within flexibility range)
        if (!isBudgetCompatible(user1, user2)) {
            return false;
        }

        return true;
    }

    private double calculateOverallCompatibility(User user1, User user2) {
        double budgetScore = calculateBudgetCompatibility(user1, user2);
        double locationScore = calculateLocationCompatibility(user1, user2);
        double lifestyleScore = calculateLifestyleCompatibility(user1, user2);
        double personalityScore = calculatePersonalityCompatibility(user1, user2);
        double preferencesScore = calculatePreferencesCompatibility(user1, user2);
        double socialScore = calculateSocialCompatibility(user1, user2);

        return (budgetScore * BUDGET_WEIGHT) +
                (locationScore * LOCATION_WEIGHT) +
                (lifestyleScore * LIFESTYLE_WEIGHT) +
                (personalityScore * PERSONALITY_WEIGHT) +
                (preferencesScore * PREFERENCES_WEIGHT) +
                (socialScore * SOCIAL_WEIGHT);
    }

    private Map<String, Double> calculateCategoryScores(User user1, User user2) {
        Map<String, Double> scores = new HashMap<>();
        scores.put("budget", calculateBudgetCompatibility(user1, user2));
        scores.put("location", calculateLocationCompatibility(user1, user2));
        scores.put("lifestyle", calculateLifestyleCompatibility(user1, user2));
        scores.put("personality", calculatePersonalityCompatibility(user1, user2));
        scores.put("preferences", calculatePreferencesCompatibility(user1, user2));
        scores.put("social", calculateSocialCompatibility(user1, user2));
        return scores;
    }

    private double calculateBudgetCompatibility(User user1, User user2) {
        double budget1 = user1.getBudget();
        double budget2 = user2.getBudget();

        if (budget1 == 0 || budget2 == 0) return 0.5; // Neutral if no budget specified

        double budgetDiff = Math.abs(budget1 - budget2);
        double avgBudget = (budget1 + budget2) / 2;
        double percentageDiff = budgetDiff / avgBudget;

        // Get flexibility from preferences
        double flexibility1 = user1.getPreferences() != null ?
                user1.getPreferences().getBudgetFlexibility() : 0.1;
        double flexibility2 = user2.getPreferences() != null ?
                user2.getPreferences().getBudgetFlexibility() : 0.1;
        double avgFlexibility = (flexibility1 + flexibility2) / 2;

        if (percentageDiff <= avgFlexibility) {
            return 1.0;
        } else if (percentageDiff <= avgFlexibility * 2) {
            return 0.8 - (percentageDiff - avgFlexibility) / avgFlexibility * 0.3;
        } else {
            return Math.max(0.0, 0.5 - percentageDiff * 0.5);
        }
    }

    private double calculateLocationCompatibility(User user1, User user2) {
        String loc1 = user1.getPreferredLocation();
        String loc2 = user2.getPreferredLocation();

        if (loc1 == null || loc2 == null) return 0.5;

        // Simple string matching - in production, use geocoding for distance calculation
        if (loc1.equalsIgnoreCase(loc2)) return 1.0;

        // Check if locations are in same city/area (basic implementation)
        String[] parts1 = loc1.toLowerCase().split(",");
        String[] parts2 = loc2.toLowerCase().split(",");

        for (String part1 : parts1) {
            for (String part2 : parts2) {
                if (part1.trim().equals(part2.trim())) {
                    return 0.7; // Same area/city
                }
            }
        }

        return 0.3; // Different locations
    }

    private double calculateLifestyleCompatibility(User user1, User user2) {
        UserLifestyle lifestyle1 = user1.getLifestyle();
        UserLifestyle lifestyle2 = user2.getLifestyle();

        if (lifestyle1 == null || lifestyle2 == null) return 0.5;

        double score = 0.0;
        int factors = 0;

        // Sleep schedule compatibility
        if (lifestyle1.getUsualSleepTime() != null && lifestyle2.getUsualSleepTime() != null) {
            Duration sleepDiff = Duration.between(lifestyle1.getUsualSleepTime(), lifestyle2.getUsualSleepTime()).abs();
            double sleepScore = Math.max(0, 1.0 - sleepDiff.toMinutes() / 180.0); // 3 hour tolerance
            score += sleepScore * 0.3;
            factors++;
        }

        // Social level compatibility
        if (lifestyle1.getSocialLevel() != null && lifestyle2.getSocialLevel() != null) {
            score += calculateSocialLevelCompatibility(lifestyle1.getSocialLevel(), lifestyle2.getSocialLevel()) * 0.2;
            factors++;
        }

        // Cooking frequency compatibility
        if (lifestyle1.getCookingFrequency() != null && lifestyle2.getCookingFrequency() != null) {
            score += calculateCookingCompatibility(lifestyle1.getCookingFrequency(), lifestyle2.getCookingFrequency()) * 0.2;
            factors++;
        }

        // Hobbies overlap
        if (lifestyle1.getHobbies() != null && lifestyle2.getHobbies() != null) {
            score += calculateHobbiesOverlap(lifestyle1.getHobbies(), lifestyle2.getHobbies()) * 0.3;
            factors++;
        }

        return factors > 0 ? score / factors : 0.5;
    }

    private double calculatePersonalityCompatibility(User user1, User user2) {
        UserCompatibilityProfile profile1 = user1.getCompatibilityProfile();
        UserCompatibilityProfile profile2 = user2.getCompatibilityProfile();

        if (profile1 == null || profile2 == null) return 0.5;

        // Use ML service for advanced personality matching
        return mlCompatibilityService.calculatePersonalityMatch(profile1, profile2);
    }

    private double calculatePreferencesCompatibility(User user1, User user2) {
        UserPreferences prefs1 = user1.getPreferences();
        UserPreferences prefs2 = user2.getPreferences();

        if (prefs1 == null || prefs2 == null) return 0.5;

        double score = 0.0;
        int factors = 0;

        // Smoking compatibility
        if (prefs1.getSmokingPreference() != null && prefs2.getSmokingPreference() != null) {
            score += calculatePreferenceMatch(prefs1.getSmokingPreference(), prefs2.getSmokingPreference()) * 0.25;
            factors++;
        }

        // Drinking compatibility
        if (prefs1.getDrinkingPreference() != null && prefs2.getDrinkingPreference() != null) {
            score += calculatePreferenceMatch(prefs1.getDrinkingPreference(), prefs2.getDrinkingPreference()) * 0.2;
            factors++;
        }

        // Cleanliness compatibility
        if (prefs1.getCleanlinessLevel() != null && prefs2.getCleanlinessLevel() != null) {
            score += calculateCleanlinessMatch(prefs1.getCleanlinessLevel(), prefs2.getCleanlinessLevel()) * 0.25;
            factors++;
        }

        // Noise level compatibility
        if (prefs1.getNoiseLevel() != null && prefs2.getNoiseLevel() != null) {
            score += calculateNoiseMatch(prefs1.getNoiseLevel(), prefs2.getNoiseLevel()) * 0.2;
            factors++;
        }

        // Pets compatibility
        score += (prefs1.isPetsAllowed() == prefs2.isPetsAllowed()) ? 0.1 : 0.0;
        factors++;

        return factors > 0 ? score / factors : 0.5;
    }

    private double calculateSocialCompatibility(User user1, User user2) {
        // Based on ratings, verification status, activity level
        double score = 0.0;

        // Rating compatibility (prefer users with good ratings)
        double avgRating = (user1.getAverageRating() + user2.getAverageRating()) / 2;
        score += (avgRating / 10.0) * 0.4;

        // Verification bonus
        if (user1.isVerified() && user2.isVerified()) {
            score += 0.3;
        } else if (user1.isVerified() || user2.isVerified()) {
            score += 0.15;
        }

        // Activity level (recent activity)
        if (user1.getLastActive() != null && user2.getLastActive() != null) {
            // Users active within last 7 days get bonus
            long daysAgo1 = java.time.temporal.ChronoUnit.DAYS.between(user1.getLastActive(), java.time.LocalDateTime.now());
            long daysAgo2 = java.time.temporal.ChronoUnit.DAYS.between(user2.getLastActive(), java.time.LocalDateTime.now());

            if (daysAgo1 <= 7 && daysAgo2 <= 7) {
                score += 0.3;
            } else if (daysAgo1 <= 30 && daysAgo2 <= 30) {
                score += 0.15;
            }
        }

        return Math.min(1.0, score);
    }

    // Helper methods for specific compatibility calculations
    private boolean violatesDealBreaker(User user, String dealBreaker) {
        // Implement deal breaker logic based on user attributes
        switch (dealBreaker.toLowerCase()) {
            case "smoking":
                return user.getLifestyle() != null &&
                        "regular".equals(user.getPreferences().getSmokingPreference());
            case "pets":
                return user.getPreferences() != null && user.getPreferences().isPetsAllowed();
            case "parties":
                return user.getLifestyle() != null &&
                        user.getLifestyle().getGuestsPerMonth() > 10;
            default:
                return false;
        }
    }

    private boolean isGenderCompatible(User user1, User user2) {
        String pref1 = user1.getPreferredGender();
        String pref2 = user2.getPreferredGender();

        if ("any".equals(pref1) || "any".equals(pref2)) return true;
        if (pref1 == null || pref2 == null) return true;

        // Assuming users have gender field or infer from preferences
        return pref1.equals(pref2);
    }

    private boolean isBudgetCompatible(User user1, User user2) {
        double budget1 = user1.getBudget();
        double budget2 = user2.getBudget();

        if (budget1 == 0 || budget2 == 0) return true;

        double flexibility1 = user1.getPreferences() != null ?
                user1.getPreferences().getBudgetFlexibility() : 0.2;
        double flexibility2 = user2.getPreferences() != null ?
                user2.getPreferences().getBudgetFlexibility() : 0.2;

        double maxFlexibility = Math.max(flexibility1, flexibility2);
        double budgetDiff = Math.abs(budget1 - budget2);
        double avgBudget = (budget1 + budget2) / 2;

        return (budgetDiff / avgBudget) <= maxFlexibility;
    }

    private double calculateSocialLevelCompatibility(String social1, String social2) {
        Map<String, Integer> socialLevels = Map.of(
                "introvert", 1,
                "ambivert", 2,
                "extrovert", 3
        );

        int level1 = socialLevels.getOrDefault(social1, 2);
        int level2 = socialLevels.getOrDefault(social2, 2);

        int diff = Math.abs(level1 - level2);
        return diff == 0 ? 1.0 : (diff == 1 ? 0.7 : 0.3);
    }

    private double calculateCookingCompatibility(String cooking1, String cooking2) {
        Map<String, Integer> cookingLevels = Map.of(
                "never", 1,
                "rarely", 2,
                "sometimes", 3,
                "often", 4,
                "daily", 5
        );

        int level1 = cookingLevels.getOrDefault(cooking1, 3);
        int level2 = cookingLevels.getOrDefault(cooking2, 3);

        int diff = Math.abs(level1 - level2);
        return Math.max(0.3, 1.0 - (diff * 0.2));
    }

    private double calculateHobbiesOverlap(List<String> hobbies1, List<String> hobbies2) {
        Set<String> set1 = new HashSet<>(hobbies1);
        Set<String> set2 = new HashSet<>(hobbies2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) return 0.5;

        double jaccardIndex = (double) intersection.size() / union.size();
        return Math.min(1.0, jaccardIndex * 2); // Boost the score
    }

    private double calculatePreferenceMatch(String pref1, String pref2) {
        if ("any".equals(pref1) || "any".equals(pref2)) return 0.8;
        return pref1.equals(pref2) ? 1.0 : 0.2;
    }

    private double calculateCleanlinessMatch(String clean1, String clean2) {
        Map<String, Integer> cleanLevels = Map.of(
                "flexible", 1,
                "average", 2,
                "clean", 3,
                "very-clean", 4
        );

        int level1 = cleanLevels.getOrDefault(clean1, 2);
        int level2 = cleanLevels.getOrDefault(clean2, 2);

        int diff = Math.abs(level1 - level2);
        return diff <= 1 ? 1.0 : (diff == 2 ? 0.6 : 0.2);
    }

    private double calculateNoiseMatch(String noise1, String noise2) {
        Map<String, Integer> noiseLevels = Map.of(
                "quiet", 1,
                "moderate", 2,
                "lively", 3,
                "any", 2
        );

        int level1 = noiseLevels.getOrDefault(noise1, 2);
        int level2 = noiseLevels.getOrDefault(noise2, 2);

        if ("any".equals(noise1) || "any".equals(noise2)) return 0.8;

        int diff = Math.abs(level1 - level2);
        return diff == 0 ? 1.0 : (diff == 1 ? 0.7 : 0.3);
    }

    private String generateMatchReason(User user1, User user2, Map<String, Double> categoryScores) {
        StringBuilder reason = new StringBuilder();

        // Find top compatibility factors
        List<Map.Entry<String, Double>> sortedScores = categoryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(3, sortedScores.size()); i++) {
            Map.Entry<String, Double> entry = sortedScores.get(i);
            if (entry.getValue() >= 0.7) {
                switch (entry.getKey()) {
                    case "budget":
                        reason.append("Similar budget preferences. ");
                        break;
                    case "lifestyle":
                        reason.append("Compatible lifestyle and schedules. ");
                        break;
                    case "location":
                        reason.append("Preferred location match. ");
                        break;
                    case "personality":
                        reason.append("Complementary personalities. ");
                        break;
                    case "preferences":
                        reason.append("Aligned living preferences. ");
                        break;
                }
            }
        }

        if (reason.length() == 0) {
            reason.append("Good overall compatibility based on multiple factors.");
        }

        return reason.toString().trim();
    }

    // Additional Service Methods

    public void favoriteMatch(String matchId) {
        matchResultRepository.findById(matchId).ifPresent(match -> {
            match.setFavorited(true);
            matchResultRepository.save(match);
        });
    }

    public List<MatchResult> getMatchHistory(String userId) {
        return matchResultRepository.findByUserIdOrderByOverallScoreDesc(userId);
    }

    public Map<String, Object> getCompatibilityInsights(String userId1, String userId2) {
        Optional<User> user1Opt = userRepository.findById(userId1);
        Optional<User> user2Opt = userRepository.findById(userId2);

        if (!user1Opt.isPresent() || !user2Opt.isPresent()) {
            throw new RuntimeException("Users not found");
        }

        User user1 = user1Opt.get();
        User user2 = user2Opt.get();

        Map<String, Object> insights = new HashMap<>();
        Map<String, Double> categoryScores = calculateCategoryScores(user1, user2);

        insights.put("categoryScores", categoryScores);
        insights.put("overallScore", calculateOverallCompatibility(user1, user2));
        insights.put("strengths", getCompatibilityStrengths(categoryScores));
        insights.put("concerns", getCompatibilityConcerns(categoryScores));
        insights.put("recommendations", getImprovementRecommendations(user1, user2));

        return insights;
    }

    private List<String> getCompatibilityStrengths(Map<String, Double> categoryScores) {
        return categoryScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.8)
                .map(entry -> capitalize(entry.getKey()) + " compatibility")
                .collect(java.util.stream.Collectors.toList());
    }

    private List<String> getCompatibilityConcerns(Map<String, Double> categoryScores) {
        return categoryScores.entrySet().stream()
                .filter(entry -> entry.getValue() < 0.5)
                .map(entry -> "Low " + entry.getKey() + " compatibility")
                .collect(java.util.stream.Collectors.toList());
    }

    private List<String> getImprovementRecommendations(User user1, User user2) {
        List<String> recommendations = new ArrayList<>();

        // Budget recommendations
        if (Math.abs(user1.getBudget() - user2.getBudget()) > user1.getBudget() * 0.2) {
            recommendations.add("Consider discussing budget flexibility and shared expenses");
        }

        // Lifestyle recommendations
        if (user1.getLifestyle() != null && user2.getLifestyle() != null) {
            if (!user1.getLifestyle().getSocialLevel().equals(user2.getLifestyle().getSocialLevel())) {
                recommendations.add("Discuss social preferences and guest policies early");
            }
        }

        return recommendations;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}