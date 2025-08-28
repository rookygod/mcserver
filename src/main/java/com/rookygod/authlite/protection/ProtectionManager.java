package com.rookygod.authlite.protection;

import com.rookygod.authlite.AuthLite;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ProtectionManager {

    private final AuthLite plugin;
    
    public ProtectionManager(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    public boolean shouldBlockMovement(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlockMovement();
    }
    
    public boolean shouldBlockChat(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlockChat();
    }
    
    public boolean shouldBlockCommand(Player player, String command) {
        if (plugin.getSessionManager().isAuthenticated(player)) {
            return false;
        }
        
        if (!plugin.getConfigManager().isBlockCommands()) {
            return false;
        }
        
        // Check if command is allowed
        List<String> allowedCommands = plugin.getConfigManager().getAllowedCommands();
        for (String allowedCommand : allowedCommands) {
            if (command.startsWith(allowedCommand)) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean shouldBlockInteraction(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlockInteraction();
    }
    
    public boolean shouldBlockDamage(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlockDamage();
    }
    
    public boolean shouldBlockInventory(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlockInventory();
    }
    
    public boolean shouldTeleportToSpawn(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isTeleportToSpawn();
    }
    
    public boolean shouldHideFromPlayerList(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isHideFromPlayerList();
    }
    
    public boolean shouldApplyBlindEffect(Player player) {
        return !plugin.getSessionManager().isAuthenticated(player) && plugin.getConfigManager().isBlindEffect();
    }
    
    public void applyProtection(Player player) {
        // Apply blindness effect if enabled
        if (shouldApplyBlindEffect(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        }
        
        // Hide from player list if enabled
        if (shouldHideFromPlayerList(player)) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }
        
        // Teleport to spawn if enabled
        if (shouldTeleportToSpawn(player)) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
        
        // Send message to player
        if (plugin.getDataManager().isRegistered(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(player, "protection.must_login");
        } else if (plugin.getConfigManager().isForceRegister()) {
            plugin.getMessageManager().sendMessage(player, "protection.must_register");
        }
    }
    
    public void removeProtection(Player player) {
        // Remove blindness effect if it was applied
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        
        // Show player in player list if they were hidden
        if (plugin.getConfigManager().isHideFromPlayerList()) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }
        }
    }
}

