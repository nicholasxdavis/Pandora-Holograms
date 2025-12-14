![PandoraHolograms Logo](https://nicholasxdavis.github.io/BN-db1/img/panholo1.png)

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.13%2B-green.svg)
![Paper](https://img.shields.io/badge/paper-supported-orange.svg)
![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)

**A high-performance, user-friendly hologram plugin for modern Minecraft servers**

[Features](#features) • [Installation](#installation) • [Commands](#commands) • [API](#api) • [Website](#website)

</div>

---

## About

PandoraHolograms is a modern, feature-rich hologram plugin designed for Paper/Spigot servers. It provides an intuitive command system, seamless PlaceholderAPI integration, and a beautiful web-based editor for creating holograms visually.

### Why PandoraHolograms?

- **Simple & Intuitive** - One command to create holograms with multiple lines
- **User-Friendly** - Web-based visual editor for easy hologram creation
- **High Performance** - Optimized for servers with many holograms
- **PlaceholderAPI Support** - Full integration with automatic updates
- **Developer-Friendly** - Clean, well-documented API
- **Production Ready** - Battle-tested and stable

---

## Features

### Core Features
- **Easy Creation** - Create holograms with a single command
- **Multiple Lines** - Support for unlimited lines per hologram
- **Color Codes** - Full Minecraft color and formatting support
- **Line Management** - Add, insert, set, and remove lines easily
- **Persistent Storage** - YAML-based storage (easily extensible to MySQL)
- **Permission System** - Per-hologram permissions support
- **Configurable Spacing** - Customizable line height per hologram

### Advanced Features
- **PlaceholderAPI Integration** - Automatic placeholder parsing and updates
- **Visual Editor** - Web-based editor with live preview
- **Command Export** - Export holograms from the editor to in-game commands
- **Clone System** - Easily duplicate holograms
- **Teleportation** - Teleport to hologram locations
- **Info Display** - View detailed hologram information

---

## Installation

### Requirements
- **Minecraft Server**: Paper or Spigot 1.13+
- **Java**: Java 17 or higher
- **Optional**: PlaceholderAPI (for placeholder support)

### Quick Install

1. **Download** the latest release from the [Releases](https://github.com/yourusername/PandoraHolograms/releases) page
2. **Place** `PandoraHolograms-1.0.0.jar` in your server's `plugins/` folder
3. **Restart** your server
4. **Done!** The plugin will create its configuration files automatically

### With PlaceholderAPI

1. Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
2. Install PandoraHolograms
3. Placeholders will work automatically!

---

## Commands

### Basic Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/holo create <name> [lines...]` | Create a new hologram | `pandoraholograms.create` |
| `/holo addline <name> <text>` | Add a line to the bottom | `pandoraholograms.edit` |
| `/holo insertline <name> <index> <text>` | Insert a line at position | `pandoraholograms.edit` |
| `/holo setline <name> <index> <text>` | Set a specific line | `pandoraholograms.edit` |
| `/holo removeline <name> <index>` | Remove a line | `pandoraholograms.edit` |
| `/holo movehere <name>` | Move hologram to you | `pandoraholograms.edit` |
| `/holo delete <name>` | Delete a hologram | `pandoraholograms.delete` |
| `/holo list` | List all holograms | `pandoraholograms.use` |

### Utility Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/holo teleport <name>` | Teleport to hologram | `pandoraholograms.use` |
| `/holo clone <source> <newName>` | Clone a hologram | `pandoraholograms.create` |
| `/holo info <name>` | Show hologram info | `pandoraholograms.use` |
| `/holo update <name>` | Update hologram display | `pandoraholograms.edit` |
| `/holo live` | Open visual editor | `pandoraholograms.use` |
| `/holo version` | Show version info | `pandoraholograms.use` |
| `/holo reload` | Reload plugin | `pandoraholograms.admin` |

### Command Examples

```bash
# Create a simple hologram
/holo create welcome "&e&lWelcome!" "&7To our server"

# Create with multiple lines
/holo create spawn "&e&lSpawn" "" "&7Welcome to spawn!" "&7Enjoy your stay!"

# Add a line
/holo addline welcome "&7Player count: &e%server_online%"

# Insert a line at position 2
/holo insertline welcome 2 "&7New line here"

# Move hologram to your location
/holo movehere welcome

# Clone a hologram
/holo clone welcome welcome2

# View hologram information
/holo info welcome
```

---

## Visual Editor

PandoraHolograms includes a beautiful web-based visual editor!

### Using the Editor

1. **In-Game**: Run `/holo live` to open the editor in your browser
2. **Standalone**: Open `plugins/PandoraHolograms/editor.html` in your browser
3. **Edit**: Create and edit holograms visually with live preview
4. **Export**: Click "Export" to get commands ready to paste in-game

### Editor Features

- **Live Preview** - See your hologram as you type
- **Drag & Drop** - Reorder lines by dragging
- **Color Picker** - Click to copy color codes
- **Line Numbers** - See line positions
- **Insert Lines** - Add lines anywhere
- **One-Click Export** - Copy commands instantly

---

## API

PandoraHolograms provides a clean, developer-friendly API.

### Basic Usage

```java
import com.pandora.holograms.api.PandoraAPI;
import com.pandora.holograms.hologram.Hologram;
import org.bukkit.Location;

// Create a hologram
Hologram holo = PandoraAPI.createHologram("my_holo", location);
holo.addLine("&aHello World!");

// Create with initial lines
List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");
Hologram holo2 = PandoraAPI.createHologram("my_holo2", location, lines);

// Get a hologram
Hologram holo = PandoraAPI.getHologram("my_holo");

// Clone a hologram
Hologram clone = holo.clone("clone_name", newLocation);

// Update lines (for PlaceholderAPI)
holo.updateLines(player);

// Delete a hologram
PandoraAPI.deleteHologram("my_holo");
```

### Advanced Usage

```java
// Set custom line height
holo.setLineHeight(0.5);

// Set permission
holo.setPermission("myplugin.use");

// Check if player has permission
if (holo.hasPermission(player)) {
    // Show hologram
}

// Update specific line
holo.setLine(0, "&eUpdated line");

// Insert line
holo.insertLine(1, "&7New line");

// Get all holograms
Collection<Hologram> all = PandoraAPI.getAllHolograms();
```

### API Methods

#### PandoraAPI

| Method | Description |
|--------|-------------|
| `createHologram(String, Location)` | Create empty hologram |
| `createHologram(String, Location, List<String>)` | Create with lines |
| `getHologram(String)` | Get hologram by name |
| `deleteHologram(String)` | Delete hologram |
| `hologramExists(String)` | Check if exists |
| `getAllHolograms()` | Get all holograms |
| `moveHologram(String, Location)` | Move hologram |

#### Hologram

| Method | Description |
|--------|-------------|
| `addLine(String)` | Add line to bottom |
| `insertLine(int, String)` | Insert line at index |
| `setLine(int, String)` | Set line at index |
| `removeLine(int)` | Remove line |
| `clearLines()` | Clear all lines |
| `updateLines()` | Update display |
| `updateLines(Player)` | Update for player |
| `setLocation(Location)` | Set location |
| `setLineHeight(double)` | Set line spacing |
| `setPermission(String)` | Set permission |
| `clone(String, Location)` | Clone hologram |

---

## Configuration

Default `config.yml`:

```yaml
# Default line height between hologram lines
default-line-height: 0.3

# Default display range in blocks
default-display-range: 48

# Update interval in ticks (20 ticks = 1 second)
# Used for PlaceholderAPI updates
update-interval: 20

# Check for updates on startup
update-checker: true

# Storage type (currently only yaml, MySQL support coming soon)
storage-type: yaml
```

---

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `pandoraholograms.use` | Use basic commands | OP |
| `pandoraholograms.create` | Create holograms | OP |
| `pandoraholograms.edit` | Edit hologram lines | OP |
| `pandoraholograms.delete` | Delete holograms | OP |
| `pandoraholograms.admin` | Admin commands (reload) | OP |
| `pandoraholograms.*` | All permissions | OP |

---

## Building

### Prerequisites
- Java 17 or higher
- Gradle 8.5+

### Build Steps

```bash
# Clone the repository
git clone https://github.com/yourusername/PandoraHolograms.git
cd PandoraHolograms/PandoraHolograms

# Build the plugin
./gradlew build

# Or on Windows
gradlew.bat build

# The JAR will be in build/libs/
```

### Development

```bash
# Build with shadow JAR (includes dependencies)
./gradlew shadowJar

# Clean build
./gradlew clean build
```

---

## Testing

We provide a test plugin for debugging and testing:

```bash
# Build the tester plugin
cd ../PandoraHologramsTester
./gradlew build

# Install both plugins and use /phtest in-game
```

See [PandoraHologramsTester](../PandoraHologramsTester/README.md) for more information.

---

## PlaceholderAPI

PandoraHolograms fully supports PlaceholderAPI placeholders!

### Examples

```bash
# Player placeholders
/holo create stats "%player_name%"
/holo addline stats "&7Health: &a%player_health%"
/holo addline stats "&7Food: &e%player_food_level%"

# Server placeholders
/holo create server "&e&lServer Info"
/holo addline server "&7Online: &a%server_online%"
/holo addline server "&7Max: &e%server_max_players%"
/holo addline server "&7TPS: &a%server_tps%"
```

Placeholders are automatically updated every second when PlaceholderAPI is installed!

---

## Website Integration

PandoraHolograms includes a beautiful web-based editor:

- **Location**: `plugins/PandoraHolograms/editor.html`
- **Features**: Live preview, drag & drop, color picker, export
- **Usage**: Open in browser or use `/holo live` in-game

See [PandoraWebsite](../PandoraWebsite/README.md) for more information.

---

## Examples

### Welcome Hologram

```bash
/holo create welcome "&e&lWelcome to the Server!" "" "&7We hope you enjoy your stay" "&7Have fun!"
```

### Player Stats

```bash
/holo create stats "&e&l%player_name%" "" "&7Kills: &a%statistic_player_kills%" "&7Deaths: &c%statistic_deaths%" "&7K/D: &e%statistic_kd_ratio%"
```

### Server Info

```bash
/holo create serverinfo "&e&lServer Information" "" "&7Online: &a%server_online%/%server_max_players%" "&7Uptime: &e%server_uptime%" "&7TPS: &a%server_tps%"
```

---

## Troubleshooting

### Holograms appear upside down
- **Fixed!** This was resolved in v1.0.0. Update to the latest version.

### Placeholders not working
- Make sure PlaceholderAPI is installed and enabled
- Check that placeholders are valid (use `/papi parse me <placeholder>`)
- Restart the server

### Holograms not spawning
- Check console for errors
- Verify world exists
- Check permissions
- Ensure location is valid

### Commands not working
- Check permissions (`pandoraholograms.use`)
- Verify you're using the correct command syntax
- Check console for errors

---

## Contributing

Contributions are welcome! However, please note that by contributing, you agree that your contributions will be subject to the same proprietary license as the rest of the project.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

**Note:** All contributions become the property of Blacnova Development and are subject to the same proprietary license.

---

## License

Copyright (c) 2024 Blacnova Development. All Rights Reserved.

This software and associated documentation files (the "Software") are the proprietary property of Blacnova Development. 

**You may NOT:**
- Copy, modify, or create derivative works of the source code
- Redistribute the source code
- Use this software for commercial purposes without explicit written permission
- Remove or alter any copyright notices
- Reverse engineer or decompile the Software

**You MAY:**
- Download and use this software on your own Minecraft server(s)
- Share the compiled JAR file with other server owners (unmodified)
- Report bugs and suggest improvements

For licensing inquiries, please contact Blacnova Development.

See the [LICENSE](LICENSE) file for full terms and conditions.

---

<div align="center">

**Made with love by Blacnova Development**

</div>





