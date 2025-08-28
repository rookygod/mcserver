package com.rookygod.simpleauthplus.commands;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /changepassword command
 */
public class ChangePasswordCommand implements CommandExecutor {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for ChangePasswordCommand
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public ChangePasswordCommand(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.player-only"));
            return true;
        }

        // Check if player is not authenticated
        if (!plugin.getSessionManager().hasSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("change-password.not-logged-in"));
            return true;
        }

        // Check if old and new passwords were provided
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("change-password.usage"));
            return true;
        }

        String oldPassword = args[0];
        String newPassword = args[1];
        String playerName = player.getName();

        // Check if old password is correct
        if (!plugin.getUserDataManager().checkPassword(playerName, oldPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("change-password.wrong-password"));
            return true;
        }

        // Check new password length
        int minLength = plugin.getConfigManager().getMinPasswordLength();
        int maxLength = plugin.getConfigManager().getMaxPasswordLength();
        
        if (newPassword.length() < minLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.password-too-short")
                    .replace("{min_length}", String.valueOf(minLength)));
            return true;
        }
        
        if (newPassword.length() > maxLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.password-too-long")
                    .replace("{max_length}", String.valueOf(maxLength)));
            return true;
        }

        // Change password
        if (plugin.getUserDataManager().changePassword(playerName, newPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("change-password.success"));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("change-password.wrong-password"));
        }

        return true;
    }
}

