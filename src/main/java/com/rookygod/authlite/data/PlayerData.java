package com.rookygod.authlite.data;

import java.time.Instant;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final String username;
    private String password;
    private String lastIp;
    private long lastLogin;
    private int loginAttempts;
    
    public PlayerData(UUID uuid, String username, String password) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.lastIp = "";
        this.lastLogin = 0;
        this.loginAttempts = 0;
    }
    
    public PlayerData(UUID uuid, String username, String password, String lastIp, long lastLogin) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.lastIp = lastIp;
        this.lastLogin = lastLogin;
        this.loginAttempts = 0;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getLastIp() {
        return lastIp;
    }
    
    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }
    
    public long getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public void updateLastLogin() {
        this.lastLogin = Instant.now().getEpochSecond();
    }
    
    public int getLoginAttempts() {
        return loginAttempts;
    }
    
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
    
    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }
    
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }
}

