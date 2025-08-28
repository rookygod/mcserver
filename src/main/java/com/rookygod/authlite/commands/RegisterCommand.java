package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.events.RegisterEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RegisterCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public RegisterCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.register")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is already registered
        if (plugin.getDataManager().isRegistered(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(player, "register.already_registered");
            return true;
        }
        
        // Check command usage
        if (args.length < 2) {
            plugin.getMessageManager().sendMessage(player, "register.usage");
            return true;
        }
        
        String password = args[0];
        String confirmPassword = args[1];
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            plugin.getMessageManager().sendMessage(player, "register.passwords_not_match");
            return true;
        }
        
        // Check password length
        int minLength = plugin.getConfigManager().getMinPasswordLength();
        int maxLength = plugin.getConfigManager().getMaxPasswordLength();
        
        if (password.length() < minLength) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("min", String.valueOf(minLength));
            plugin.getMessageManager().sendMessage(player, "register.password_too_short", placeholders);
            return true;
        }
        
        if (password.length() > maxLength) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("max", String.valueOf(maxLength));
            plugin.getMessageManager().sendMessage(player, "register.password_too_long", placeholders);
            return true;
        }
        
        // Register player
        if (plugin.getDataManager().registerPlayer(player, password)) {
            // Set player as authenticated
            plugin.getSessionManager().authenticatePlayer(player);
            
            // Send success message
            plugin.getMessageManager().sendMessage(player, "register.success");
            
            // Call register event
            RegisterEvent registerEvent = new RegisterEvent(player);
            plugin.getServer().getPluginManager().callEvent(registerEvent);
            
            return true;
        } else {
            // This should not happen, but just in case
            plugin.getMessageManager().sendMessage(player, "register.already_registered");
            return true;
        }
    }
}

