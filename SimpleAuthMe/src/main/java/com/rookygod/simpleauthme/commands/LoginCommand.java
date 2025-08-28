package com.rookygod.simpleauthme.commands;

import com.rookygod.simpleauthme.SimpleAuthMe;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /login command
 */
public class LoginCommand implements CommandExecutor {
    private final SimpleAuthMe plugin;

    /**
     * Constructor for LoginCommand
     *
     * @param plugin The SimpleAuthMe plugin instance
     */
    public LoginCommand(SimpleAuthMe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if player is not registered
        if (!plugin.getUserDataManager().isPlayerRegistered(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not registered. Use /register <password> to register.");
            return true;
        }

        // Check if player is already authenticated
        if (plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already logged in.");
            return true;
        }

        // Check if password was provided
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /login <password>");
            return true;
        }

        String password = args[0];

        // Check password
        if (plugin.getUserDataManager().checkPassword(player.getUniqueId(), password)) {
            // Authenticate the player
            plugin.getSessionManager().authenticatePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have been logged in successfully!");
        } else {
            player.sendMessage(ChatColor.RED + "Incorrect password. Please try again.");
        }

        return true;
    }
}

