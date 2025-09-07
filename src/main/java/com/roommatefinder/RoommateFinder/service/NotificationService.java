package com.roommatefinder.RoommateFinder.service;

import com.roommatefinder.RoommateFinder.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendMatchNotifications(User user, List<MatchResult> highQualityMatches) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("New High-Quality Roommate Matches Found!");
            message.setText(buildMatchNotificationText(user, highQualityMatches));

            mailSender.send(message);
            logger.info("Match notification sent to user: {}", user.getUsername());

        } catch (Exception e) {
            logger.error("Failed to send match notification to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private String buildMatchNotificationText(User user, List<MatchResult> matches) {
        StringBuilder text = new StringBuilder();
        text.append("Hi ").append(user.getUsername()).append(",\n\n");
        text.append("We found ").append(matches.size()).append(" high-quality roommate matches for you!\n\n");

        for (MatchResult match : matches) {
            text.append("• Match with ").append(Math.round(match.getOverallScore() * 100))
                    .append("% compatibility\n");
            text.append("  Reason: ").append(match.getMatchReason()).append("\n\n");
        }

        text.append("Log in to view full profiles and start conversations.\n\n");
        text.append("Happy roommate hunting!\n");
        text.append("The RoommateFinder Team");

        return text.toString();
    }

    public void sendMatchRequestNotification(String recipientEmail, String senderName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("New Roommate Match Request");
            message.setText(String.format(
                    "Hi,\n\n%s is interested in being your roommate! " +
                            "Check out their profile and respond to their request.\n\n" +
                            "Log in to RoommateFinder to view the details.\n\n" +
                            "Best regards,\nRoommateFinder Team", senderName));

            mailSender.send(message);

        } catch (Exception e) {
            logger.error("Failed to send match request notification: {}", e.getMessage());
        }
    }
}