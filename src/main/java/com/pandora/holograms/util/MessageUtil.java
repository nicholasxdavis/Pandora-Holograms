package com.pandora.holograms.util;

import org.bukkit.ChatColor;

/**
 * Utility class for formatting and coloring messages.
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class MessageUtil {
    
    public static final String PREFIX = "&e&lHolograms &8» &r";
    public static final String HEADER = "&e&l";
    public static final String VALUE = "&6";
    public static final String TEXT = "&7";
    public static final String SPECIAL = "&e";
    public static final String ERROR = "&c";
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String format(String message) {
        return colorize(PREFIX + message);
    }
    
    public static String formatError(String message) {
        return format(ERROR + message);
    }
    
    public static String formatSuccess(String message) {
        return format(SPECIAL + message);
    }
    
    public static String formatValue(String value) {
        return colorize(VALUE + value);
    }
    
    public static String formatText(String text) {
        return colorize(TEXT + text);
    }
    
    public static String formatSpecial(String text) {
        return colorize(SPECIAL + text);
    }

}



