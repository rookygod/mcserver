package com.rookygod.simpleauthplus.config;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages the plugin configuration files
 */
public class ConfigManager {
    private final SimpleAuthPlus plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messagesFile;

    /**
     * Constructor for ConfigManager
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public ConfigManager(SimpleAuthPlus plugin) {
        this.plugin = plugin;
        setupFiles();
    }

    /**
     * Sets up the configuration files
     */
    private void setupFiles() {
        // Create plugin directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        // Setup config.yml
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Setup messages.yml
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Reloads the configuration files
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Saves the configuration files
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config.yml", e);
        }
    }

    /**
     * Gets the config.yml configuration
     *
     * @return The config.yml FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Gets the messages.yml configuration
     *
     * @return The messages.yml FileConfiguration
     */
    public FileConfiguration getMessages() {
        return messages;
    }

    /**
     * Gets a message from messages.yml with the plugin prefix
     *
     * @param path The path to the message
     * @return The message with the plugin prefix
     */
    public String getMessage(String path) {
        String prefix = messages.getString("prefix", "&8[&bSimpleAuthPlus&8] &r");
        String message = messages.getString(path, "&cMessage not found: " + path);
        return colorize(prefix + message);
    }

    /**
     * Gets a message from messages.yml without the plugin prefix
     *
     * @param path The path to the message
     * @return The message without the plugin prefix
     */
    public String getMessageNoPrefix(String path) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        return colorize(message);
    }

    /**
     * Converts color codes in a string
     *
     * @param text The text to colorize
     * @return The colorized text
     */
    public String colorize(String text) {
        return text.replace("&", "§");
    }

    /**
     * Gets the session duration in seconds
     *
     * @return The session duration in seconds
     */
    public int getSessionDuration() {
        return config.getInt("session.duration", 600);
    }

    /**
     * Checks if sessions are enabled
     *
     * @return True if sessions are enabled, false otherwise
     */
    public boolean isSessionEnabled() {
        return config.getBoolean("session.enabled", true);
    }

    /**
     * Checks if sessions should persist across server restarts
     *
     * @return True if sessions should persist across server restarts, false otherwise
     */
    public boolean isPersistSessionsEnabled() {
        return config.getBoolean("session.persist-across-restarts", false);
    }

    /**
     * Checks if auto-registration is enabled
     *
     * @return True if auto-registration is enabled, false otherwise
     */
    public boolean isAutoRegisterEnabled() {
        return config.getBoolean("registration.auto-register", false);
    }

    /**
     * Gets the minimum password length
     *
     * @return The minimum password length
     */
    public int getMinPasswordLength() {
        return config.getInt("registration.min-password-length", 4);
    }

    /**
     * Gets the maximum password length
     *
     * @return The maximum password length
     */
    public int getMaxPasswordLength() {
        return config.getInt("registration.max-password-length", 20);
    }

    /**
     * Gets the maximum number of login attempts
     *
     * @return The maximum number of login attempts
     */
    public int getMaxLoginAttempts() {
        return config.getInt("login.max-attempts", 3);
    }

    /**
     * Gets the login timeout in seconds
     *
     * @return The login timeout in seconds
     */
    public int getLoginTimeout() {
        return config.getInt("login.timeout", 60);
    }

    /**
     * Checks if teleport on login is enabled
     *
     * @return True if teleport on login is enabled, false otherwise
     */
    public boolean isTeleportOnLoginEnabled() {
        return config.getBoolean("login.teleport-on-login", true);
    }

    /**
     * Checks if teleport to last location is enabled
     *
     * @return True if teleport to last location is enabled, false otherwise
     */
    public boolean isTeleportToLastLocationEnabled() {
        return config.getBoolean("login.teleport-to-last-location", false);
    }

    /**
     * Checks if captcha is enabled
     *
     * @return True if captcha is enabled, false otherwise
     */
    public boolean isCaptchaEnabled() {
        return config.getBoolean("captcha.enabled", true);
    }

    /**
     * Gets the number of failed login attempts before requiring captcha
     *
     * @return The number of failed login attempts before requiring captcha
     */
    public int getCaptchaAfterFailedAttempts() {
        return config.getInt("captcha.after-failed-attempts", 2);
    }

    /**
     * Gets the captcha length
     *
     * @return The captcha length
     */
    public int getCaptchaLength() {
        return config.getInt("captcha.length", 5);
    }

    /**
     * Gets the captcha characters
     *
     * @return The captcha characters
     */
    public String getCaptchaCharacters() {
        return config.getString("captcha.characters", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
    }

    /**
     * Checks if limbo mode is enabled
     *
     * @return True if limbo mode is enabled, false otherwise
     */
    public boolean isLimboEnabled() {
        return config.getBoolean("limbo.enabled", true);
    }

    /**
     * Checks if hiding players in limbo is enabled
     *
     * @return True if hiding players in limbo is enabled, false otherwise
     */
    public boolean isHidePlayersEnabled() {
        return config.getBoolean("limbo.hide-players", true);
    }

    /**
     * Checks if disabling damage in limbo is enabled
     *
     * @return True if disabling damage in limbo is enabled, false otherwise
     */
    public boolean isDisableDamageEnabled() {
        return config.getBoolean("limbo.disable-damage", true);
    }

    /**
     * Gets the spawn location
     *
     * @return The spawn location
     */
    public Location getSpawnLocation() {
        String worldName = config.getString("spawn.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }
        
        double x = config.getDouble("spawn.x", 0);
        double y = config.getDouble("spawn.y", 64);
        double z = config.getDouble("spawn.z", 0);
        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);
        
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Sets the spawn location
     *
     * @param location The spawn location
     */
    public void setSpawnLocation(Location location) {
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        saveConfig();
    }

    /**
     * Checks if blocking commands for unauthenticated players is enabled
     *
     * @return True if blocking commands for unauthenticated players is enabled, false otherwise
     */
    public boolean isBlockCommandsEnabled() {
        return config.getBoolean("advanced.block-commands", true);
    }

    /**
     * Gets the list of allowed commands for unauthenticated players
     *
     * @return The list of allowed commands for unauthenticated players
     */
    public List<String> getAllowedCommands() {
        return config.getStringList("advanced.allowed-commands");
    }

    /**
     * Checks if blocking chat for unauthenticated players is enabled
     *
     * @return True if blocking chat for unauthenticated players is enabled, false otherwise
     */
    public boolean isBlockChatEnabled() {
        return config.getBoolean("advanced.block-chat", true);
    }

    /**
     * Checks if blocking movement for unauthenticated players is enabled
     *
     * @return True if blocking movement for unauthenticated players is enabled, false otherwise
     */
    public boolean isBlockMovementEnabled() {
        return config.getBoolean("advanced.block-movement", true);
    }

    /**
     * Checks if blocking interaction for unauthenticated players is enabled
     *
     * @return True if blocking interaction for unauthenticated players is enabled, false otherwise
     */
    public boolean isBlockInteractionEnabled() {
        return config.getBoolean("advanced.block-interaction", true);
    }

    /**
     * Checks if saving the last login IP is enabled
     *
     * @return True if saving the last login IP is enabled, false otherwise
     */
    public boolean isSaveLastIpEnabled() {
        return config.getBoolean("advanced.save-last-ip", true);
    }

    /**
     * Checks if saving the last login time is enabled
     *
     * @return True if saving the last login time is enabled, false otherwise
     */
    public boolean isSaveLastLoginEnabled() {
        return config.getBoolean("advanced.save-last-login", true);
    }
}

