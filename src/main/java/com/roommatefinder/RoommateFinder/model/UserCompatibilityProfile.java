package com.roommatefinder.RoommateFinder.model;

import java.util.Map;

public class UserCompatibilityProfile {
    private Map<String, Double> personalityTraits; // Big Five traits
    private double communicationScore = 5.0; // 1-10 scale
    private double adaptabilityScore = 5.0;
    private double responsibilityScore = 5.0;
    private double conflictResolutionScore = 5.0;
    private Map<String, Integer> lifestyleCompatibility; // Generated scores

    // Constructors, getters, and setters
    public UserCompatibilityProfile() {}

    public Map<String, Double> getPersonalityTraits() { return personalityTraits; }
    public void setPersonalityTraits(Map<String, Double> personalityTraits) {
        this.personalityTraits = personalityTraits;
    }

    public double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(double communicationScore) { this.communicationScore = communicationScore; }

    public double getAdaptabilityScore() { return adaptabilityScore; }
    public void setAdaptabilityScore(double adaptabilityScore) { this.adaptabilityScore = adaptabilityScore; }

    public double getResponsibilityScore() { return responsibilityScore; }
    public void setResponsibilityScore(double responsibilityScore) { this.responsibilityScore = responsibilityScore; }

    public double getConflictResolutionScore() { return conflictResolutionScore; }
    public void setConflictResolutionScore(double conflictResolutionScore) {
        this.conflictResolutionScore = conflictResolutionScore;
    }

    public Map<String, Integer> getLifestyleCompatibility() { return lifestyleCompatibility; }
    public void setLifestyleCompatibility(Map<String, Integer> lifestyleCompatibility) {
        this.lifestyleCompatibility = lifestyleCompatibility;
    }
}