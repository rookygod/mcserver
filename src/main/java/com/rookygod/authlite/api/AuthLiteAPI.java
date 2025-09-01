package com.rookygod.authlite.api;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * API for other plugins to interact with AuthLite.
 */
public class AuthLiteAPI {

    private static AuthLiteAPI instance;
    private final AuthLite plugin;
    
    private AuthLiteAPI(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Get the instance of the API.
     *
     * @return The API instance
     */
    public static AuthLiteAPI getInstance() {
        if (instance == null) {
            instance = new AuthLiteAPI(AuthLite.getInstance());
        }
        return instance;
    }
    
    /**
     * Check if a player is registered.
     *
     * @param player The player to check
     * @return True if the player is registered, false otherwise
     */
    public boolean isRegistered(Player player) {
        return plugin.getDataManager().isRegistered(player.getUniqueId());
    }
    
    /**
     * Check if a player is registered.
     *
     * @param uuid The UUID of the player to check
     * @return True if the player is registered, false otherwise
     */
    public boolean isRegistered(UUID uuid) {
        return plugin.getDataManager().isRegistered(uuid);
    }
    
    /**
     * Check if a player is registered.
     *
     * @param username The username of the player to check
     * @return True if the player is registered, false otherwise
     */
    public boolean isRegistered(String username) {
        return plugin.getDataManager().isRegistered(username);
    }
    
    /**
     * Check if a player is authenticated.
     *
     * @param player The player to check
     * @return True if the player is authenticated, false otherwise
     */
    public boolean isAuthenticated(Player player) {
        return plugin.getSessionManager().isAuthenticated(player);
    }
    
    /**
     * Register a player.
     *
     * @param player The player to register
     * @param password The password to register with
     * @return True if the player was registered, false if they are already registered
     */
    public boolean registerPlayer(Player player, String password) {
        return plugin.getDataManager().registerPlayer(player, password);
    }
    
    /**
     * Unregister a player.
     *
     * @param player The player to unregister
     * @return True if the player was unregistered, false if they are not registered
     */
    public boolean unregisterPlayer(Player player) {
        return plugin.getDataManager().unregisterPlayer(player.getUniqueId());
    }
    
    /**
     * Authenticate a player.
     *
     * @param player The player to authenticate
     * @param password The password to authenticate with
     * @return True if the player was authenticated, false if the password is incorrect
     */
    public boolean authenticatePlayer(Player player, String password) {
        if (plugin.getDataManager().authenticate(player, password)) {
            plugin.getSessionManager().authenticatePlayer(player);
            return true;
        }
        return false;
    }
    
    /**
     * Deauthenticate a player.
     *
     * @param player The player to deauthenticate
     */
    public void deauthenticatePlayer(Player player) {
        plugin.getSessionManager().deauthenticatePlayer(player);
    }
    
    /**
     * Change a player's password.
     *
     * @param player The player to change the password for
     * @param newPassword The new password
     * @return True if the password was changed, false if the player is not registered
     */
    public boolean changePassword(Player player, String newPassword) {
        return plugin.getDataManager().changePassword(player.getUniqueId(), newPassword);
    }
    
    /**
     * Get a player's last login time.
     *
     * @param player The player to get the last login time for
     * @return The last login time in seconds since epoch, or 0 if the player is not registered
     */
    public long getLastLoginTime(Player player) {
        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());
        return playerData != null ? playerData.getLastLogin() : 0;
    }
    
    /**
     * Get a player's last IP address.
     *
     * @param player The player to get the last IP address for
     * @return The last IP address, or an empty string if the player is not registered
     */
    public String getLastIpAddress(Player player) {
        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());
        return playerData != null ? playerData.getLastIp() : "";
    }
}

