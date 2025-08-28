package com.rookygod.authlite.commands;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.events.RegisterEvent;
import com.rookygod.authlite.events.UnregisterEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnregisterCommand implements CommandExecutor {

    private final AuthLite plugin;
    private final Map<UUID, BukkitTask> registrationPromptTasks = new HashMap<>();
    
    public UnregisterCommand(AuthLite plugin) {
        this.plugin = plugin;
        
        // Register player quit event to clean up tasks
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                if (registrationPromptTasks.containsKey(uuid)) {
                    registrationPromptTasks.get(uuid).cancel();
                    registrationPromptTasks.remove(uuid);
                }
            }
            
            @EventHandler
            public void onPlayerRegister(RegisterEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                if (registrationPromptTasks.containsKey(uuid)) {
                    registrationPromptTasks.get(uuid).cancel();
                    registrationPromptTasks.remove(uuid);
                }
            }
        }, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Hide sensitive command from console logs
        if (plugin.getConfigManager().isHideSensitiveCommands()) {
            plugin.getLogger().info(sender.getName() + " issued a sensitive command (password hidden)");
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players");
            return true;
        }
        
        // Check if player has permission
        if (!player.hasPermission("authlite.unregister")) {
            plugin.getMessageManager().sendMessage(player, "protection.no_permission");
            return true;
        }
        
        // Check if player is registered
        if (!plugin.getDataManager().isRegistered(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(player, "unregister.not_registered");
            return true;
        }
        
        // Check command usage
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(player, "unregister.usage");
            return true;
        }
        
        String password = args[0];
        
        // Check if password is correct
        if (!plugin.getDataManager().authenticate(player, password)) {
            plugin.getMessageManager().sendMessage(player, "unregister.wrong_password");
            return true;
        }
        
        // Unregister player
        if (plugin.getDataManager().unregisterPlayer(player.getUniqueId())) {
            // Deauthenticate player
            plugin.getSessionManager().deauthenticatePlayer(player);
            
            // Send success message
            plugin.getMessageManager().sendMessage(player, "unregister.success");
            
            // Call unregister event
            UnregisterEvent unregisterEvent = new UnregisterEvent(player);
            plugin.getServer().getPluginManager().callEvent(unregisterEvent);
            
            // Start registration prompt task
            startRegistrationPromptTask(player);
            
            return true;
        } else {
            // This should not happen, but just in case
            plugin.getMessageManager().sendMessage(player, "unregister.not_registered");
            return true;
        }
    }
    
    /**
     * Starts a task to periodically remind the player to register after unregistering
     * 
     * @param player The player to remind
     */
    private void startRegistrationPromptTask(Player player) {
        // Only start if reminder interval is greater than 0
        int reminderInterval = plugin.getConfigManager().getPostUnregisterReminderInterval();
        if (reminderInterval <= 0) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Cancel any existing reminder task for this player
        if (registrationPromptTasks.containsKey(uuid)) {
            registrationPromptTasks.get(uuid).cancel();
            registrationPromptTasks.remove(uuid);
        }
        
        // Create a new reminder task
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            // Only send reminder if player is still online and not registered
            if (player.isOnline() && !plugin.getDataManager().isRegistered(player.getUniqueId())) {
                plugin.getMessageManager().sendMessage(player, "register.prompt_after_unregister");
            } else {
                // Cancel task if player is offline or registered
                if (registrationPromptTasks.containsKey(uuid)) {
                    registrationPromptTasks.get(uuid).cancel();
                    registrationPromptTasks.remove(uuid);
                }
            }
        }, reminderInterval * 20L, reminderInterval * 20L); // Convert seconds to ticks
        
        registrationPromptTasks.put(uuid, task);
    }
}
