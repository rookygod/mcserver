package com.rookygod.simpleauthplus.listeners;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

/**
 * Listener for protecting unauthenticated players
 */
public class ProtectionListener implements Listener {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for ProtectionListener
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public ProtectionListener(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player) && plugin.getConfigManager().isBlockMovementEnabled()) {
            // Allow small head movements but prevent actual movement
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || 
                event.getFrom().getBlockY() != event.getTo().getBlockY() || 
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                
                event.setCancelled(true);
                player.sendMessage(plugin.getConfigManager().getMessage("protection.move-blocked"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player) && plugin.getConfigManager().isBlockChatEnabled()) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.chat-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player) && plugin.getConfigManager().isBlockInteractionEnabled()) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (!plugin.getSessionManager().hasSession(player) && plugin.getConfigManager().isBlockCommandsEnabled()) {
            // Check if command is allowed
            for (String allowedCommand : plugin.getConfigManager().getAllowedCommands()) {
                if (command.startsWith(allowedCommand)) {
                    return;
                }
            }
            
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.command-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("protection.interaction-blocked"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getSessionManager().hasSession(player) && plugin.getConfigManager().isDisableDamageEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getSessionManager().hasSession(player)) {
            event.setCancelled(true);
        }
    }
}

