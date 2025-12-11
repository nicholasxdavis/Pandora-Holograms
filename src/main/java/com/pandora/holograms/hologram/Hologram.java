package com.pandora.holograms.hologram;

import com.pandora.holograms.PandoraHologramsPlugin;
import com.pandora.holograms.util.PlaceholderUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a hologram with multiple lines of text.
 * Holograms are displayed using invisible armor stands.
 * 
 * @author Blacnova Development
 * @version 1.0.0
 */
public class Hologram {
    
    private final String name;
    private Location location;
    private final List<String> lines;
    private final List<ArmorStand> armorStands;
    private final Set<UUID> viewers;
    private boolean spawned;
    private String permission;
    private double lineHeight;
    
    public Hologram(String name, Location location) {
        this.name = name;
        this.location = location.clone();
        this.location.setPitch(0.0f);
        this.location.setYaw(0.0f);
        this.lines = new ArrayList<>();
        this.armorStands = new ArrayList<>();
        this.viewers = new HashSet<>();
        this.spawned = false;
        this.permission = null;
        this.lineHeight = PandoraHologramsPlugin.getInstance().getConfig().getDouble("default-line-height", 0.3);
    }
    
    public Hologram(String name, Location location, List<String> lines) {
        this.name = name;
        this.location = location.clone();
        this.location.setPitch(0.0f);
        this.location.setYaw(0.0f);
        this.lines = new ArrayList<>(lines);
        this.armorStands = new ArrayList<>();
        this.viewers = new HashSet<>();
        this.spawned = false;
        this.permission = null;
        this.lineHeight = PandoraHologramsPlugin.getInstance().getConfig().getDouble("default-line-height", 0.3);
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return location.clone();
    }
    
