package com.rookygod.simpleauthme.commands;

import com.rookygod.simpleauthme.SimpleAuthMe;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /register command
 */
public class RegisterCommand implements CommandExecutor {
    private final SimpleAuthMe plugin;

    /**
     * Constructor for RegisterCommand
     *
     * @param plugin The SimpleAuthMe plugin instance
     */
    public RegisterCommand(SimpleAuthMe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        // Check if player is already registered
        if (plugin.getUserDataManager().isPlayerRegistered(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already registered. Use /login <password> to log in.");
            return true;
        }

        // Check if player is already authenticated
        if (plugin.getSessionManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already logged in.");
            return true;
        }

        // Check if password was provided
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /register <password>");
            return true;
        }

        String password = args[0];

        // Register the player
        if (plugin.getUserDataManager().registerPlayer(player, password)) {
            // Authenticate the player
            plugin.getSessionManager().authenticatePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have been registered and logged in successfully!");
        } else {
            player.sendMessage(ChatColor.RED + "Registration failed. Please try again.");
        }

        return true;
    }
}

