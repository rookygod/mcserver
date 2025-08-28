package com.rookygod.simpleauthme;

import com.rookygod.simpleauthme.commands.ChangePasswordCommand;
import com.rookygod.simpleauthme.commands.LoginCommand;
import com.rookygod.simpleauthme.commands.RegisterCommand;
import com.rookygod.simpleauthme.data.SessionManager;
import com.rookygod.simpleauthme.data.UserDataManager;
import com.rookygod.simpleauthme.listeners.AuthListener;
import com.rookygod.simpleauthme.listeners.ProtectionListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for SimpleAuthMe
 */
public class SimpleAuthMe extends JavaPlugin {
    private UserDataManager userDataManager;
    private SessionManager sessionManager;

    @Override
    public void onEnable() {
        // Initialize managers
        this.userDataManager = new UserDataManager(this);
        this.sessionManager = new SessionManager();

        // Register commands
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        getLogger().info("SimpleAuthMe has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save user data if needed
        if (userDataManager != null) {
            userDataManager.saveUsersConfig();
        }
        
        // Clear authenticated players
        if (sessionManager != null) {
            sessionManager.clearAuthentications();
        }
        
        getLogger().info("SimpleAuthMe has been disabled!");
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
}

