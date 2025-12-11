package com.pandora.holograms.util;

import com.pandora.holograms.PandoraHologramsPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class BrowserUtil {
    
    public static void openEditor() {
        PandoraHologramsPlugin plugin = PandoraHologramsPlugin.getInstance();
        
        // Get or create editor.html in plugin data folder
        File dataFolder = plugin.getDataFolder();
        File editorFile = new File(dataFolder, "editor.html");
        
        // Copy editor.html from resources if it doesn't exist or is outdated
        if (!editorFile.exists()) {
            dataFolder.mkdirs();
            try (InputStream is = plugin.getResource("editor.html")) {
                if (is != null) {
                    Files.copy(is, editorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("Extracted editor.html to plugin folder");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to extract editor.html: " + e.getMessage());
                return;
            }
        }
        
        // Try to open in browser
        try {
            openInBrowser(editorFile);
            plugin.getLogger().info("Opened editor.html in browser");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to open browser: " + e.getMessage());
            plugin.getLogger().info("Editor location: " + editorFile.getAbsolutePath());
        }
    }
    
    private static void openInBrowser(File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        String os = System.getProperty("os.name").toLowerCase();
        
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            // Use Desktop API (works on most systems)
            Desktop.getDesktop().browse(file.toURI());
        } else {
            // Fallback to command line
            Runtime runtime = Runtime.getRuntime();
            String command;
            
            if (os.contains("win")) {
                // Windows
                command = "cmd /c start \"\" \"" + absolutePath + "\"";
            } else if (os.contains("mac")) {
                // macOS
                command = "open \"" + absolutePath + "\"";
            } else {
                // Linux and others
                command = "xdg-open \"" + absolutePath + "\"";
            }
            
            runtime.exec(command);
        }
    }
    
    public static String getEditorPath() {
        File dataFolder = PandoraHologramsPlugin.getInstance().getDataFolder();
        File editorFile = new File(dataFolder, "editor.html");
        return editorFile.getAbsolutePath();
    }

}

