package com.example.scanshield_mobile_app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpamDetector {
    // Common spam keywords (can be expanded based on requirements)
    private static final Set<String> SPAM_KEYWORDS = new HashSet<>(Arrays.asList(
            "win", "free", "urgent", "prize", "lottery", "claim", "winner", "money",
            "offer", "discount", "deal", "click", "subscribe", "unsubscribe", "limited",
            "exclusive", "alert", "warning", "scam", "verify", "account", "password"
    ));

    public static boolean isSpam(String message) {
        if (message == null) return false;
        String messageLower = message.toLowerCase();
        // Check if the message contains any spam keywords
        for (String keyword : SPAM_KEYWORDS) {
            if (messageLower.contains(keyword)) {
                return true;
            }
        }
        // Additional heuristic: Check for suspicious links (simplified)
        if (messageLower.contains("http://") || messageLower.contains("https://")) {
            return true;
        }
        return false;
    }
}