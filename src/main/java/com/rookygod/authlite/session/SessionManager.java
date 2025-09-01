package com.rookygod.authlite.session;

import com.rookygod.authlite.AuthLite;
import com.rookygod.authlite.data.PlayerData;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionManager {

    private final AuthLite plugin;
    private final Set<UUID> authenticatedPlayers = new HashSet<>();
    
    public SessionManager(AuthLite plugin) {
        this.plugin = plugin;
    }
    
    public boolean isAuthenticated(Player player) {
        return authenticatedPlayers.contains(player.getUniqueId());
    }
    
    public void authenticatePlayer(Player player) {
        authenticatedPlayers.add(player.getUniqueId());
    }
    
    public void deauthenticatePlayer(Player player) {
        authenticatedPlayers.remove(player.getUniqueId());
    }
    
    public void clearSessions() {
        authenticatedPlayers.clear();
    }
    
    public boolean hasValidSession(Player player) {
        // Check if sessions are enabled
        if (!plugin.getConfigManager().isEnableSessions()) {
            return false;
        }
        
        // Check if player is registered
        if (!plugin.getDataManager().isRegistered(player.getUniqueId())) {
            return false;
        }
        
        // Get player data
        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        // Check if player has a last login time
        if (playerData.getLastLogin() == 0) {
            return false;
        }
        
        // Check if session has expired
        long sessionTimeoutSeconds = plugin.getConfigManager().getSessionTimeout() * 60L; // Convert minutes to seconds
        long currentTime = Instant.now().getEpochSecond();
        long lastLoginTime = playerData.getLastLogin();
        
        if (currentTime - lastLoginTime > sessionTimeoutSeconds) {
            return false;
        }
        
        // Check if IP matches
        if (plugin.getConfigManager().isSessionCheckIP()) {
            String lastIp = playerData.getLastIp();
            String currentIp = player.getAddress().getAddress().getHostAddress();
            
            if (!lastIp.equals(currentIp)) {
                return false;
            }
        }
        
        return true;
    }
}

