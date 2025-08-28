package com.rookygod.authlite.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class UnregisterEvent extends AuthenticationEvent {

    private static final HandlerList handlers = new HandlerList();
    
    public UnregisterEvent(Player player) {
        super(player);
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

