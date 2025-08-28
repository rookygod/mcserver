package com.rookygod.authlite;

import com.rookygod.authlite.commands.*;
import com.rookygod.authlite.config.ConfigManager;
import com.rookygod.authlite.config.MessageManager;
import com.rookygod.authlite.data.DataManager;
import com.rookygod.authlite.listeners.CommandSecurityListener;
import com.rookygod.authlite.listeners.PlayerProtectionListener;
import com.rookygod.authlite.protection.ProtectionManager;
import com.rookygod.authlite.session.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class AuthLite extends JavaPlugin {

    private static AuthLite instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private DataManager dataManager;
    private SessionManager sessionManager;
    private ProtectionManager protectionManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.dataManager = new DataManager(this);
        this.sessionManager = new SessionManager(this);
        this.protectionManager = new ProtectionManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandSecurityListener(this), this);
        
        getLogger().info("AuthLite has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        
        // Clear sessions
        if (sessionManager != null) {
            sessionManager.clearSessions();
        }
        
        getLogger().info("AuthLite has been disabled!");
    }
    
    private void registerCommands() {
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand(this));
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand(this));
        Objects.requireNonNull(getCommand("changepassword")).setExecutor(new ChangePasswordCommand(this));
        Objects.requireNonNull(getCommand("logout")).setExecutor(new LogoutCommand(this));
        Objects.requireNonNull(getCommand("unregister")).setExecutor(new UnregisterCommand(this));
        Objects.requireNonNull(getCommand("authlite")).setExecutor(new AuthLiteCommand(this));
    }
    
    public void reload() {
        // Reload configuration
        configManager.reloadConfig();
        messageManager.reloadMessages();
        
        // Reload data
        dataManager.reloadData();
        
        getLogger().info("AuthLite has been reloaded!");
    }
    
    public static AuthLite getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }
}
