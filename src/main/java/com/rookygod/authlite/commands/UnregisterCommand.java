package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.events.UnregisterEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnregisterCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public UnregisterCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.unregister")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is registered
        if (!plugin.getDataManager().isRegistered(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(player, "unregister.not_registered");
            return true;
        }
        
        // Check command usage
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(player, "unregister.usage");
            return true;
        }
        
        String password = args[0];
        
        // Check if password is correct
        if (!plugin.getDataManager().authenticate(player, password)) {
            plugin.getMessageManager().sendMessage(player, "unregister.wrong_password");
            return true;
        }
        
        // Unregister player
        if (plugin.getDataManager().unregisterPlayer(player.getUniqueId())) {
            // Deauthenticate player
            plugin.getSessionManager().deauthenticatePlayer(player);
            
            // Send success message
            plugin.getMessageManager().sendMessage(player, "unregister.success");
            
            // Call unregister event
            UnregisterEvent unregisterEvent = new UnregisterEvent(player);
            plugin.getServer().getPluginManager().callEvent(unregisterEvent);
            
            return true;
        } else {
            // This should not happen, but just in case
            plugin.getMessageManager().sendMessage(player, "unregister.not_registered");
            return true;
        }
    }
}

