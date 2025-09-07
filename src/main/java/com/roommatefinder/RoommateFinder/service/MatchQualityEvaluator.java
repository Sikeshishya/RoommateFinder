package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchQualityEvaluator {

    public MatchQualityReport evaluateMatchQuality(MatchResult match, User user1, User user2) {
        MatchQualityReport report = new MatchQualityReport();

        report.setOverallScore(match.getOverallScore());
        report.setCompatibilityBreakdown(analyzeCompatibilityFactors(match.getCategoryScores()));
        report.setRiskFactors(identifyRiskFactors(user1, user2));
        report.setSuccessPredictors(identifySuccessPredictors(user1, user2));
        report.setRecommendations(generateQualityRecommendations(user1, user2, match.getCategoryScores()));

        return report;
    }

    private Map<String, String> analyzeCompatibilityFactors(Map<String, Double> categoryScores) {
        Map<String, String> analysis = new HashMap<>();

        categoryScores.forEach((category, score) -> {
            String assessment;
            if (score >= 0.8) {
                assessment = "Excellent match - very compatible";
            } else if (score >= 0.6) {
                assessment = "Good match - compatible with minor differences";
            } else if (score >= 0.4) {
                assessment = "Fair match - some compatibility concerns";
            } else {
                assessment = "Poor match - significant incompatibilities";
            }
            analysis.put(category, assessment);
        });

        return analysis;
    }

    private List<String> identifyRiskFactors(User user1, User user2) {
        List<String> risks = new ArrayList<>();

        // Budget risk
        if (Math.abs(user1.getBudget() - user2.getBudget()) > user1.getBudget() * 0.3) {
            risks.add("Significant budget difference may cause conflicts");
        }

        // Lifestyle risks
        if (user1.getLifestyle() != null && user2.getLifestyle() != null) {
            UserLifestyle lifestyle1 = user1.getLifestyle();
            UserLifestyle lifestyle2 = user2.getLifestyle();

            // Sleep schedule conflict
            if (lifestyle1.getUsualSleepTime() != null && lifestyle2.getUsualSleepTime() != null) {
                long hourDiff = Math.abs(lifestyle1.getUsualSleepTime().getHour() -
                        lifestyle2.getUsualSleepTime().getHour());
                if (hourDiff > 4) {
                    risks.add("Very different sleep schedules may cause disturbances");
                }
            }

            // Social level mismatch
            if ("introvert".equals(lifestyle1.getSocialLevel()) &&
                    "extrovert".equals(lifestyle2.getSocialLevel())) {
                risks.add("Opposite social preferences may lead to conflicts");
            }
        }

        // Experience risks
        if (user1.getTotalRatings() == 0 || user2.getTotalRatings() == 0) {
            risks.add("Limited roommate history - compatibility uncertain");
        }

        return risks;
    }

    private List<String> identifySuccessPredictors(User user1, User user2) {
        List<String> predictors = new ArrayList<>();

        // High ratings
        if (user1.getAverageRating() >= 8.0 && user2.getAverageRating() >= 8.0) {
            predictors.add("Both users have excellent roommate ratings");
        }

        // Verification status
        if (user1.isVerified() && user2.isVerified()) {
            predictors.add("Both users are verified - higher trustworthiness");
        }

        // Active users
        if (user1.getLastActive() != null && user2.getLastActive() != null &&
                user1.getLastActive().isAfter(java.time.LocalDateTime.now().minusDays(7)) &&
                user2.getLastActive().isAfter(java.time.LocalDateTime.now().minusDays(7))) {
            predictors.add("Both users are actively looking - higher response rate");
        }

        // Shared interests
        if (user1.getInterests() != null && user2.getInterests() != null) {
            Set<String> commonInterests = new HashSet<>(user1.getInterests());
            commonInterests.retainAll(user2.getInterests());
            if (commonInterests.size() >= 3) {
                predictors.add("Multiple shared interests - good foundation for friendship");
            }
        }

        return predictors;
    }

    private List<String> generateQualityRecommendations(User user1, User user2, Map<String, Double> categoryScores) {
        List<String> recommendations = new ArrayList<>();

        // Low budget compatibility
        if (categoryScores.getOrDefault("budget", 0.0) < 0.5) {
            recommendations.add("Discuss shared expenses and budget flexibility early");
        }

        // Low lifestyle compatibility
        if (categoryScores.getOrDefault("lifestyle", 0.0) < 0.6) {
            recommendations.add("Have detailed discussions about daily routines and lifestyle preferences");
        }

        // Low preferences compatibility
        if (categoryScores.getOrDefault("preferences", 0.0) < 0.6) {
            recommendations.add("Clarify house rules and living preferences before moving in");
        }

        // General recommendations
        recommendations.add("Consider a trial period or temporary arrangement first");
        recommendations.add("Establish clear communication channels and conflict resolution methods");

        return recommendations;
    }

    // Inner class for match quality report
    public static class MatchQualityReport {
        private double overallScore;
        private Map<String, String> compatibilityBreakdown;
        private List<String> riskFactors;
        private List<String> successPredictors;
        private List<String> recommendations;

        // Getters and setters
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

        public Map<String, String> getCompatibilityBreakdown() { return compatibilityBreakdown; }
        public void setCompatibilityBreakdown(Map<String, String> compatibilityBreakdown) {
            this.compatibilityBreakdown = compatibilityBreakdown;
        }

        public List<String> getRiskFactors() { return riskFactors; }
        public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }

        public List<String> getSuccessPredictors() { return successPredictors; }
        public void setSuccessPredictors(List<String> successPredictors) { this.successPredictors = successPredictors; }

        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
}