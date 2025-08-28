package com.rookygod.authlite.data;

import com.rookygod.authlite.AuthLite;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DataManager {

    private final AuthLite plugin;
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> usernameToUuidMap = new ConcurrentHashMap<>();
    private File playersFile;
    private FileConfiguration playersConfig;
    
    public DataManager(AuthLite plugin) {
        this.plugin = plugin;
        loadData();
    }
    
    public void loadData() {
        // Create players.yml file if it doesn't exist
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.getParentFile().mkdirs();
                playersFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create players.yml file", e);
            }
        }
        
        // Load players.yml
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        
        // Load player data
        ConfigurationSection playersSection = playersConfig.getConfigurationSection("players");
        if (playersSection != null) {
            for (String uuidString : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidString);
                    
                    if (playerSection != null) {
                        String username = playerSection.getString("username");
                        String password = playerSection.getString("password");
                        String lastIp = playerSection.getString("last-ip", "");
                        long lastLogin = playerSection.getLong("last-login", 0);
                        
                        PlayerData playerData = new PlayerData(uuid, username, password, lastIp, lastLogin);
                        playerDataMap.put(uuid, playerData);
                        usernameToUuidMap.put(username.toLowerCase(), uuid);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in players.yml: " + uuidString);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + playerDataMap.size() + " player data entries");
    }
    
    public void saveAllData() {
        // Clear existing data
        playersConfig.set("players", null);
        
        // Save all player data
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerData playerData = entry.getValue();
            
            String path = "players." + uuid.toString();
            playersConfig.set(path + ".username", playerData.getUsername());
            playersConfig.set(path + ".password", playerData.getPassword());
            playersConfig.set(path + ".last-ip", playerData.getLastIp());
            playersConfig.set(path + ".last-login", playerData.getLastLogin());
        }
        
        // Save to file
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save players.yml file", e);
        }
    }
    
    public void savePlayerData(PlayerData playerData) {
        UUID uuid = playerData.getUuid();
        
        String path = "players." + uuid.toString();
        playersConfig.set(path + ".username", playerData.getUsername());
        playersConfig.set(path + ".password", playerData.getPassword());
        playersConfig.set(path + ".last-ip", playerData.getLastIp());
        playersConfig.set(path + ".last-login", playerData.getLastLogin());
        
        // Save to file
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save players.yml file", e);
        }
    }
    
    public void reloadData() {
        // Save current data
        saveAllData();
        
        // Clear maps
        playerDataMap.clear();
        usernameToUuidMap.clear();
        
        // Reload data
        loadData();
    }
    
    public boolean isRegistered(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }
    
    public boolean isRegistered(String username) {
        return usernameToUuidMap.containsKey(username.toLowerCase());
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
    
    public PlayerData getPlayerData(String username) {
        UUID uuid = usernameToUuidMap.get(username.toLowerCase());
        return uuid != null ? playerDataMap.get(uuid) : null;
    }
    
    public boolean registerPlayer(Player player, String password) {
        UUID uuid = player.getUniqueId();
        String username = player.getName();
        
        // Check if player is already registered
        if (isRegistered(uuid)) {
            return false;
        }
        
        // Create new player data
        PlayerData playerData = new PlayerData(uuid, username, password);
        playerData.setLastIp(player.getAddress().getAddress().getHostAddress());
        playerData.updateLastLogin();
        
        // Add to maps
        playerDataMap.put(uuid, playerData);
        usernameToUuidMap.put(username.toLowerCase(), uuid);
        
        // Save to file
        savePlayerData(playerData);
        
        return true;
    }
    
    public boolean registerPlayer(String username, UUID uuid, String password) {
        // Check if player is already registered
        if (isRegistered(uuid)) {
            return false;
        }
        
        // Create new player data
        PlayerData playerData = new PlayerData(uuid, username, password);
        
        // Add to maps
        playerDataMap.put(uuid, playerData);
        usernameToUuidMap.put(username.toLowerCase(), uuid);
        
        // Save to file
        savePlayerData(playerData);
        
        return true;
    }
    
    public boolean unregisterPlayer(UUID uuid) {
        // Check if player is registered
        if (!isRegistered(uuid)) {
            return false;
        }
        
        // Get player data
        PlayerData playerData = getPlayerData(uuid);
        
        // Remove from maps
        playerDataMap.remove(uuid);
        usernameToUuidMap.remove(playerData.getUsername().toLowerCase());
        
        // Remove from file
        playersConfig.set("players." + uuid.toString(), null);
        
        // Save to file
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save players.yml file", e);
        }
        
        return true;
    }
    
    public boolean changePassword(UUID uuid, String newPassword) {
        // Check if player is registered
        if (!isRegistered(uuid)) {
            return false;
        }
        
        // Get player data
        PlayerData playerData = getPlayerData(uuid);
        
        // Change password
        playerData.setPassword(newPassword);
        
        // Save to file
        savePlayerData(playerData);
        
        return true;
    }
    
    public boolean authenticate(Player player, String password) {
        UUID uuid = player.getUniqueId();
        
        // Check if player is registered
        if (!isRegistered(uuid)) {
            return false;
        }
        
        // Get player data
        PlayerData playerData = getPlayerData(uuid);
        
        // Check password
        if (playerData.getPassword().equals(password)) {
            // Update last login and IP
            playerData.setLastIp(player.getAddress().getAddress().getHostAddress());
            playerData.updateLastLogin();
            playerData.resetLoginAttempts();
            
            // Save to file
            savePlayerData(playerData);
            
            return true;
        } else {
            // Increment login attempts
            playerData.incrementLoginAttempts();
            
            return false;
        }
    }
}

