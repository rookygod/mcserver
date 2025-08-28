package com.rookygod.authlite.listeners;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.events.LoginEvent;
import com.rookygod.authlite.events.LogoutEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProtectionListener implements Listener {

    private final AuthLite plugin;
    private final Map<UUID, BukkitTask> loginTimeoutTasks = new HashMap<>();
    
    public PlayerProtectionListener(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player has a valid session
        if (plugin.getSessionManager().hasValidSession(player)) {
            // Resume session
            plugin.getSessionManager().authenticatePlayer(player);
            plugin.getMessageManager().sendMessage(player, "session.resumed");
            return;
        }
        
        // Apply protection
        plugin.getProtectionManager().applyProtection(player);
        
        // Start login timeout task
        if (plugin.getConfigManager().getLoginTimeout() > 0) {
            BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // Check if player is still online and not authenticated
                if (player.isOnline() && !plugin.getSessionManager().isAuthenticated(player)) {
                    if (plugin.getConfigManager().isKickOnTimeout()) {
                        player.kickPlayer(plugin.getMessageManager().getMessage("login.timeout"));
                    } else {
                        plugin.getMessageManager().sendMessage(player, "login.timeout");
                    }
                }
                
                loginTimeoutTasks.remove(player.getUniqueId());
            }, plugin.getConfigManager().getLoginTimeout() * 20L); // Convert seconds to ticks
            
            loginTimeoutTasks.put(player.getUniqueId(), task);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Cancel login timeout task
        if (loginTimeoutTasks.containsKey(uuid)) {
            loginTimeoutTasks.get(uuid).cancel();
            loginTimeoutTasks.remove(uuid);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only block movement if the player changes block position
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        if (plugin.getProtectionManager().shouldBlockMovement(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getProtectionManager().shouldBlockChat(event.getPlayer())) {
            event.setCancelled(true);
            plugin.getProtectionManager().sendAuthenticationMessage(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (plugin.getProtectionManager().shouldBlockCommand(event.getPlayer(), command)) {
            event.setCancelled(true);
            // Send command blocked message and then the appropriate authentication message
            plugin.getMessageManager().sendMessage(event.getPlayer(), "protection.command_blocked");
            plugin.getProtectionManager().sendAuthenticationMessage(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getProtectionManager().shouldBlockInteraction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (plugin.getProtectionManager().shouldBlockInteraction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getProtectionManager().shouldBlockInteraction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getProtectionManager().shouldBlockInteraction(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getProtectionManager().shouldBlockDamage(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (plugin.getProtectionManager().shouldBlockDamage(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (plugin.getProtectionManager().shouldBlockInventory(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (plugin.getProtectionManager().shouldBlockInventory(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Cancel login timeout task
        if (loginTimeoutTasks.containsKey(uuid)) {
            loginTimeoutTasks.get(uuid).cancel();
            loginTimeoutTasks.remove(uuid);
        }
        
        // Remove protection
        plugin.getProtectionManager().removeProtection(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(LogoutEvent event) {
        Player player = event.getPlayer();
        
        // Apply protection
        plugin.getProtectionManager().applyProtection(player);
    }
}
