package com.rookygod.simpleauthplus.data;

import com.rookygod.simpleauthplus.SimpleAuthPlus;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player limbo state
 */
public class LimboManager {
    private final SimpleAuthPlus plugin;
    private final Map<UUID, LimboData> limboPlayers = new HashMap<>();

    /**
     * Constructor for LimboManager
     *
     * @param plugin The SimpleAuthPlus plugin instance
     */
    public LimboManager(SimpleAuthPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Add a player to limbo
     *
     * @param player Player to add to limbo
     */
    public void addPlayerToLimbo(Player player) {
        if (!plugin.getConfigManager().isLimboEnabled()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Store player's current state
        LimboData data = new LimboData(
            player.getLocation(),
            player.getGameMode(),
            player.getAllowFlight(),
            player.isFlying(),
            player.getWalkSpeed(),
            player.getFlySpeed()
        );
        
        limboPlayers.put(uuid, data);
        
        // Apply limbo restrictions
        player.setWalkSpeed(0.0f);
        player.setFlySpeed(0.0f);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.ADVENTURE);
        
        // Hide player if enabled
        if (plugin.getConfigManager().isHidePlayersEnabled()) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.equals(player) && plugin.getSessionManager().hasSession(onlinePlayer)) {
                    onlinePlayer.hidePlayer(plugin, player);
                    player.hidePlayer(plugin, onlinePlayer);
                }
            }
        }
    }

    /**
     * Remove a player from limbo
     *
     * @param player Player to remove from limbo
     */
    public void removePlayerFromLimbo(Player player) {
        if (!plugin.getConfigManager().isLimboEnabled()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        LimboData data = limboPlayers.remove(uuid);
        
        if (data == null) {
            return;
        }
        
        // Restore player's state
        player.setWalkSpeed(data.getWalkSpeed());
        player.setFlySpeed(data.getFlySpeed());
        player.setAllowFlight(data.isAllowFlight());
        player.setFlying(data.isFlying());
        player.setGameMode(data.getGameMode());
        
        // Teleport player if enabled
        if (plugin.getConfigManager().isTeleportOnLoginEnabled()) {
            if (plugin.getConfigManager().isTeleportToLastLocationEnabled()) {
                player.teleport(data.getLocation());
            } else {
                player.teleport(plugin.getConfigManager().getSpawnLocation());
            }
        }
        
        // Show player if they were hidden
        if (plugin.getConfigManager().isHidePlayersEnabled()) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.equals(player)) {
                    onlinePlayer.showPlayer(plugin, player);
                    player.showPlayer(plugin, onlinePlayer);
                }
            }
        }
    }

    /**
     * Check if a player is in limbo
     *
     * @param player Player to check
     * @return true if the player is in limbo, false otherwise
     */
    public boolean isPlayerInLimbo(Player player) {
        return limboPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Get a player's limbo data
     *
     * @param player Player to get limbo data for
     * @return The player's limbo data, or null if not in limbo
     */
    public LimboData getPlayerLimboData(Player player) {
        return limboPlayers.get(player.getUniqueId());
    }

    /**
     * Clear all limbo players
     */
    public void clearLimboPlayers() {
        limboPlayers.clear();
    }

    /**
     * Class to store player limbo data
     */
    public static class LimboData {
        private final Location location;
        private final GameMode gameMode;
        private final boolean allowFlight;
        private final boolean flying;
        private final float walkSpeed;
        private final float flySpeed;

        /**
         * Constructor for LimboData
         *
         * @param location Player's location
         * @param gameMode Player's game mode
         * @param allowFlight Whether the player can fly
         * @param flying Whether the player is flying
         * @param walkSpeed Player's walk speed
         * @param flySpeed Player's fly speed
         */
        public LimboData(Location location, GameMode gameMode, boolean allowFlight, boolean flying, float walkSpeed, float flySpeed) {
            this.location = location;
            this.gameMode = gameMode;
            this.allowFlight = allowFlight;
            this.flying = flying;
            this.walkSpeed = walkSpeed;
            this.flySpeed = flySpeed;
        }

        /**
         * Get the player's location
         *
         * @return The player's location
         */
        public Location getLocation() {
            return location;
        }

        /**
         * Get the player's game mode
         *
         * @return The player's game mode
         */
        public GameMode getGameMode() {
            return gameMode;
        }

        /**
         * Check if the player can fly
         *
         * @return true if the player can fly, false otherwise
         */
        public boolean isAllowFlight() {
            return allowFlight;
        }

        /**
         * Check if the player is flying
         *
         * @return true if the player is flying, false otherwise
         */
        public boolean isFlying() {
            return flying;
        }

        /**
         * Get the player's walk speed
         *
         * @return The player's walk speed
         */
        public float getWalkSpeed() {
            return walkSpeed;
        }

        /**
         * Get the player's fly speed
         *
         * @return The player's fly speed
         */
        public float getFlySpeed() {
            return flySpeed;
        }
    }
}

