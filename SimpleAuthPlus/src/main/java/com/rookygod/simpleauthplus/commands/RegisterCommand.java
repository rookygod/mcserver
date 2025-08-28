package com.rookygod.simpleauthplus.commands;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /register command
 */
public class RegisterCommand implements CommandExecutor {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for RegisterCommand
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public RegisterCommand(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.player-only"));
            return true;
        }

        // Check if player is already registered
        if (plugin.getUserDataManager().isPlayerRegistered(player.getName())) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.already-registered"));
            return true;
        }

        // Check if player is already authenticated (should not happen, but just in case)
        if (plugin.getSessionManager().hasSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("login.already-logged-in"));
            return true;
        }

        // Check if password was provided
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.usage"));
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.passwords-not-match"));
            return true;
        }

        // Check password length
        int minLength = plugin.getConfigManager().getMinPasswordLength();
        int maxLength = plugin.getConfigManager().getMaxPasswordLength();
        
        if (password.length() < minLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.password-too-short")
                    .replace("{min_length}", String.valueOf(minLength)));
            return true;
        }
        
        if (password.length() > maxLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.password-too-long")
                    .replace("{max_length}", String.valueOf(maxLength)));
            return true;
        }

        // Register the player
        if (plugin.getUserDataManager().registerPlayer(player, password)) {
            // Create session for the player
            plugin.getSessionManager().createSession(player);
            
            // Remove player from limbo
            plugin.getLimboManager().removePlayerFromLimbo(player);
            
            player.sendMessage(plugin.getConfigManager().getMessage("registration.success"));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("registration.already-registered"));
        }

        return true;
    }
}

