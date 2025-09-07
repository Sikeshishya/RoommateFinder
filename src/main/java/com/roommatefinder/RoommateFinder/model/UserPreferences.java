package com.roommatefinder.RoommateFinder.model;

import java.util.List;

public class UserPreferences {
    private int ageRangeMin = 18;
    private int ageRangeMax = 65;
    private String smokingPreference; // "non-smoker", "occasional", "regular", "any"
    private String drinkingPreference; // "non-drinker", "social", "regular", "any"
    private String cleanlinessLevel; // "very-clean", "clean", "average", "flexible"
    private String noiseLevel; // "quiet", "moderate", "lively", "any"
    private boolean petsAllowed = false;
    private boolean guestsAllowed = true;
    private String workSchedule; // "day-shift", "night-shift", "flexible", "work-from-home"
    private List<String> dealBreakers; // List of absolute no-gos
    private double budgetFlexibility = 0.1; // 10% flexibility
    private int maxCommuteMins = 60;

    // Constructors, getters, and setters
    public UserPreferences() {}

    public int getAgeRangeMin() { return ageRangeMin; }
    public void setAgeRangeMin(int ageRangeMin) { this.ageRangeMin = ageRangeMin; }

    public int getAgeRangeMax() { return ageRangeMax; }
    public void setAgeRangeMax(int ageRangeMax) { this.ageRangeMax = ageRangeMax; }

    public String getSmokingPreference() { return smokingPreference; }
    public void setSmokingPreference(String smokingPreference) { this.smokingPreference = smokingPreference; }

    public String getDrinkingPreference() { return drinkingPreference; }
    public void setDrinkingPreference(String drinkingPreference) { this.drinkingPreference = drinkingPreference; }

    public String getCleanlinessLevel() { return cleanlinessLevel; }
    public void setCleanlinessLevel(String cleanlinessLevel) { this.cleanlinessLevel = cleanlinessLevel; }

    public String getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(String noiseLevel) { this.noiseLevel = noiseLevel; }

    public boolean isPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(boolean petsAllowed) { this.petsAllowed = petsAllowed; }

    public boolean isGuestsAllowed() { return guestsAllowed; }
    public void setGuestsAllowed(boolean guestsAllowed) { this.guestsAllowed = guestsAllowed; }

    public String getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(String workSchedule) { this.workSchedule = workSchedule; }

    public List<String> getDealBreakers() { return dealBreakers; }
    public void setDealBreakers(List<String> dealBreakers) { this.dealBreakers = dealBreakers; }

    public double getBudgetFlexibility() { return budgetFlexibility; }
    public void setBudgetFlexibility(double budgetFlexibility) { this.budgetFlexibility = budgetFlexibility; }

    public int getMaxCommuteMins() { return maxCommuteMins; }
    public void setMaxCommuteMins(int maxCommuteMins) { this.maxCommuteMins = maxCommuteMins; }
}