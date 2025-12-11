package com.pandora.holograms.util;

import org.bukkit.entity.Player;

/**
 * Utility class for handling PlaceholderAPI integration
 */
public class PlaceholderUtil {
    
    private static boolean placeholderApiEnabled = false;
    
    /**
     * Check if PlaceholderAPI is available
     */
    public static void checkPlaceholderAPI() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            placeholderApiEnabled = true;
        } catch (ClassNotFoundException e) {
            placeholderApiEnabled = false;
        }
    }
    
    /**
     * Check if PlaceholderAPI is enabled
     */
    public static boolean isPlaceholderApiEnabled() {
        return placeholderApiEnabled;
    }
    
    /**
     * Parse placeholders in a string for a player
     * 
     * @param text The text containing placeholders
     * @param player The player to parse placeholders for
     * @return The text with placeholders parsed
     */
    public static String parsePlaceholders(String text, Player player) {
        if (!placeholderApiEnabled || text == null || player == null) {
            return text;
        }
        
        try {
            // Use reflection to avoid compile-time dependency
            Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            java.lang.reflect.Method setPlaceholders = placeholderAPI.getMethod("setPlaceholders", Player.class, String.class);
            return (String) setPlaceholders.invoke(null, player, text);
        } catch (Exception e) {
            // If PlaceholderAPI fails, return original text
            return text;
        }
    }
    
    /**
     * Check if a string contains placeholders
     * 
     * @param text The text to check
     * @return True if the text contains placeholders
     */
    public static boolean containsPlaceholders(String text) {
        if (!placeholderApiEnabled || text == null) {
            return false;
        }
        
        return text.contains("%");
    }

}

