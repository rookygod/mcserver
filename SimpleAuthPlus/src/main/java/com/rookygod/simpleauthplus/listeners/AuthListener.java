package com.rookygod.simpleauthplus.listeners;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import com.rookygod.simpleauthplus.utils.RandomPasswordGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for player authentication events
 */
public class AuthListener implements Listener {
    private final SimpleAuthPlus plugin;
    private final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    /**
     * Constructor for AuthListener
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public AuthListener(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        
        // Add player to limbo
        plugin.getLimboManager().addPlayerToLimbo(player);
        
        // Check if player has a valid session
        if (plugin.getSessionManager().hasSession(player)) {
            // Auto-login
            plugin.getLimboManager().removePlayerFromLimbo(player);
            player.sendMessage(plugin.getConfigManager().getMessage("login.auto-login"));
            return;
        }
        
        // Check if player is registered
        if (plugin.getUserDataManager().isPlayerRegistered(playerName)) {
            // Send login message
            player.sendMessage(plugin.getConfigManager().getMessage("join.login-required"));
        } else {
            // Check if auto-registration is enabled
            if (plugin.getConfigManager().isAutoRegisterEnabled()) {
                // Generate random password
                String password = RandomPasswordGenerator.generatePassword(10);
                
                // Register player
                if (plugin.getUserDataManager().registerPlayer(player, password)) {
                    // Create session for the player
                    plugin.getSessionManager().createSession(player);
                    
                    // Remove player from limbo
                    plugin.getLimboManager().removePlayerFromLimbo(player);
                    
                    // Send auto-registration message
                    player.sendMessage(plugin.getConfigManager().getMessage("registration.auto-registered")
                            .replace("{password}", password));
                }
            } else {
                // Send registration message
                player.sendMessage(plugin.getConfigManager().getMessage("join.register-required"));
            }
        }
        
        // Start timeout task
        int timeout = plugin.getConfigManager().getLoginTimeout();
        if (timeout > 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("join.timeout-warning")
                    .replace("{timeout}", String.valueOf(timeout)));
            
            BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // Check if player is still online and not authenticated
                if (player.isOnline() && !plugin.getSessionManager().hasSession(player)) {
                    player.kickPlayer(plugin.getConfigManager().getMessageNoPrefix("login.timeout"));
                }
                
                timeoutTasks.remove(player.getUniqueId());
            }, timeout * 20L);
            
            timeoutTasks.put(player.getUniqueId(), task);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Cancel timeout task
        if (timeoutTasks.containsKey(uuid)) {
            timeoutTasks.get(uuid).cancel();
            timeoutTasks.remove(uuid);
        }
        
        // Remove player from limbo
        plugin.getLimboManager().removePlayerFromLimbo(player);
    }
}

