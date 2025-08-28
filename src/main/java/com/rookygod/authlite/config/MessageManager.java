package com.rookygod.authlite.config;

import com.rookygod.authlite.AuthLite;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final AuthLite plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final Map<String, String> messages = new HashMap<>();
    
    public MessageManager(AuthLite plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Load default messages from jar
        InputStream defaultMessagesStream = plugin.getResource("messages.yml");
        if (defaultMessagesStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultMessagesStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultMessages);
        }
        
        // Load all messages into memory
        for (String key : messagesConfig.getKeys(true)) {
            if (messagesConfig.isString(key)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, "")));
            }
        }
    }
    
    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages.clear();
        
        // Load default messages from jar
        InputStream defaultMessagesStream = plugin.getResource("messages.yml");
        if (defaultMessagesStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultMessagesStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultMessages);
        }
        
        // Load all messages into memory
        for (String key : messagesConfig.getKeys(true)) {
            if (messagesConfig.isString(key)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(key, "")));
            }
        }
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "Missing message: " + key);
    }
    
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return message;
    }
    
    public void sendMessage(Player player, String key) {
        player.sendMessage(getMessage(key));
    }
    
    public void sendMessage(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(getMessage(key, placeholders));
    }
}

