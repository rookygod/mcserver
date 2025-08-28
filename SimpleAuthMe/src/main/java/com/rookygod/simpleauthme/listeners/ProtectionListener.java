package com.rookygod.simpleauthme.listeners;

import com.rookygod.simpleauthme.SimpleAuthMe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Listener for protecting unauthenticated players
 */
public class ProtectionListener implements Listener {
    private final SimpleAuthMe plugin;
    private final List<String> allowedCommands = Arrays.asList("/login", "/register", "/l", "/reg");

    /**
     * Constructor for ProtectionListener
     *
     * @param plugin The SimpleAuthMe plugin instance
     */
    public ProtectionListener(SimpleAuthMe plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Allow small head movements but prevent actual movement
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId()) && 
            (event.getFrom().getBlockX() != event.getTo().getBlockX() || 
             event.getFrom().getBlockY() != event.getTo().getBlockY() || 
             event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
            
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (!plugin.getSessionManager().isAuthenticated(player.getUniqueId()) && !isAllowedCommand(command)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }
    
    /**
     * Check if a command is allowed for unauthenticated players
     *
     * @param command The command to check
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedCommand(String command) {
        return allowedCommands.stream().anyMatch(command::startsWith);
    }
}

