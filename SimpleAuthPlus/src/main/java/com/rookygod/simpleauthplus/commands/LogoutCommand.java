package com.rookygod.simpleauthplus.commands;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /logout command
 */
public class LogoutCommand implements CommandExecutor {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for LogoutCommand
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public LogoutCommand(SimpleAuthPlus plugin) {
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
            player.sendMessage(plugin.getConfigManager().getMessage("logout.not-logged-in"));
            return true;
        }

        // Remove session
        plugin.getSessionManager().removeSession(player);
        
        // Add player to limbo
        plugin.getLimboManager().addPlayerToLimbo(player);
        
        player.sendMessage(plugin.getConfigManager().getMessage("logout.success"));
        return true;
    }
}

