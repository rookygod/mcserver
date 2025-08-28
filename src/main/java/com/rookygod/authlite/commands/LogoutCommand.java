package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.events.LogoutEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogoutCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public LogoutCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.logout")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is logged in
        if (!plugin.getSessionManager().isAuthenticated(player)) {
            plugin.getMessageManager().sendMessage(player, "logout.not_logged_in");
            return true;
        }
        
        // Logout player
        plugin.getSessionManager().deauthenticatePlayer(player);
        
        // Send success message
        plugin.getMessageManager().sendMessage(player, "logout.success");
        
        // Call logout event
        LogoutEvent logoutEvent = new LogoutEvent(player);
        plugin.getServer().getPluginManager().callEvent(logoutEvent);
        
        return true;
    }
}

