package com.pandora.holograms.manager;

import com.pandora.holograms.PandoraHologramsPlugin;
import com.pandora.holograms.hologram.Hologram;
import com.pandora.holograms.storage.HologramStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages all holograms in the plugin.
 * Handles creation, deletion, loading, and saving of holograms.
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class HologramManager {
    
    private final PandoraHologramsPlugin plugin;
    private final HologramStorage storage;
    private final Map<String, Hologram> holograms;
    
    public HologramManager(PandoraHologramsPlugin plugin, HologramStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.holograms = new HashMap<>();
    }
    
    public Hologram createHologram(String name, Location location) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (location == null || location.getWorld() == null) {
            plugin.getLogger().warning("Cannot create hologram '" + name + "' - invalid location!");
            return null;
        }
        if (holograms.containsKey(name.toLowerCase())) {
            return null;
        }
        
        Hologram hologram = new Hologram(name, location);
        holograms.put(name.toLowerCase(), hologram);
        hologram.spawn();
        return hologram;
    }
    
    public Hologram createHologram(String name, Location location, List<String> lines) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (location == null || location.getWorld() == null) {
            plugin.getLogger().warning("Cannot create hologram '" + name + "' - invalid location!");
            return null;
        }
        if (holograms.containsKey(name.toLowerCase())) {
            return null;
        }
        if (lines == null) {
            lines = new ArrayList<>();
        }
        
        Hologram hologram = new Hologram(name, location, lines);
        holograms.put(name.toLowerCase(), hologram);
        hologram.spawn();
        return hologram;
    }
    
    public void addHologram(Hologram hologram) {
        holograms.put(hologram.getName().toLowerCase(), hologram);
    }
    
    public Hologram getHologram(String name) {
        return holograms.get(name.toLowerCase());
    }
    
    public boolean exists(String name) {
        return holograms.containsKey(name.toLowerCase());
    }
    
    public boolean deleteHologram(String name) {
        Hologram hologram = holograms.remove(name.toLowerCase());
        if (hologram != null) {
            hologram.despawn();
            return true;
        }
        return false;
    }
    
    public Collection<Hologram> getAllHolograms() {
        return new ArrayList<>(holograms.values());
    }
    
    public void removeAll() {
        for (Hologram hologram : holograms.values()) {
            hologram.despawn();
        }
        holograms.clear();
    }
    
    public void loadAll() {
        storage.loadAll(this);
        
        // Spawn all loaded holograms on next tick (after worlds are fully loaded)
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Hologram hologram : holograms.values()) {
                if (hologram.getLocation().getWorld() != null) {
                    hologram.spawn();
                }
            }
            plugin.getLogger().info("Loaded " + holograms.size() + " hologram(s)!");
        });
    }
    
    public void saveAll() {
        storage.saveAll(holograms.values());
        plugin.getLogger().info("Saved " + holograms.size() + " hologram(s)!");
    }
    
    public void saveHologram(String name) {
        Hologram hologram = getHologram(name);
        if (hologram != null) {
            storage.saveHologram(hologram);
        }
    }

}

