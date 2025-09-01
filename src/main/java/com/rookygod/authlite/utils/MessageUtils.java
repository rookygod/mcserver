package com.rookygod.authlite.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    /**
     * Colorize a message using Bukkit color codes.
     *
     * @param message The message to colorize
     * @return The colorized message
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Replace placeholders in a message.
     *
     * @param message The message with placeholders
     * @param placeholders The placeholders to replace (key: placeholder, value: replacement)
     * @return The message with replaced placeholders
     */
    public static String replacePlaceholders(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs (placeholder, replacement)");
        }
        
        String result = message;
        
        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i];
            String replacement = placeholders[i + 1];
            
            result = result.replace("{" + placeholder + "}", replacement);
        }
        
        return result;
    }
}

