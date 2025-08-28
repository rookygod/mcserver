package com.rookygod.simpleauth.commands;

import com.rookygod.simpleauth.SimpleAuth;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangePasswordCommand implements CommandExecutor {
    private final SimpleAuth plugin;

    public ChangePasswordCommand(SimpleAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if player is not registered
        if (!plugin.isPlayerRegistered(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not registered. Use /register <password> to register.");
            return true;
        }

        // Check if player is not authenticated
        if (!plugin.isAuthenticated(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be logged in to change your password. Use /login <password> to log in.");
            return true;
        }

        // Check if old and new passwords were provided
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /changepassword <old> <new>");
            return true;
        }

        String oldPassword = args[0];
        String newPassword = args[1];

        // Check if old password is correct
        if (!plugin.checkPassword(player.getUniqueId(), oldPassword)) {
            player.sendMessage(ChatColor.RED + "Your old password is incorrect. Please try again.");
            return true;
        }

        // Change password
        if (plugin.changePassword(player.getUniqueId(), newPassword)) {
            player.sendMessage(ChatColor.GREEN + "Your password has been changed successfully!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to change password. Please try again.");
        }

        return true;
    }
}

