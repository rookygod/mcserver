package com.rookygod.simpleauth.listeners;

import com.rookygod.simpleauth.SimpleAuth;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerAuthListener implements Listener {
    private final SimpleAuth plugin;

    public PlayerAuthListener(SimpleAuth plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Ensure player is not authenticated when joining
        plugin.deauthenticatePlayer(player.getUniqueId());
        
        // Check if player is registered
        if (plugin.isPlayerRegistered(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Please login using /login <password>");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Please register using /register <password>");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove player from authenticated players when they quit
        plugin.deauthenticatePlayer(player.getUniqueId());
    }
}

