package com.rookygod.simpleauthplus;

import com.rookygod.simpleauthplus.commands.*;
import com.rookygod.simpleauthplus.config.ConfigManager;
import com.rookygod.simpleauthplus.data.LimboManager;
import com.rookygod.simpleauthplus.data.SessionManager;
import com.rookygod.simpleauthplus.data.UserDataManager;
import com.rookygod.simpleauthplus.listeners.AuthListener;
import com.rookygod.simpleauthplus.listeners.ProtectionListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for SimpleAuthPlus
 */
public class SimpleAuthPlus extends JavaPlugin {
    private ConfigManager configManager;
    private UserDataManager userDataManager;
    private SessionManager sessionManager;
    private LimboManager limboManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.userDataManager = new UserDataManager(this);
        this.sessionManager = new SessionManager(this);
        this.limboManager = new LimboManager(this);

        // Register commands
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("logout").setExecutor(new LogoutCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        getCommand("authreload").setExecutor(new ReloadCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        getLogger().info("SimpleAuthPlus has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save user data if needed
        if (userDataManager != null) {
            userDataManager.saveUsersConfig();
        }
        
        // Save sessions if persistence is enabled
        if (sessionManager != null) {
            sessionManager.saveSessions();
        }
        
        // Clear limbo players
        if (limboManager != null) {
            limboManager.clearLimboPlayers();
        }
        
        getLogger().info("SimpleAuthPlus has been disabled!");
    }

    /**
     * Get the configuration manager
     *
     * @return The ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Get the user data manager
     *
     * @return The UserDataManager instance
     */
    public UserDataManager getUserDataManager() {
        return userDataManager;
    }

    /**
     * Get the session manager
     *
     * @return The SessionManager instance
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Get the limbo manager
     *
     * @return The LimboManager instance
     */
    public LimboManager getLimboManager() {
        return limboManager;
    }
}

