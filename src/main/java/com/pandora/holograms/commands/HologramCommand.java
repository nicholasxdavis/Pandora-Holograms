package com.pandora.holograms.commands;

import com.pandora.holograms.hologram.Hologram;
import com.pandora.holograms.manager.HologramManager;
import com.pandora.holograms.PandoraHologramsPlugin;
import com.pandora.holograms.util.BrowserUtil;
import com.pandora.holograms.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HologramCommand implements CommandExecutor, TabCompleter {
    
    private final PandoraHologramsPlugin plugin;
    private final HologramManager manager;
    
    public HologramCommand(PandoraHologramsPlugin plugin, HologramManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("help"))) {
            sendHelp(sender);
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.formatError("This command can only be used by players!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                handleHelp(player, args);
                break;
            case "create":
                handleCreate(player, args);
                break;
            case "addline":
                handleAddLine(player, args);
                break;
            case "setline":
                handleSetLine(player, args);
                break;
            case "removeline":
                handleRemoveLine(player, args);
                break;
            case "movehere":
                handleMoveHere(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "list":
                handleList(player, args);
                break;
            case "reload":
                handleReload(player, args);
                break;
            case "live":
                handleLive(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "teleport":
            case "tp":
                handleTeleport(player, args);
                break;
            case "clone":
            case "copy":
                handleClone(player, args);
                break;
            case "insertline":
            case "insert":
                handleInsertLine(player, args);
                break;
            case "update":
                handleUpdate(player, args);
                break;
            case "version":
            case "ver":
                handleVersion(player, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.create")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to create holograms!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo create <name> [lines...]"));
            return;
        }
        
        String name = args[1];
        
        // Validate hologram name
        if (!isValidHologramName(name)) {
            player.sendMessage(MessageUtil.formatError("Invalid hologram name! Use only letters, numbers, underscores, and hyphens."));
            return;
        }
        
        if (manager.exists(name)) {
            player.sendMessage(MessageUtil.formatError("A hologram named &6" + name + " &calready exists!"));
            return;
        }
        
        Location location = player.getLocation();
        List<String> lines = new ArrayList<>();
        
        // Parse lines from arguments (each quoted string is a separate line)
        if (args.length > 2) {
            StringBuilder currentLine = new StringBuilder();
            boolean inQuotes = false;
            
            for (int i = 2; i < args.length; i++) {
                String arg = args[i];
                
                if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() >= 2) {
                    // Complete quoted string in one argument
                    String content = arg.substring(1, arg.length() - 1);
                    lines.add(content.isEmpty() ? " " : content); // Preserve empty quotes as space
                } else if (arg.startsWith("\"")) {
                    // Start of multi-word quoted string
                    inQuotes = true;
                    currentLine = new StringBuilder(arg.substring(1));
                } else if (inQuotes) {
                    if (arg.endsWith("\"")) {
                        // End of multi-word quoted string
                        currentLine.append(" ").append(arg.substring(0, arg.length() - 1));
                        String content = currentLine.toString();
                        lines.add(content.isEmpty() ? " " : content);
                        currentLine = new StringBuilder();
                        inQuotes = false;
                    } else {
                        // Middle of multi-word quoted string
                        currentLine.append(" ").append(arg);
                    }
                } else {
                    // Regular unquoted argument
                    lines.add(arg);
                }
            }
            
            // Handle unclosed quote
            if (inQuotes && currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }
        
        if (lines.isEmpty()) {
            lines.add(MessageUtil.TEXT + "Empty Hologram");
        }
        
        Hologram hologram = manager.createHologram(name, location, lines);
        if (hologram != null) {
            manager.saveHologram(name);
            player.sendMessage(MessageUtil.formatSuccess("Created hologram &6" + name + " &ewith &6" + lines.size() + " &eline(s)!"));
        } else {
            player.sendMessage(MessageUtil.formatError("Failed to create hologram!"));
        }
    }
    
    private void handleAddLine(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to edit holograms!"));
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo addline <name> <text>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) text.append(" ");
            text.append(args[i]);
        }
        
        hologram.addLine(text.toString());
        manager.saveHologram(name);
        player.sendMessage(MessageUtil.formatSuccess("Added line to hologram &6" + name + "&e!"));
    }
    
    private void handleSetLine(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to edit holograms!"));
            return;
        }
        
        if (args.length < 4) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo setline <name> <index> <text>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        try {
            int index = Integer.parseInt(args[2]) - 1; // Convert to 0-based index
            
            if (index < 0 || index >= hologram.getLines().size()) {
                player.sendMessage(MessageUtil.formatError("Invalid line index! Hologram has &6" + hologram.getLines().size() + " &cline(s)."));
                return;
            }
            
            StringBuilder text = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (i > 3) text.append(" ");
                text.append(args[i]);
            }
            
            hologram.setLine(index, text.toString());
            manager.saveHologram(name);
            player.sendMessage(MessageUtil.formatSuccess("Set line &6" + (index + 1) + " &eof hologram &6" + name + "&e!"));
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtil.formatError("Invalid line number: &6" + args[2] + "&c!"));
        }
    }
    
    private void handleRemoveLine(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to edit holograms!"));
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo removeline <name> <index>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        try {
            int index = Integer.parseInt(args[2]) - 1; // Convert to 0-based index
            
            if (index < 0 || index >= hologram.getLines().size()) {
                player.sendMessage(MessageUtil.formatError("Invalid line index! Hologram has &6" + hologram.getLines().size() + " &cline(s)."));
                return;
            }
            
            hologram.removeLine(index);
            manager.saveHologram(name);
            player.sendMessage(MessageUtil.formatSuccess("Removed line &6" + (index + 1) + " &efrom hologram &6" + name + "&e!"));
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtil.formatError("Invalid line number: &6" + args[2] + "&c!"));
        }
    }
    
    private void handleMoveHere(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to edit holograms!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo movehere <name>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        hologram.setLocation(player.getLocation());
        manager.saveHologram(name);
        player.sendMessage(MessageUtil.formatSuccess("Moved hologram &6" + name + " &eto your location!"));
    }
    
    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.delete")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to delete holograms!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo delete <name>"));
            return;
        }
        
        String name = args[1];
        
        if (!manager.exists(name)) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        manager.deleteHologram(name);
        plugin.getStorage().deleteHologram(name);
        player.sendMessage(MessageUtil.formatSuccess("Deleted hologram &6" + name + "&e!"));
    }
    
    private void handleHelp(Player player, String[] args) {
        sendHelp(player);
    }
    
    private void handleList(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.use")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to list holograms!"));
            return;
        }
        
        Collection<Hologram> allHolograms = manager.getAllHolograms();
        
        if (allHolograms.isEmpty()) {
            player.sendMessage(MessageUtil.formatText("There are no holograms created yet."));
            return;
        }
        
        player.sendMessage(MessageUtil.colorize("&e&lPandora Holograms&r&7"));
        player.sendMessage(MessageUtil.formatText("Hologram List &7(" + MessageUtil.VALUE + allHolograms.size() + MessageUtil.TEXT + "):"));
        int count = 1;
        for (Hologram hologram : allHolograms) {
            Location loc = hologram.getLocation();
            String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";
            player.sendMessage(MessageUtil.formatText("  " + count + ". &6" + hologram.getName() + 
                    " &7- " + worldName + " &8(" + 
                    String.format("%.1f", loc.getX()) + ", " + 
                    String.format("%.1f", loc.getY()) + ", " + 
                    String.format("%.1f", loc.getZ()) + ") &7- &6" + 
                    hologram.getLines().size() + " &7line(s)"));
            count++;
        }
    }
    
    private void handleReload(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.admin")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to reload the plugin!"));
            return;
        }
        
        player.sendMessage(MessageUtil.formatText("Reloading holograms..."));
        try {
            manager.saveAll();
            manager.removeAll();
            plugin.reloadConfig();
            manager.loadAll();
            player.sendMessage(MessageUtil.formatSuccess("Reloaded successfully! &7(&6" + manager.getAllHolograms().size() + " &7hologram(s) loaded)"));
        } catch (Exception e) {
            player.sendMessage(MessageUtil.formatError("Failed to reload! Check console for errors."));
            plugin.getLogger().severe("Failed to reload PandoraHolograms: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleLive(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.use")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to use this command!"));
            return;
        }
        
        // Run browser opening in async task to not block server thread
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                BrowserUtil.openEditor();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(MessageUtil.formatSuccess("Opening editor in browser..."));
                    player.sendMessage(MessageUtil.formatText("Editor location: &6" + BrowserUtil.getEditorPath()));
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(MessageUtil.formatError("Failed to open editor! Check console for details."));
                    plugin.getLogger().warning("Failed to open editor: " + e.getMessage());
                });
            }
        });
    }
    
    private void handleInfo(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.use")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to use this command!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo info <name>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        Location loc = hologram.getLocation();
        String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";
        
        player.sendMessage(MessageUtil.colorize("&e&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(MessageUtil.colorize("&e&lHologram Info: &6" + hologram.getName()));
        player.sendMessage(MessageUtil.colorize("&7"));
        player.sendMessage(MessageUtil.formatText("&7Location: &6" + worldName + " &8(" + 
                String.format("%.2f", loc.getX()) + ", " + 
                String.format("%.2f", loc.getY()) + ", " + 
                String.format("%.2f", loc.getZ()) + ")"));
        player.sendMessage(MessageUtil.formatText("&7Lines: &6" + hologram.getLineCount()));
        player.sendMessage(MessageUtil.formatText("&7Line Height: &6" + String.format("%.2f", hologram.getLineHeight())));
        player.sendMessage(MessageUtil.formatText("&7Spawned: &6" + (hologram.isSpawned() ? "Yes" : "No")));
        if (hologram.getPermission() != null && !hologram.getPermission().isEmpty()) {
            player.sendMessage(MessageUtil.formatText("&7Permission: &6" + hologram.getPermission()));
        }
        player.sendMessage(MessageUtil.colorize("&7"));
        player.sendMessage(MessageUtil.colorize("&e&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }
    
    private void handleTeleport(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.use")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to use this command!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo teleport <name>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        Location loc = hologram.getLocation().clone();
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());
        
        player.teleport(loc);
        player.sendMessage(MessageUtil.formatSuccess("Teleported to hologram &6" + name + "&e!"));
    }
    
    private void handleClone(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.create")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to clone holograms!"));
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo clone <source> <newName>"));
            return;
        }
        
        String sourceName = args[1];
        String newName = args[2];
        
        Hologram source = manager.getHologram(sourceName);
        if (source == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + sourceName + " &cdoes not exist!"));
            return;
        }
        
        if (!isValidHologramName(newName)) {
            player.sendMessage(MessageUtil.formatError("Invalid hologram name! Use only letters, numbers, underscores, and hyphens."));
            return;
        }
        
        if (manager.exists(newName)) {
            player.sendMessage(MessageUtil.formatError("A hologram named &6" + newName + " &calready exists!"));
            return;
        }
        
        Location newLocation = player.getLocation();
        
        // Use the clone method
        Hologram cloned = source.clone(newName, newLocation);
        manager.addHologram(cloned);
        cloned.spawn();
        manager.saveHologram(newName);
        
        player.sendMessage(MessageUtil.formatSuccess("Cloned hologram &6" + sourceName + " &eto &6" + newName + "&e!"));
    }
    
    private void handleInsertLine(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to edit holograms!"));
            return;
        }
        
        if (args.length < 4) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo insertline <name> <index> <text>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        try {
            int index = Integer.parseInt(args[2]) - 1; // Convert to 0-based index
            
            if (index < 0 || index > hologram.getLines().size()) {
                player.sendMessage(MessageUtil.formatError("Invalid line index! Must be between 1 and " + (hologram.getLines().size() + 1) + "."));
                return;
            }
            
            StringBuilder text = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (i > 3) text.append(" ");
                text.append(args[i]);
            }
            
            hologram.insertLine(index, text.toString());
            manager.saveHologram(name);
            player.sendMessage(MessageUtil.formatSuccess("Inserted line at position &6" + (index + 1) + " &ein hologram &6" + name + "&e!"));
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtil.formatError("Invalid line number: &6" + args[2] + "&c!"));
        }
    }
    
    private void handleUpdate(Player player, String[] args) {
        if (!player.hasPermission("pandoraholograms.edit")) {
            player.sendMessage(MessageUtil.formatError("You don't have permission to update holograms!"));
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage(MessageUtil.formatError("Usage: /holo update <name>"));
            return;
        }
        
        String name = args[1];
        Hologram hologram = manager.getHologram(name);
        
        if (hologram == null) {
            player.sendMessage(MessageUtil.formatError("Hologram &6" + name + " &cdoes not exist!"));
            return;
        }
        
        hologram.updateLines(player);
        player.sendMessage(MessageUtil.formatSuccess("Updated hologram &6" + name + "&e!"));
    }
    
    private void handleVersion(Player player, String[] args) {
        String version = plugin.getDescription().getVersion();
        player.sendMessage(MessageUtil.colorize("&e&lPandora Holograms &7v" + version));
        player.sendMessage(MessageUtil.formatText("&7By &6Blacnova Development"));
        player.sendMessage(MessageUtil.formatText("&7API Version: &6" + plugin.getDescription().getAPIVersion()));
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtil.colorize("&e&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage(MessageUtil.colorize("&e&lPandora Holograms &7v" + plugin.getDescription().getVersion()));
        sender.sendMessage(MessageUtil.colorize("&7"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo help &8- &7Show this help menu"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo create <name> [lines...] &8- &7Create a new hologram"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo addline <name> <text> &8- &7Add a line to a hologram"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo insertline <name> <index> <text> &8- &7Insert a line at position"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo setline <name> <index> <text> &8- &7Set a specific line"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo removeline <name> <index> &8- &7Remove a line"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo movehere <name> &8- &7Move hologram to you"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo teleport <name> &8- &7Teleport to hologram"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo clone <source> <newName> &8- &7Clone a hologram"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo info <name> &8- &7Show hologram information"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo update <name> &8- &7Update hologram display"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo delete <name> &8- &7Delete a hologram"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo list &8- &7List all holograms"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo live &8- &7Open the visual editor"));
        sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo version &8- &7Show plugin version"));
        if (sender.hasPermission("pandoraholograms.admin")) {
            sender.sendMessage(MessageUtil.formatText("  &e&l» &7/holo reload &8- &7Reload the plugin"));
        }
        sender.sendMessage(MessageUtil.colorize("&7"));
        sender.sendMessage(MessageUtil.formatText("  &7Tip: Use quotes for multi-word text: &6\"Hello World\""));
        sender.sendMessage(MessageUtil.colorize("&e&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("help", "create", "addline", "setline", "removeline", "movehere", "delete", "list", "live"));
            if (sender.hasPermission("pandoraholograms.admin")) {
                commands.add("reload");
            }
            String input = args[0].toLowerCase();
            for (String cmd : commands) {
                if (cmd.toLowerCase().startsWith(input)) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2) {
            // Tab complete hologram names for commands that need them
            String subCommand = args[0].toLowerCase();
            if (!subCommand.equals("create") && !subCommand.equals("help") && !subCommand.equals("version") && !subCommand.equals("live")) {
                for (Hologram hologram : manager.getAllHolograms()) {
                    if (hologram.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(hologram.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            // Tab complete for commands that need a second argument
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("clone")) {
                // Second arg is source hologram name
                for (Hologram hologram : manager.getAllHolograms()) {
                    if (hologram.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(hologram.getName());
                    }
                }
            } else if (subCommand.equals("setline") || subCommand.equals("removeline") || subCommand.equals("insertline")) {
                // Second arg is line number
                Hologram hologram = manager.getHologram(args[1]);
                if (hologram != null) {
                    int lineCount = hologram.getLineCount();
                    for (int i = 1; i <= lineCount; i++) {
                        String num = String.valueOf(i);
                        if (num.startsWith(args[2].toLowerCase())) {
                            completions.add(num);
                        }
                    }
                }
            }
        }
        
        return completions;
    }
    
    private boolean isValidHologramName(String name) {
        if (name == null || name.isEmpty() || name.length() > 32) {
            return false;
        }
        // Allow only letters, numbers, underscores, and hyphens
        return name.matches("^[a-zA-Z0-9_-]+$");
    }

}

