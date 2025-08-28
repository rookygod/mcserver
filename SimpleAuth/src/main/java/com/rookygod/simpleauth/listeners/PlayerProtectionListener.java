package com.rookygod.simpleauth.listeners;

import com.rookygod.simpleauth.SimpleAuth;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.List;

public class PlayerProtectionListener implements Listener {
    private final SimpleAuth plugin;
    private final List<String> allowedCommands = Arrays.asList("/login", "/register");

    public PlayerProtectionListener(SimpleAuth plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Allow small head movements but prevent actual movement
        if (!plugin.isAuthenticated(player.getUniqueId()) && 
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
        
        if (!plugin.isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (!plugin.isAuthenticated(player.getUniqueId()) && !isAllowedCommand(command)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must login first!");
        }
    }
    
    private boolean isAllowedCommand(String command) {
        return allowedCommands.stream().anyMatch(command::startsWith);
    }
}