    public void setLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        this.location = location.clone();
        this.location.setPitch(0.0f);
        this.location.setYaw(0.0f);
        if (spawned) {
            respawn();
        }
    }
    
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
    
    public void addLine(String text) {
        lines.add(text);
        if (spawned) {
            respawn();
        }
    }
    
    public void setLine(int index, String text) {
        if (index < 0 || index >= lines.size()) {
            return;
        }
        lines.set(index, text);
        if (spawned) {
            respawn();
        }
    }
    
    public void removeLine(int index) {
        if (index < 0 || index >= lines.size()) {
            return;
        }
        lines.remove(index);
        if (spawned) {
            respawn();
        }
    }
    
    public void insertLine(int index, String text) {
        if (index < 0 || index > lines.size()) {
            return;
        }
        lines.add(index, text);
        if (spawned) {
            respawn();
        }
    }
    
    public void spawn(Player player) {
        if (spawned) {
            return;
        }
        
        if (location == null || location.getWorld() == null) {
            PandoraHologramsPlugin.getInstance().getLogger().warning("Cannot spawn hologram '" + name + "' - location or world is null!");
            return;
        }
        
        Location loc = location.clone();
        // CRITICAL: Set pitch and yaw to 0 to prevent upside-down holograms
        loc.setPitch(0.0f);
        loc.setYaw(0.0f);
        
        if (lines.isEmpty()) {
            PandoraHologramsPlugin.getInstance().getLogger().warning("Cannot spawn hologram '" + name + "' - no lines defined!");
            return;
        }
        
        // Location is where the BOTTOM line appears
        // First line in list (index 0) should appear at TOP
        // Last line in list (index N) should appear at BOTTOM (base location)
        // We calculate positions for all lines, placing them correctly
        
        // Iterate forward through lines, but place them from top to bottom
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // Preserve empty lines - if null or empty, use a single space for visibility
            if (line == null) {
                line = " ";
            } else if (line.trim().isEmpty()) {
                // Empty string or whitespace - use space for proper spacing
                line = " ";
            }
            
            // Calculate position: first line (i=0) goes at top, last line goes at bottom
            // Line 0 should be at: base + (total lines - 1) * height
            // Line N should be at: base
            double yOffset = (lines.size() - 1 - i) * this.lineHeight;
            Location lineLoc = loc.clone().add(0, yOffset, 0);
            // Ensure pitch and yaw are 0 for each line location
            lineLoc.setPitch(0.0f);
            lineLoc.setYaw(0.0f);
            
            ArmorStand stand = (ArmorStand) lineLoc.getWorld().spawnEntity(lineLoc, EntityType.ARMOR_STAND);
            
            // Set all armor stand properties BEFORE making it invisible
            stand.setGravity(false);
            stand.setMarker(true);
            stand.setSmall(true);
            stand.setInvulnerable(true);
            stand.setCanPickupItems(false);
            stand.setCollidable(false);
            stand.setBasePlate(false);
            stand.setArms(false);
            // CRITICAL: Set head pose to prevent rotation issues
            stand.setHeadPose(new EulerAngle(0, 0, 0));
            stand.setBodyPose(new EulerAngle(0, 0, 0));
            stand.setLeftArmPose(new EulerAngle(0, 0, 0));
            stand.setRightArmPose(new EulerAngle(0, 0, 0));
            stand.setLeftLegPose(new EulerAngle(0, 0, 0));
            stand.setRightLegPose(new EulerAngle(0, 0, 0));
            
            // Set visibility and name AFTER all properties
            stand.setVisible(false);
            stand.setCustomNameVisible(true);
            // Parse color codes and placeholders
            String displayText = parseText(line, null);
            stand.setCustomName(displayText);
            
            armorStands.add(stand);
        }
        
        spawned = true;
    }
    
    public void spawn() {
        spawn(null);
    }
    
    public void despawn() {
        if (!spawned) {
            return;
        }
        
        for (ArmorStand stand : armorStands) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        armorStands.clear();
        spawned = false;
    }
    
    public void respawn() {
        despawn();
        spawn();
    }
    
    public boolean isSpawned() {
        return spawned;
    }
    
    public void show(Player player) {
        if (player == null) return;
        viewers.add(player.getUniqueId());
        if (!spawned) {
            spawn();
        }
        // Armor stands are visible to all players by default
        // Future: Could implement packet-based per-player visibility
    }
    
    public void hide(Player player) {
        if (player == null) return;
        viewers.remove(player.getUniqueId());
        // Armor stands are visible to all players by default
        // Future: Could implement packet-based per-player visibility
    }
    
    public boolean isVisibleTo(Player player) {
        return player != null && viewers.contains(player.getUniqueId());
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public boolean hasPermission(Player player) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        return player.hasPermission(permission);
    }
    
    public double getLineHeight() {
        return lineHeight;
    }
    
    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
        if (spawned) {
            respawn();
        }
    }
    
    public void updateLines() {
        updateLines(null);
    }
    
    public void updateLines(Player player) {
        if (!spawned || armorStands.size() != lines.size()) {
            respawn();
            return;
        }
        
        for (int i = 0; i < lines.size() && i < armorStands.size(); i++) {
            ArmorStand stand = armorStands.get(i);
            if (stand != null && !stand.isDead()) {
                String line = lines.get(i);
                if (line == null || line.trim().isEmpty()) {
                    line = " ";
                }
                String displayText = parseText(line, player);
                stand.setCustomName(displayText);
            }
        }
    }
    
    /**
     * Parse text with color codes and placeholders
     */
    private String parseText(String text, Player player) {
        if (text == null) {
            return " ";
        }
        
        // Replace color codes
        text = text.replace('&', '§');
        
        // Parse PlaceholderAPI placeholders if player is provided
        if (player != null && PlaceholderUtil.containsPlaceholders(text)) {
            text = PlaceholderUtil.parsePlaceholders(text, player);
        }
        
        return text;
    }
    
    public int getLineCount() {
        return lines.size();
    }
    
    public void clearLines() {
        lines.clear();
        if (spawned) {
            respawn();
        }
    }
    
    /**
     * Create a clone of this hologram at a new location
     * 
     * @param newName The name for the cloned hologram
     * @param newLocation The location for the cloned hologram
     * @return A new Hologram instance with the same lines and properties
     */
    public Hologram clone(String newName, Location newLocation) {
        Hologram clone = new Hologram(newName, newLocation, new ArrayList<>(lines));
        clone.setLineHeight(this.lineHeight);
        clone.setPermission(this.permission);
        return clone;
    }

}

