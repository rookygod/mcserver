package com.rookygod.simpleauthme.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages user data storage in YAML format
 */
public class UserDataManager {
    private final JavaPlugin plugin;
    private final File usersFile;
    private FileConfiguration usersConfig;

    /**
     * Constructor for UserDataManager
     *
     * @param plugin The JavaPlugin instance
     */
    public UserDataManager(JavaPlugin plugin) {
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
     * @param uuid Player UUID
     * @return true if registered, false otherwise
     */
    public boolean isPlayerRegistered(UUID uuid) {
        return usersConfig.contains("users." + uuid.toString());
    }

    /**
     * Register a new player
     *
     * @param player Player to register
     * @param password Password to set
     * @return true if registration was successful, false otherwise
     */
    public boolean registerPlayer(Player player, String password) {
        UUID uuid = player.getUniqueId();
        String path = "users." + uuid.toString();
        
        if (isPlayerRegistered(uuid)) {
            return false;
        }
        
        usersConfig.set(path + ".name", player.getName());
        usersConfig.set(path + ".password", password);
        saveUsersConfig();
        return true;
    }

    /**
     * Check if a player's password is correct
     *
     * @param uuid Player UUID
     * @param password Password to check
     * @return true if password is correct, false otherwise
     */
    public boolean checkPassword(UUID uuid, String password) {
        if (!isPlayerRegistered(uuid)) {
            return false;
        }
        
        String storedPassword = usersConfig.getString("users." + uuid.toString() + ".password");
        return storedPassword != null && storedPassword.equals(password);
    }

    /**
     * Change a player's password
     *
     * @param uuid Player UUID
     * @param newPassword New password to set
     * @return true if password was changed, false otherwise
     */
    public boolean changePassword(UUID uuid, String newPassword) {
        if (!isPlayerRegistered(uuid)) {
            return false;
        }
        
        usersConfig.set("users." + uuid.toString() + ".password", newPassword);
        saveUsersConfig();
        return true;
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

