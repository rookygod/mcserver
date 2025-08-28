package com.rookygod.simpleauthplus.commands;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor for the /login command
 */
public class LoginCommand implements CommandExecutor {
    private final SimpleAuthPlus plugin;

    /**
     * Constructor for LoginCommand
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public LoginCommand(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.player-only"));
            return true;
        }

        // Check if player is not registered
        if (!plugin.getUserDataManager().isPlayerRegistered(player.getName())) {
            player.sendMessage(plugin.getConfigManager().getMessage("login.not-registered"));
            return true;
        }

        // Check if player is already authenticated
        if (plugin.getSessionManager().hasSession(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("login.already-logged-in"));
            return true;
        }

        // Check if password was provided
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("login.usage"));
            return true;
        }

        String password = args[0];
        String playerName = player.getName();

        // Check if captcha is required
        if (plugin.getSessionManager().needsCaptcha(playerName)) {
            // Check if captcha was provided
            if (args.length < 2) {
                String captcha = plugin.getSessionManager().getCaptcha(playerName);
                if (captcha == null) {
                    captcha = plugin.getSessionManager().generateCaptcha(playerName);
                }
                
                player.sendMessage(plugin.getConfigManager().getMessage("captcha.required")
                        .replace("{captcha}", captcha));
                return true;
            }
            
            // Verify captcha
            String captcha = args[1];
            if (!plugin.getSessionManager().verifyCaptcha(playerName, captcha)) {
                player.sendMessage(plugin.getConfigManager().getMessage("captcha.wrong"));
                return true;
            }
            
            // Captcha verified
            player.sendMessage(plugin.getConfigManager().getMessage("captcha.success"));
        }

        // Check password
        if (plugin.getUserDataManager().checkPassword(playerName, password)) {
            // Create session for the player
            plugin.getSessionManager().createSession(player);
            
            // Update last login information
            plugin.getUserDataManager().updateLastLogin(player);
            
            // Remove player from limbo
            plugin.getLimboManager().removePlayerFromLimbo(player);
            
            player.sendMessage(plugin.getConfigManager().getMessage("login.success"));
        } else {
            // Increment login attempts
            int attempts = plugin.getSessionManager().incrementLoginAttempts(playerName);
            int maxAttempts = plugin.getConfigManager().getMaxLoginAttempts();
            
            // Check if player should be kicked
            if (attempts >= maxAttempts) {
                player.kickPlayer(plugin.getConfigManager().getMessageNoPrefix("login.too-many-attempts"));
                return true;
            }
            
            // Send wrong password message
            player.sendMessage(plugin.getConfigManager().getMessage("login.wrong-password")
                    .replace("{attempts}", String.valueOf(attempts))
                    .replace("{max_attempts}", String.valueOf(maxAttempts)));
            
            // Generate captcha if needed
            if (plugin.getSessionManager().needsCaptcha(playerName)) {
                String captcha = plugin.getSessionManager().generateCaptcha(playerName);
                player.sendMessage(plugin.getConfigManager().getMessage("captcha.required")
                        .replace("{captcha}", captcha));
            }
        }

        return true;
    }
}

