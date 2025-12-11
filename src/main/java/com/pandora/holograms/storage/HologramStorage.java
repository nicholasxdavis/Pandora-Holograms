package com.pandora.holograms.storage;

import com.pandora.holograms.PandoraHologramsPlugin;
import com.pandora.holograms.hologram.Hologram;
import com.pandora.holograms.manager.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles storage and retrieval of holograms from YAML files.
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class HologramStorage {
    
    private final PandoraHologramsPlugin plugin;
    private File hologramsFile;
    private FileConfiguration hologramsConfig;
    
    public HologramStorage(PandoraHologramsPlugin plugin) {
        this.plugin = plugin;
        this.hologramsFile = new File(plugin.getDataFolder(), "holograms.yml");
        
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        if (!hologramsFile.exists()) {
            try {
                hologramsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create holograms.yml file!");
                e.printStackTrace();
            }
        }
        
        this.hologramsConfig = YamlConfiguration.loadConfiguration(hologramsFile);
    }
    
    public void loadAll(HologramManager manager) {
        if (hologramsConfig.getConfigurationSection("holograms") == null) {
            return;
        }
        
        for (String name : hologramsConfig.getConfigurationSection("holograms").getKeys(false)) {
            String path = "holograms." + name;
            
            String worldName = hologramsConfig.getString(path + ".world");
            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                plugin.getLogger().warning("World '" + worldName + "' for hologram '" + name + "' does not exist! Skipping...");
                continue;
            }
            
            double x = hologramsConfig.getDouble(path + ".x");
            double y = hologramsConfig.getDouble(path + ".y");
            double z = hologramsConfig.getDouble(path + ".z");
            float yaw = (float) hologramsConfig.getDouble(path + ".yaw", 0.0);
            float pitch = (float) hologramsConfig.getDouble(path + ".pitch", 0.0);
            
            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            // Ensure pitch and yaw are 0 to prevent upside-down holograms
            location.setPitch(0.0f);
            location.setYaw(0.0f);
            
            List<String> lines = hologramsConfig.getStringList(path + ".lines");
            if (lines == null) {
                lines = new ArrayList<>();
            }
            
            if (lines.isEmpty()) {
                lines.add("&7Empty Hologram");
            }
            
            Hologram hologram = new Hologram(name, location, lines);
            
            // Load optional fields
            if (hologramsConfig.contains(path + ".permission")) {
                String permission = hologramsConfig.getString(path + ".permission");
                if (permission != null && !permission.isEmpty()) {
                    hologram.setPermission(permission);
                }
            }
            
            if (hologramsConfig.contains(path + ".line-height")) {
                double lineHeight = hologramsConfig.getDouble(path + ".line-height");
                hologram.setLineHeight(lineHeight);
            }
            
            manager.addHologram(hologram);
        }
    }
    
    public void saveAll(Collection<Hologram> holograms) {
        hologramsConfig.set("holograms", null);
        
        for (Hologram hologram : holograms) {
            String path = "holograms." + hologram.getName();
            Location loc = hologram.getLocation();
            
            hologramsConfig.set(path + ".world", loc.getWorld().getName());
            hologramsConfig.set(path + ".x", loc.getX());
            hologramsConfig.set(path + ".y", loc.getY());
            hologramsConfig.set(path + ".z", loc.getZ());
            hologramsConfig.set(path + ".yaw", 0.0); // Always save as 0
            hologramsConfig.set(path + ".pitch", 0.0); // Always save as 0
            hologramsConfig.set(path + ".lines", hologram.getLines());
            
            // Save optional fields
            if (hologram.getPermission() != null && !hologram.getPermission().isEmpty()) {
                hologramsConfig.set(path + ".permission", hologram.getPermission());
            }
            
            // Only save line height if it's different from default
            double defaultHeight = plugin.getConfig().getDouble("default-line-height", 0.3);
            if (Math.abs(hologram.getLineHeight() - defaultHeight) > 0.001) {
                hologramsConfig.set(path + ".line-height", hologram.getLineHeight());
            }
        }
        
        try {
            hologramsConfig.save(hologramsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save holograms.yml file!");
            e.printStackTrace();
        }
    }
    
    public void saveHologram(Hologram hologram) {
        String path = "holograms." + hologram.getName();
        Location loc = hologram.getLocation();
        
        hologramsConfig.set(path + ".world", loc.getWorld().getName());
        hologramsConfig.set(path + ".x", loc.getX());
        hologramsConfig.set(path + ".y", loc.getY());
        hologramsConfig.set(path + ".z", loc.getZ());
        hologramsConfig.set(path + ".yaw", 0.0); // Always save as 0
        hologramsConfig.set(path + ".pitch", 0.0); // Always save as 0
        hologramsConfig.set(path + ".lines", hologram.getLines());
        
        // Save optional fields
        if (hologram.getPermission() != null && !hologram.getPermission().isEmpty()) {
            hologramsConfig.set(path + ".permission", hologram.getPermission());
        }
        
        // Only save line height if it's different from default
        double defaultHeight = plugin.getConfig().getDouble("default-line-height", 0.3);
        if (Math.abs(hologram.getLineHeight() - defaultHeight) > 0.001) {
            hologramsConfig.set(path + ".line-height", hologram.getLineHeight());
        }
        
        try {
            hologramsConfig.save(hologramsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save hologram '" + hologram.getName() + "'!");
            e.printStackTrace();
        }
    }
    
    public void deleteHologram(String name) {
        hologramsConfig.set("holograms." + name, null);
        
        try {
            hologramsConfig.save(hologramsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to delete hologram '" + name + "' from storage!");
            e.printStackTrace();
        }
    }

}

