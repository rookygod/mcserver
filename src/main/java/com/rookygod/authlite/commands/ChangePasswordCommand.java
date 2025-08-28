package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public ChangePasswordCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.changepassword")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is logged in
        if (!plugin.getSessionManager().isAuthenticated(player)) {
            plugin.getMessageManager().sendMessage(player, "change_password.not_logged_in");
            return true;
        }
        
        // Check command usage
        if (args.length < 2) {
            plugin.getMessageManager().sendMessage(player, "change_password.usage");
            return true;
        }
        
        String oldPassword = args[0];
        String newPassword = args[1];
        
        // Check if old password is correct
        if (!plugin.getDataManager().authenticate(player, oldPassword)) {
            plugin.getMessageManager().sendMessage(player, "change_password.wrong_password");
            return true;
        }
        
        // Check password length
        int minLength = plugin.getConfigManager().getMinPasswordLength();
        int maxLength = plugin.getConfigManager().getMaxPasswordLength();
        
        if (newPassword.length() < minLength) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("min", String.valueOf(minLength));
            plugin.getMessageManager().sendMessage(player, "change_password.password_too_short", placeholders);
            return true;
        }
        
        if (newPassword.length() > maxLength) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("max", String.valueOf(maxLength));
            plugin.getMessageManager().sendMessage(player, "change_password.password_too_long", placeholders);
            return true;
        }
        
        // Change password
        if (plugin.getDataManager().changePassword(player.getUniqueId(), newPassword)) {
            // Send success message
            plugin.getMessageManager().sendMessage(player, "change_password.success");
            return true;
        } else {
            // This should not happen, but just in case
            plugin.getMessageManager().sendMessage(player, "change_password.not_logged_in");
            return true;
        }
    }
}

