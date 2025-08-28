package com.rookygod.simpleauthplus.data;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages player authentication sessions
 */
public class SessionManager {
    private final SimpleAuthPlus plugin;
    private final Map<UUID, Long> sessions = new HashMap<>();
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Map<String, String> captchas = new HashMap<>();
    private final File sessionsFile;
    private FileConfiguration sessionsConfig;

    /**
     * Constructor for SessionManager
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public SessionManager(SimpleAuthPlus plugin) {
        this.plugin = plugin;
        
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        // Initialize sessions.yml file if session persistence is enabled
        if (plugin.getConfigManager().isPersistSessionsEnabled()) {
            this.sessionsFile = new File(plugin.getDataFolder(), "sessions.yml");
            if (!sessionsFile.exists()) {
                try {
                    sessionsFile.createNewFile();
                    // Create initial structure
                    this.sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
                    this.sessionsConfig.createSection("sessions");
                    this.sessionsConfig.save(sessionsFile);
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create sessions.yml file", e);
                }
            } else {
                this.sessionsConfig = YamlConfiguration.loadConfiguration(sessionsFile);
                loadSessions();
            }
        } else {
            this.sessionsFile = null;
            this.sessionsConfig = null;
        }
    }

    /**
     * Load sessions from sessions.yml
     */
    private void loadSessions() {
        if (sessionsConfig == null || !plugin.getConfigManager().isPersistSessionsEnabled()) {
            return;
        }
        
        sessions.clear();
        
        if (!sessionsConfig.contains("sessions")) {
            return;
        }
        
        for (String uuidString : sessionsConfig.getConfigurationSection("sessions").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                long expiry = sessionsConfig.getLong("sessions." + uuidString);
                
                // Only load sessions that haven't expired
                if (expiry > System.currentTimeMillis()) {
                    sessions.put(uuid, expiry);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.WARNING, "Invalid UUID in sessions.yml: " + uuidString);
            }
        }
    }

    /**
     * Save sessions to sessions.yml
     */
    public void saveSessions() {
        if (sessionsConfig == null || !plugin.getConfigManager().isPersistSessionsEnabled()) {
            return;
        }
        
        sessionsConfig.set("sessions", null);
        
        for (Map.Entry<UUID, Long> entry : sessions.entrySet()) {
            sessionsConfig.set("sessions." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            sessionsConfig.save(sessionsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save sessions.yml file", e);
        }
    }

    /**
     * Create a session for a player
     *
     * @param player Player to create a session for
     */
    public void createSession(Player player) {
        UUID uuid = player.getUniqueId();
        long expiry = System.currentTimeMillis() + (plugin.getConfigManager().getSessionDuration() * 1000L);
        sessions.put(uuid, expiry);
        
        // Reset login attempts and captcha
        loginAttempts.remove(player.getName().toLowerCase());
        captchas.remove(player.getName().toLowerCase());
        
        // Save sessions if persistence is enabled
        if (plugin.getConfigManager().isPersistSessionsEnabled()) {
            saveSessions();
        }
    }

    /**
     * Check if a player has a valid session
     *
     * @param player Player to check
     * @return true if the player has a valid session, false otherwise
     */
    public boolean hasSession(Player player) {
        if (!plugin.getConfigManager().isSessionEnabled()) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        if (!sessions.containsKey(uuid)) {
            return false;
        }
        
        long expiry = sessions.get(uuid);
        if (expiry < System.currentTimeMillis()) {
            sessions.remove(uuid);
            return false;
        }
        
        return true;
    }

    /**
     * Remove a player's session
     *
     * @param player Player to remove the session for
     */
    public void removeSession(Player player) {
        UUID uuid = player.getUniqueId();
        sessions.remove(uuid);
        
        // Save sessions if persistence is enabled
        if (plugin.getConfigManager().isPersistSessionsEnabled()) {
            saveSessions();
        }
    }

    /**
     * Clear all sessions
     */
    public void clearSessions() {
        sessions.clear();
        
        // Save sessions if persistence is enabled
        if (plugin.getConfigManager().isPersistSessionsEnabled()) {
            saveSessions();
        }
    }

    /**
     * Increment a player's login attempts
     *
     * @param playerName Player name
     * @return The number of login attempts
     */
    public int incrementLoginAttempts(String playerName) {
        String name = playerName.toLowerCase();
        int attempts = loginAttempts.getOrDefault(name, 0) + 1;
        loginAttempts.put(name, attempts);
        return attempts;
    }

    /**
     * Get a player's login attempts
     *
     * @param playerName Player name
     * @return The number of login attempts
     */
    public int getLoginAttempts(String playerName) {
        return loginAttempts.getOrDefault(playerName.toLowerCase(), 0);
    }

    /**
     * Reset a player's login attempts
     *
     * @param playerName Player name
     */
    public void resetLoginAttempts(String playerName) {
        loginAttempts.remove(playerName.toLowerCase());
    }

    /**
     * Generate a captcha for a player
     *
     * @param playerName Player name
     * @return The generated captcha
     */
    public String generateCaptcha(String playerName) {
        String name = playerName.toLowerCase();
        String captchaChars = plugin.getConfigManager().getCaptchaCharacters();
        int captchaLength = plugin.getConfigManager().getCaptchaLength();
        
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < captchaLength; i++) {
            int index = (int) (Math.random() * captchaChars.length());
            captcha.append(captchaChars.charAt(index));
        }
        
        String captchaCode = captcha.toString();
        captchas.put(name, captchaCode);
        return captchaCode;
    }

    /**
     * Check if a player needs a captcha
     *
     * @param playerName Player name
     * @return true if the player needs a captcha, false otherwise
     */
    public boolean needsCaptcha(String playerName) {
        if (!plugin.getConfigManager().isCaptchaEnabled()) {
            return false;
        }
        
        int attempts = getLoginAttempts(playerName);
        int requiredAttempts = plugin.getConfigManager().getCaptchaAfterFailedAttempts();
        return attempts >= requiredAttempts;
    }

    /**
     * Verify a player's captcha
     *
     * @param playerName Player name
     * @param captcha Captcha to verify
     * @return true if the captcha is correct, false otherwise
     */
    public boolean verifyCaptcha(String playerName, String captcha) {
        String name = playerName.toLowerCase();
        String storedCaptcha = captchas.get(name);
        
        if (storedCaptcha == null) {
            return false;
        }
        
        return storedCaptcha.equals(captcha);
    }

    /**
     * Remove a player's captcha
     *
     * @param playerName Player name
     */
    public void removeCaptcha(String playerName) {
        captchas.remove(playerName.toLowerCase());
    }

    /**
     * Get a player's captcha
     *
     * @param playerName Player name
     * @return The player's captcha, or null if not found
     */
    public String getCaptcha(String playerName) {
        return captchas.get(playerName.toLowerCase());
    }
}

