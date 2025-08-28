package com.rookygod.simpleauthme.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages authenticated player sessions
 */
public class SessionManager {
    private final Set<UUID> authenticatedPlayers = new HashSet<>();

    /**
     * Authenticate a player
     *
     * @param uuid Player UUID to authenticate
     */
    public void authenticatePlayer(UUID uuid) {
        authenticatedPlayers.add(uuid);
    }

    /**
     * Remove a player's authentication
     *
     * @param uuid Player UUID to deauthenticate
     */
    public void deauthenticatePlayer(UUID uuid) {
        authenticatedPlayers.remove(uuid);
    }

    /**
     * Check if a player is authenticated
     *
     * @param uuid Player UUID to check
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }

    /**
     * Clear all authenticated players
     */
    public void clearAuthentications() {
        authenticatedPlayers.clear();
    }
}

