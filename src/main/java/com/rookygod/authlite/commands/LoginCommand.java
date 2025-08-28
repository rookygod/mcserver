package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.data.PlayerData;
import com.rookygod.authlite.events.LoginEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LoginCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public LoginCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Hide sensitive command from console logs
        if (plugin.getConfigManager().isHideSensitiveCommands()) {
            plugin.getLogger().info(sender.getName() + " issued a sensitive command (password hidden)");
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.login")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is already logged in
        if (plugin.getSessionManager().isAuthenticated(player)) {
            plugin.getMessageManager().sendMessage(player, "login.already_logged_in");
            return true;
        }
        
        // Check if player is registered
        if (!plugin.getDataManager().isRegistered(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(player, "login.not_registered");
            return true;
        }
        
        // Check command usage
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(player, "login.usage");
            return true;
        }
        
        String password = args[0];
        
        // Get player data
        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        // Check if player has exceeded max login attempts
        if (playerData.getLoginAttempts() >= plugin.getConfigManager().getMaxLoginAttempts()) {
            if (plugin.getConfigManager().isKickOnWrongPassword()) {
                player.kickPlayer(plugin.getMessageManager().getMessage("login.max_attempts"));
            } else {
                plugin.getMessageManager().sendMessage(player, "login.max_attempts");
            }
            return true;
        }
        
        // Authenticate player
        if (plugin.getDataManager().authenticate(player, password)) {
            // Set player as authenticated
            plugin.getSessionManager().authenticatePlayer(player);
            
            // Send success message
            plugin.getMessageManager().sendMessage(player, "login.success");
            
            // Call login event
            LoginEvent loginEvent = new LoginEvent(player);
            plugin.getServer().getPluginManager().callEvent(loginEvent);
            
            return true;
        } else {
            // Send wrong password message
            plugin.getMessageManager().sendMessage(player, "login.wrong_password");
            
            // Check if player should be kicked
            if (plugin.getConfigManager().isKickOnWrongPassword() && 
                    playerData.getLoginAttempts() >= plugin.getConfigManager().getMaxLoginAttempts()) {
                player.kickPlayer(plugin.getMessageManager().getMessage("login.max_attempts"));
            }
            
            return true;
        }
    }
}
