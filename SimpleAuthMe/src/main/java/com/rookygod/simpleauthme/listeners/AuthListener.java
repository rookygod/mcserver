package com.rookygod.simpleauthme.listeners;

import com.rookygod.simpleauthme.SimpleAuthMe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for player authentication events
 */
public class AuthListener implements Listener {
    private final SimpleAuthMe plugin;

    /**
     * Constructor for AuthListener
     *
     * @param plugin The SimpleAuthMe plugin instance
     */
    public AuthListener(SimpleAuthMe plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Ensure player is not authenticated when joining
        plugin.getSessionManager().deauthenticatePlayer(player.getUniqueId());
        
        // Check if player is registered
        if (plugin.getUserDataManager().isPlayerRegistered(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Please login using /login <password>");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Please register using /register <password>");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove player from authenticated players when they quit
        plugin.getSessionManager().deauthenticatePlayer(player.getUniqueId());
    }
}

