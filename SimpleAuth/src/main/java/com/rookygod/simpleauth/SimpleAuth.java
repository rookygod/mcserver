package com.rookygod.simpleauth;

import com.rookygod.simpleauth.commands.ChangePasswordCommand;
import com.rookygod.simpleauth.commands.LoginCommand;
import com.rookygod.simpleauth.commands.RegisterCommand;
import com.rookygod.simpleauth.listeners.PlayerAuthListener;
import com.rookygod.simpleauth.listeners.PlayerProtectionListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleAuth extends JavaPlugin {
    private File usersFile;
    private FileConfiguration usersConfig;
    private final Set<UUID> authenticatedPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Initialize users.yml file
        usersFile = new File(getDataFolder(), "users.yml");
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
                // Create initial structure
                usersConfig = YamlConfiguration.loadConfiguration(usersFile);
                usersConfig.createSection("users");
                usersConfig.save(usersFile);
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not create users.yml file", e);
            }
        }

        // Load users.yml
        loadUsersConfig();

        // Register commands
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerAuthListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this), this);

        getLogger().info("SimpleAuth has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save users.yml if needed
        saveUsersConfig();
        // Clear authenticated players set
        authenticatedPlayers.clear();
        getLogger().info("SimpleAuth has been disabled!");
    }

    /**
     * Load the users configuration from users.yml
     */
    public void loadUsersConfig() {
        usersConfig = YamlConfiguration.loadConfiguration(usersFile);
    }

    /**
     * Save the users configuration to users.yml
     */
    public void saveUsersConfig() {
        try {
            usersConfig.save(usersFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save users.yml file", e);
        }
    }

    /**
     * Check if a player is registered
     * @param uuid Player UUID
     * @return true if registered, false otherwise
     */
    public boolean isPlayerRegistered(UUID uuid) {
        return usersConfig.contains("users." + uuid.toString());
    }

    /**
     * Register a new player
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
     * Authenticate a player
     * @param uuid Player UUID to authenticate
     */
    public void authenticatePlayer(UUID uuid) {
        authenticatedPlayers.add(uuid);
    }

    /**
     * Remove a player's authentication
     * @param uuid Player UUID to deauthenticate
     */
    public void deauthenticatePlayer(UUID uuid) {
        authenticatedPlayers.remove(uuid);
    }

    /**
     * Check if a player is authenticated
     * @param uuid Player UUID to check
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }

    /**
     * Get the users configuration
     * @return FileConfiguration for users.yml
     */
    public FileConfiguration getUsersConfig() {
        return usersConfig;
    }
}

