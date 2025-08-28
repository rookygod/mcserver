package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthLiteCommand implements CommandExecutor {

    private final AuthLite plugin;
    
    public AuthLiteCommand(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has admin permission
        if (!sender.hasPermission("authlite.admin")) {
            if (sender instanceof Player player) {
                plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            } else {
                sender.sendMessage("You don't have permission to use this command");
            }
            return true;
        }
        
        // Check command usage
        if (args.length < 1) {
            if (sender instanceof Player player) {
                plugin.getMessageManager().sendMessage(player, "admin.usage");
            } else {
                sender.sendMessage("Usage: /authlite <reload|register|changepassword|unregister> [player] [password]");
            }
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload" -> {
                // Check if sender has reload permission
                if (!sender.hasPermission("authlite.admin.reload")) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "protection.no_permission");
                    } else {
                        sender.sendMessage("You don't have permission to use this command");
                    }
                    return true;
                }
                
                // Reload plugin
                plugin.reload();
                
                // Send success message
                if (sender instanceof Player player) {
                    plugin.getMessageManager().sendMessage(player, "admin.reload.success");
                } else {
                    sender.sendMessage("AuthLite has been reloaded!");
                }
                
                return true;
            }
            case "register" -> {
                // Check if sender has register permission
                if (!sender.hasPermission("authlite.admin.register")) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "protection.no_permission");
                    } else {
                        sender.sendMessage("You don't have permission to use this command");
                    }
                    return true;
                }
                
                // Check command usage
                if (args.length < 3) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "admin.register.usage");
                    } else {
                        sender.sendMessage("Usage: /authlite register <player> <password>");
                    }
                    return true;
                }
                
                String playerName = args[1];
                String password = args[2];
                
                // Get player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                UUID uuid = offlinePlayer.getUniqueId();
                
                // Check if player is already registered
                if (plugin.getDataManager().isRegistered(uuid)) {
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.register.already_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is already registered!");
                    }
                    return true;
                }
                
                // Register player
                if (plugin.getDataManager().registerPlayer(playerName, uuid, password)) {
                    // Send success message
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.register.success", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " has been registered!");
                    }
                    
                    return true;
                } else {
                    // This should not happen, but just in case
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.register.already_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is already registered!");
                    }
                    return true;
                }
            }
            case "changepassword" -> {
                // Check if sender has changepassword permission
                if (!sender.hasPermission("authlite.admin.changepassword")) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "protection.no_permission");
                    } else {
                        sender.sendMessage("You don't have permission to use this command");
                    }
                    return true;
                }
                
                // Check command usage
                if (args.length < 3) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "admin.change_password.usage");
                    } else {
                        sender.sendMessage("Usage: /authlite changepassword <player> <newPassword>");
                    }
                    return true;
                }
                
                String playerName = args[1];
                String newPassword = args[2];
                
                // Get player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                UUID uuid = offlinePlayer.getUniqueId();
                
                // Check if player is registered
                if (!plugin.getDataManager().isRegistered(uuid)) {
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.change_password.not_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is not registered!");
                    }
                    return true;
                }
                
                // Change password
                if (plugin.getDataManager().changePassword(uuid, newPassword)) {
                    // Send success message
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.change_password.success", placeholders);
                    } else {
                        sender.sendMessage("Password for player " + playerName + " has been changed!");
                    }
                    
                    return true;
                } else {
                    // This should not happen, but just in case
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.change_password.not_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is not registered!");
                    }
                    return true;
                }
            }
            case "unregister" -> {
                // Check if sender has unregister permission
                if (!sender.hasPermission("authlite.admin.unregister")) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "protection.no_permission");
                    } else {
                        sender.sendMessage("You don't have permission to use this command");
                    }
                    return true;
                }
                
                // Check command usage
                if (args.length < 2) {
                    if (sender instanceof Player player) {
                        plugin.getMessageManager().sendMessage(player, "admin.unregister.usage");
                    } else {
                        sender.sendMessage("Usage: /authlite unregister <player>");
                    }
                    return true;
                }
                
                String playerName = args[1];
                
                // Get player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                UUID uuid = offlinePlayer.getUniqueId();
                
                // Check if player is registered
                if (!plugin.getDataManager().isRegistered(uuid)) {
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.unregister.not_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is not registered!");
                    }
                    return true;
                }
                
                // Unregister player
                if (plugin.getDataManager().unregisterPlayer(uuid)) {
                    // Deauthenticate player if online
                    Player targetPlayer = Bukkit.getPlayer(uuid);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        plugin.getSessionManager().deauthenticatePlayer(targetPlayer);
                    }
                    
                    // Send success message
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.unregister.success", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " has been unregistered!");
                    }
                    
                    return true;
                } else {
                    // This should not happen, but just in case
                    if (sender instanceof Player player) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", playerName);
                        plugin.getMessageManager().sendMessage(player, "admin.unregister.not_registered", placeholders);
                    } else {
                        sender.sendMessage("Player " + playerName + " is not registered!");
                    }
                    return true;
                }
            }
            default -> {
                // Unknown subcommand
                if (sender instanceof Player player) {
                    plugin.getMessageManager().sendMessage(player, "admin.usage");
                } else {
                    sender.sendMessage("Usage: /authlite <reload|register|changepassword|unregister> [player] [password]");
                }
                return true;
            }
        }
    }
}

