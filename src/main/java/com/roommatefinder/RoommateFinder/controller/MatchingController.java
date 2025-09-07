package com.roommatefinder.RoommateFinder.controller;

import com.roommatefinder.RoommateFinder.model.MatchResult;
import com.roommatefinder.RoommateFinder.model.User;
import com.roommatefinder.RoommateFinder.service.SmartMatchingService;
import com.roommatefinder.RoommateFinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    @Autowired
    private SmartMatchingService matchingService;

    @Autowired
    private UserService userService;

    @GetMapping("/compatible-roommates")
    public ResponseEntity<List<MatchResult>> getCompatibleRoommates(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0.6") double minScore) {

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MatchResult> matches = matchingService.findCompatibleRoommates(currentUser.getId(), limit);

        // Filter by minimum score
        matches = matches.stream()
                .filter(match -> match.getOverallScore() >= minScore)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matches);
    }

    @PostMapping("/update-preferences")
    public ResponseEntity<Map<String, String>> updateUserPreferences(
            Authentication authentication,
            @RequestBody User updatedUser) {

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user preferences and lifestyle
        currentUser.setPreferences(updatedUser.getPreferences());
        currentUser.setLifestyle(updatedUser.getLifestyle());
        currentUser.setCompatibilityProfile(updatedUser.getCompatibilityProfile());
        currentUser.setInterests(updatedUser.getInterests());

        userService.updateUser(username, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Preferences updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rate-match/{matchId}")
    public ResponseEntity<Map<String, String>> rateMatch(
            Authentication authentication,
            @PathVariable String matchId,
            @RequestBody Map<String, Object> ratingData) {

        // Implementation for rating matches to improve ML algorithm
        // This feedback will be used to train the ML model

        Map<String, String> response = new HashMap<>();
        response.put("message", "Match rated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorite-match/{matchId}")
    public ResponseEntity<Map<String, String>> favoriteMatch(
            Authentication authentication,
            @PathVariable String matchId) {

        // Mark match as favorited
        matchingService.favoriteMatch(matchId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Match added to favorites");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/match-history")
    public ResponseEntity<List<MatchResult>> getMatchHistory(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MatchResult> history = matchingService.getMatchHistory(currentUser.getId());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/compatibility-insights/{userId}")
    public ResponseEntity<Map<String, Object>> getCompatibilityInsights(
            Authentication authentication,
            @PathVariable String userId) {

        String currentUsername = authentication.getName();
        User currentUser = userService.findUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> insights = matchingService.getCompatibilityInsights(currentUser.getId(), userId);
        return ResponseEntity.ok(insights);
    }
}