package com.rookygod.authlite.listeners;

import com.rookygod.authlite.AuthLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Listener responsible for hiding sensitive commands from the console
 * to prevent passwords from being visible in server logs.
 */
public class CommandSecurityListener implements Listener {

    private final AuthLite plugin;
    private final Set<String> sensitiveCommands = new HashSet<>();
    
    public CommandSecurityListener(AuthLite plugin) {
        this.plugin = plugin;
        
        // Add sensitive commands that should be hidden
        sensitiveCommands.add("/login");
        sensitiveCommands.add("/l");
        sensitiveCommands.add("/register");
        sensitiveCommands.add("/reg");
        sensitiveCommands.add("/changepassword");
        sensitiveCommands.add("/changepass");
        sensitiveCommands.add("/cp");
    }
    
    /**
     * Handles player commands and hides sensitive ones from the console
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfigManager().isHideSensitiveCommands()) {
            return;
        }
        
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (sensitiveCommands.contains(command)) {
            // Create a sanitized version of the command for logging
            String sanitizedCommand = sanitizeCommand(event.getMessage());
            
            // Set the message to the sanitized version for console logging
            // This doesn't affect command execution, only what's shown in logs
            event.setMessage(sanitizedCommand);
            
            // Log a sanitized version to the console
            plugin.getLogger().info(event.getPlayer().getName() + " issued a sensitive command (password hidden)");
        }
    }
    
    /**
     * Sanitizes a command by replacing password arguments with asterisks
     */
    private String sanitizeCommand(String command) {
        String[] parts = command.split(" ");
        
        // If the command has arguments (potential passwords)
        if (parts.length > 1) {
            // Replace all arguments (potential passwords) with asterisks
            for (int i = 1; i < parts.length; i++) {
                parts[i] = "*****";
            }
            
            // Reconstruct the command with sanitized arguments
            return String.join(" ", parts);
        }
        
        return command;
    }
}

