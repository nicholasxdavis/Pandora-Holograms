package com.pandora.holograms.api;

import com.pandora.holograms.PandoraHologramsPlugin;
import com.pandora.holograms.hologram.Hologram;
import com.pandora.holograms.manager.HologramManager;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

/**
 * PandoraHolograms API - Simple and user-friendly API for creating and managing holograms
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class PandoraAPI {
    
    private static PandoraHologramsPlugin getPlugin() {
        return PandoraHologramsPlugin.getInstance();
    }
    
    /**
     * Create a new hologram at the specified location
     * 
     * @param name The unique name/ID of the hologram
     * @param location The location where the hologram should be created
     * @return The created Hologram object, or null if creation failed
     */
    public static Hologram createHologram(String name, Location location) {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return null;
        }
        return manager.createHologram(name, location);
    }
    
    /**
     * Create a new hologram with initial lines at the specified location
     * 
     * @param name The unique name/ID of the hologram
     * @param location The location where the hologram should be created
     * @param lines The initial lines of text for the hologram
     * @return The created Hologram object, or null if creation failed
     */
    public static Hologram createHologram(String name, Location location, List<String> lines) {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return null;
        }
        return manager.createHologram(name, location, lines);
    }
    
    /**
     * Get a hologram by its name
     * 
     * @param name The name/ID of the hologram
     * @return The Hologram object, or null if not found
     */
    public static Hologram getHologram(String name) {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return null;
        }
        return manager.getHologram(name);
    }
    
    /**
     * Delete a hologram permanently
     * 
     * @param name The name/ID of the hologram to delete
     * @return True if the hologram was deleted, false otherwise
     */
    public static boolean deleteHologram(String name) {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return false;
        }
        boolean deleted = manager.deleteHologram(name);
        if (deleted) {
            getPlugin().getStorage().deleteHologram(name);
        }
        return deleted;
    }
    
    /**
     * Check if a hologram exists
     * 
     * @param name The name/ID of the hologram to check
     * @return True if the hologram exists, false otherwise
     */
    public static boolean hologramExists(String name) {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return false;
        }
        return manager.exists(name);
    }
    
    /**
     * Get all holograms
     * 
     * @return A collection of all holograms
     */
    public static Collection<Hologram> getAllHolograms() {
        HologramManager manager = getPlugin().getHologramManager();
        if (manager == null) {
            return new java.util.ArrayList<>();
        }
        return manager.getAllHolograms();
    }
    
    /**
     * Move a hologram to a new location
     * 
     * @param name The name/ID of the hologram
     * @param location The new location
     * @return True if the hologram was moved, false otherwise
     */
    public static boolean moveHologram(String name, Location location) {
        Hologram hologram = getHologram(name);
        if (hologram == null) {
            return false;
        }
        hologram.setLocation(location);
        getPlugin().getHologramManager().saveHologram(name);
        return true;
    }

}



