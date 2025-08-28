package com.rookygod.simpleauthplus.commands;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /authreload command
 */
public class ReloadCommand implements CommandExecutor {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for ReloadCommand
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public ReloadCommand(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if sender has permission
        if (!sender.hasPermission("authme.admin.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.no-permission"));
            return true;
        }

        try {
            // Reload configuration
            plugin.getConfigManager().reloadConfig();
            
            // Reload user data
            plugin.getUserDataManager().loadUsersConfig();
            
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.reload-success"));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.reload-failed"));
            plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}

