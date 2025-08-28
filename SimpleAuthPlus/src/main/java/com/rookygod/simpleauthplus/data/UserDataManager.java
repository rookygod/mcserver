package com.rookygod.simpleauthplus.data;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages user data storage in YAML format
 */
public class UserDataManager {
    private final SimpleAuthPlus plugin;
    private final File usersFile;
    private FileConfiguration usersConfig;

    /**
     * Constructor for UserDataManager
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public UserDataManager(SimpleAuthPlus plugin) {
        this.plugin = plugin;
        
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        // Initialize users.yml file
        this.usersFile = new File(plugin.getDataFolder(), "users.yml");
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
                // Create initial structure
                this.usersConfig = YamlConfiguration.loadConfiguration(usersFile);
                this.usersConfig.createSection("users");
                this.usersConfig.save(usersFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create users.yml file", e);
            }
        } else {
            this.usersConfig = YamlConfiguration.loadConfiguration(usersFile);
        }
    }

    /**
     * Reload the users configuration from disk
     */
    public void loadUsersConfig() {
        this.usersConfig = YamlConfiguration.loadConfiguration(usersFile);
    }

    /**
     * Save the users configuration to disk
     */
    public void saveUsersConfig() {
        try {
            usersConfig.save(usersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save users.yml file", e);
        }
    }

    /**
     * Check if a player is registered
     *
     * @param playerName Player name
     * @return true if registered, false otherwise
     */
    public boolean isPlayerRegistered(String playerName) {
        return usersConfig.contains("users." + playerName.toLowerCase());
    }

    /**
     * Register a new player
     *
     * @param player Player to register
     * @param password Password to set
     * @return true if registration was successful, false otherwise
     */
    public boolean registerPlayer(Player player, String password) {
        String playerName = player.getName().toLowerCase();
        String path = "users." + playerName;
        
        if (isPlayerRegistered(playerName)) {
            return false;
        }
        
        usersConfig.set(path + ".password", password);
        
        // Save additional data if enabled
        if (plugin.getConfigManager().isSaveLastIpEnabled()) {
            usersConfig.set(path + ".ip", player.getAddress().getAddress().getHostAddress());
        }
        
        if (plugin.getConfigManager().isSaveLastLoginEnabled()) {
            usersConfig.set(path + ".lastLogin", System.currentTimeMillis());
        }
        
        // Save UUID for reference
        usersConfig.set(path + ".uuid", player.getUniqueId().toString());
        
        saveUsersConfig();
        return true;
    }

    /**
     * Check if a player's password is correct
     *
     * @param playerName Player name
     * @param password Password to check
     * @return true if password is correct, false otherwise
     */
    public boolean checkPassword(String playerName, String password) {
        if (!isPlayerRegistered(playerName)) {
            return false;
        }
        
        String storedPassword = usersConfig.getString("users." + playerName.toLowerCase() + ".password");
        return storedPassword != null && storedPassword.equals(password);
    }

    /**
     * Change a player's password
     *
     * @param playerName Player name
     * @param newPassword New password to set
     * @return true if password was changed, false otherwise
     */
    public boolean changePassword(String playerName, String newPassword) {
        if (!isPlayerRegistered(playerName)) {
            return false;
        }
        
        usersConfig.set("users." + playerName.toLowerCase() + ".password", newPassword);
        saveUsersConfig();
        return true;
    }

    /**
     * Update a player's last login information
     *
     * @param player Player to update
     */
    public void updateLastLogin(Player player) {
        String playerName = player.getName().toLowerCase();
        
        if (!isPlayerRegistered(playerName)) {
            return;
        }
        
        String path = "users." + playerName;
        
        if (plugin.getConfigManager().isSaveLastIpEnabled()) {
            usersConfig.set(path + ".ip", player.getAddress().getAddress().getHostAddress());
        }
        
        if (plugin.getConfigManager().isSaveLastLoginEnabled()) {
            usersConfig.set(path + ".lastLogin", System.currentTimeMillis());
        }
        
        saveUsersConfig();
    }

    /**
     * Get a player's last login time
     *
     * @param playerName Player name
     * @return The player's last login time, or 0 if not found
     */
    public long getLastLoginTime(String playerName) {
        if (!isPlayerRegistered(playerName)) {
            return 0;
        }
        
        return usersConfig.getLong("users." + playerName.toLowerCase() + ".lastLogin", 0);
    }

    /**
     * Get a player's last login IP
     *
     * @param playerName Player name
     * @return The player's last login IP, or null if not found
     */
    public String getLastLoginIp(String playerName) {
        if (!isPlayerRegistered(playerName)) {
            return null;
        }
        
        return usersConfig.getString("users." + playerName.toLowerCase() + ".ip");
    }

    /**
     * Get a player's UUID
     *
     * @param playerName Player name
     * @return The player's UUID, or null if not found
     */
    public UUID getPlayerUuid(String playerName) {
        if (!isPlayerRegistered(playerName)) {
            return null;
        }
        
        String uuidString = usersConfig.getString("users." + playerName.toLowerCase() + ".uuid");
        if (uuidString == null) {
            return null;
        }
        
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the users configuration
     *
     * @return FileConfiguration for users.yml
     */
    public FileConfiguration getUsersConfig() {
        return usersConfig;
    }
}

