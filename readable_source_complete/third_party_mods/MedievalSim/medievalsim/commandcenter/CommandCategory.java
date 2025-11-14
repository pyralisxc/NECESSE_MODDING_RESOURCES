/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter;

public enum CommandCategory {
    SERVER_ADMIN("Server Administration", "Server management, permissions, bans, settings - ZERO F10 overlap"),
    TELEPORT("Teleport & Position", "Complex teleportation and position workflows"),
    TEAMS("Teams & Communication", "Team management and communication commands"),
    RAIDS("Raids & Events", "Server-side raid and event management"),
    WORLD_EDITING("Administrative World Tools", "Complex area operations and server maintenance"),
    INVENTORY("Advanced Inventory", "Complex inventory operations and administrative tools"),
    PLAYER_STATS("Administrative Player Management", "Server-side player management (not creative stats)"),
    WORLD("Administrative World Settings", "Server world configuration (not creative mode tools)"),
    OTHER("Other Administrative Tools", "Additional server management commands");

    private final String displayName;
    private final String description;

    private CommandCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }
}

