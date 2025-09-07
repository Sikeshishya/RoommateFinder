package com.roommatefinder.RoommateFinder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "match_results")
public class MatchResult {
    @Id
    private String id;
    private String userId;
    private String potentialRoommateId;
    private String propertyId; // If matching for a specific property
    private double overallScore;
    private Map<String, Double> categoryScores;
    private String matchReason;
    private LocalDateTime calculatedAt;
    private boolean isViewed = false;
    private boolean isFavorited = false;
    private String status; // "pending", "accepted", "rejected", "expired"

    // Constructors, getters, and setters
    public MatchResult() {}

    public MatchResult(String userId, String potentialRoommateId, double overallScore) {
        this.userId = userId;
        this.potentialRoommateId = potentialRoommateId;
        this.overallScore = overallScore;
        this.calculatedAt = LocalDateTime.now();
        this.status = "pending";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPotentialRoommateId() { return potentialRoommateId; }
    public void setPotentialRoommateId(String potentialRoommateId) {
        this.potentialRoommateId = potentialRoommateId;
    }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

    public Map<String, Double> getCategoryScores() { return categoryScores; }
    public void setCategoryScores(Map<String, Double> categoryScores) { this.categoryScores = categoryScores; }

    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    public boolean isViewed() { return isViewed; }
    public void setViewed(boolean viewed) { isViewed = viewed; }

    public boolean isFavorited() { return isFavorited; }
    public void setFavorited(boolean favorited) { isFavorited = favorited; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}