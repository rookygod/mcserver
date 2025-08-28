package com.rookygod.authlite.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class LoginEvent extends AuthenticationEvent {

    private static final HandlerList handlers = new HandlerList();
    
    public LoginEvent(Player player) {
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

