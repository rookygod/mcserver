package com.rookygod.authlite.config;

import com.rookygod.authlite.AuthLite;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final AuthLite plugin;
    private FileConfiguration config;
    
    // Authentication settings
    private int minPasswordLength;
    private int maxPasswordLength;
    private boolean forceRegister;
    private int loginTimeout;
    private int maxLoginAttempts;
    private boolean kickOnWrongPassword;
    private boolean kickOnTimeout;
    private int reminderInterval;
    
    // Session settings
    private boolean enableSessions;
    private int sessionTimeout;
    private boolean sessionCheckIP;
    
    // Protection settings
    private boolean blockMovement;
    private boolean blockChat;
    private boolean blockCommands;
    private List<String> allowedCommands;
    private boolean blockInteraction;
    private boolean blockDamage;
    private boolean blockInventory;
    private boolean teleportToSpawn;
    private boolean hideFromPlayerList;
    private boolean blindEffect;
    
    public ConfigManager(AuthLite plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        // Save default config if it doesn't exist
        plugin.saveDefaultConfig();
        
        // Load config
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Load settings
        loadSettings();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadSettings();
    }
    
    private void loadSettings() {
        // Authentication settings
        minPasswordLength = config.getInt("authentication.min-password-length", 4);
        maxPasswordLength = config.getInt("authentication.max-password-length", 16);
        forceRegister = config.getBoolean("authentication.force-register", true);
        loginTimeout = config.getInt("authentication.login-timeout", 60);
        maxLoginAttempts = config.getInt("authentication.max-login-attempts", 3);
        kickOnWrongPassword = config.getBoolean("authentication.kick-on-wrong-password", true);
        kickOnTimeout = config.getBoolean("authentication.kick-on-timeout", true);
        reminderInterval = config.getInt("authentication.reminder-interval", 10);
        
        // Session settings
        enableSessions = config.getBoolean("session.enable", true);
        sessionTimeout = config.getInt("session.timeout", 1440); // 24 hours in minutes
        sessionCheckIP = config.getBoolean("session.check-ip", true);
        
        // Protection settings
        blockMovement = config.getBoolean("protection.block-movement", true);
        blockChat = config.getBoolean("protection.block-chat", true);
        blockCommands = config.getBoolean("protection.block-commands", true);
        allowedCommands = config.getStringList("protection.allowed-commands");
        blockInteraction = config.getBoolean("protection.block-interaction", true);
        blockDamage = config.getBoolean("protection.block-damage", true);
        blockInventory = config.getBoolean("protection.block-inventory", true);
        teleportToSpawn = config.getBoolean("protection.teleport-to-spawn", true);
        hideFromPlayerList = config.getBoolean("protection.hide-from-player-list", false);
        blindEffect = config.getBoolean("protection.blind-effect", false);
    }
    
    // Getters for all settings
    public int getMinPasswordLength() {
        return minPasswordLength;
    }
    
    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }
    
    public boolean isForceRegister() {
        return forceRegister;
    }
    
    public int getLoginTimeout() {
        return loginTimeout;
    }
    
    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }
    
    public boolean isKickOnWrongPassword() {
        return kickOnWrongPassword;
    }
    
    public boolean isKickOnTimeout() {
        return kickOnTimeout;
    }
    
    public int getReminderInterval() {
        return reminderInterval;
    }
    
    public boolean isEnableSessions() {
        return enableSessions;
    }
    
    public int getSessionTimeout() {
        return sessionTimeout;
    }
    
    public boolean isSessionCheckIP() {
        return sessionCheckIP;
    }
    
    public boolean isBlockMovement() {
        return blockMovement;
    }
    
    public boolean isBlockChat() {
        return blockChat;
    }
    
    public boolean isBlockCommands() {
        return blockCommands;
    }
    
    public List<String> getAllowedCommands() {
        return allowedCommands;
    }
    
    public boolean isBlockInteraction() {
        return blockInteraction;
    }
    
    public boolean isBlockDamage() {
        return blockDamage;
    }
    
    public boolean isBlockInventory() {
        return blockInventory;
    }
    
    public boolean isTeleportToSpawn() {
        return teleportToSpawn;
    }
    
    public boolean isHideFromPlayerList() {
        return hideFromPlayerList;
    }
    
    public boolean isBlindEffect() {
        return blindEffect;
    }
}
