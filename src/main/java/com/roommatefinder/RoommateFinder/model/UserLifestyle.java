package com.roommatefinder.RoommateFinder.model;

import java.time.LocalTime;
import java.util.List;

public class UserLifestyle {
    private LocalTime usualSleepTime = LocalTime.of(22, 0);
    private LocalTime usualWakeTime = LocalTime.of(7, 0);
    private String socialLevel; // "introvert", "ambivert", "extrovert"
    private int guestsPerMonth = 2;
    private List<String> hobbies;
    private String cookingFrequency; // "never", "rarely", "sometimes", "often", "daily"
    private boolean hasVehicle = false;
    private String exerciseRoutine; // "none", "light", "moderate", "intense"
    private List<String> musicGenres;
    private String studyWorkStyle; // "quiet", "background-music", "collaborative"

    // Constructors, getters, and setters
    public UserLifestyle() {}

    public LocalTime getUsualSleepTime() { return usualSleepTime; }
    public void setUsualSleepTime(LocalTime usualSleepTime) { this.usualSleepTime = usualSleepTime; }

    public LocalTime getUsualWakeTime() { return usualWakeTime; }
    public void setUsualWakeTime(LocalTime usualWakeTime) { this.usualWakeTime = usualWakeTime; }

    public String getSocialLevel() { return socialLevel; }
    public void setSocialLevel(String socialLevel) { this.socialLevel = socialLevel; }

    public int getGuestsPerMonth() { return guestsPerMonth; }
    public void setGuestsPerMonth(int guestsPerMonth) { this.guestsPerMonth = guestsPerMonth; }

    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }

    public String getCookingFrequency() { return cookingFrequency; }
    public void setCookingFrequency(String cookingFrequency) { this.cookingFrequency = cookingFrequency; }

    public boolean isHasVehicle() { return hasVehicle; }
    public void setHasVehicle(boolean hasVehicle) { this.hasVehicle = hasVehicle; }

    public String getExerciseRoutine() { return exerciseRoutine; }
    public void setExerciseRoutine(String exerciseRoutine) { this.exerciseRoutine = exerciseRoutine; }

    public List<String> getMusicGenres() { return musicGenres; }
    public void setMusicGenres(List<String> musicGenres) { this.musicGenres = musicGenres; }

    public String getStudyWorkStyle() { return studyWorkStyle; }
    public void setStudyWorkStyle(String studyWorkStyle) { this.studyWorkStyle = studyWorkStyle; }
}