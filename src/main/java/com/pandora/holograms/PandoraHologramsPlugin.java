package com.pandora.holograms;

import com.pandora.holograms.commands.HologramCommand;
import com.pandora.holograms.hologram.Hologram;
import com.pandora.holograms.manager.HologramManager;
import com.pandora.holograms.storage.HologramStorage;
import com.pandora.holograms.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * PandoraHolograms - A high-performance, user-friendly hologram plugin
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class PandoraHologramsPlugin extends JavaPlugin {
    
    private static PandoraHologramsPlugin instance;
    private HologramManager hologramManager;
    private HologramStorage storage;
    private BukkitTask updateTask;
    
    @Override
    public void onLoad() {
        instance = this;
    }
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // Check for PlaceholderAPI
        com.pandora.holograms.util.PlaceholderUtil.checkPlaceholderAPI();
        if (com.pandora.holograms.util.PlaceholderUtil.isPlaceholderApiEnabled()) {
            getLogger().info("PlaceholderAPI found! Placeholder support enabled.");
        }
        
        this.storage = new HologramStorage(this);
        this.hologramManager = new HologramManager(this, storage);
        
        HologramCommand command = new HologramCommand(this, hologramManager);
        getCommand("holo").setExecutor(command);
        getCommand("holo").setTabCompleter(command);
        
        // Load all holograms from storage
        hologramManager.loadAll();
        
        // Start update task for PlaceholderAPI support
        startUpdateTask();
        
        getLogger().info("PandoraHolograms has been enabled!");
        getLogger().info("Loaded " + hologramManager.getAllHolograms().size() + " hologram(s)!");
    }
    
    private void startUpdateTask() {
        long interval = getConfig().getLong("update-interval", 20L);
        if (interval <= 0) {
            interval = 20L; // Default to 1 second
        }
        
        // Only start update task if PlaceholderAPI is enabled
        if (PlaceholderUtil.isPlaceholderApiEnabled()) {
            updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                // Update holograms for all online players
                Bukkit.getScheduler().runTask(this, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Hologram hologram : hologramManager.getAllHolograms()) {
                            if (hologram.isSpawned() && hologram.hasPermission(player)) {
                                hologram.updateLines(player);
                            }
                        }
                    }
                });
            }, interval, interval);
            
            getLogger().info("PlaceholderAPI update task started (interval: " + interval + " ticks)");
        }
    }
    
    @Override
    public void onDisable() {
        // Cancel update task
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        
        if (hologramManager != null) {
            hologramManager.saveAll();
            hologramManager.removeAll();
        }
        
        getLogger().info("PandoraHolograms has been disabled!");
    }
    
    public static PandoraHologramsPlugin getInstance() {
        return instance;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
    
    public HologramStorage getStorage() {
        return storage;
    }

}

