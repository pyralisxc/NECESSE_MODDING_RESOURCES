/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.network.client.Client
 *  necesse.level.maps.Level
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.buildmode;

import medievalsim.MedievalSimSettings;
import medievalsim.buildmode.BuildModePreviewElement;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.network.client.Client;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class BuildModeManager {
    private static BuildModeManager instance;
    public static final int SHAPE_NORMAL = 0;
    public static final int SHAPE_LINE = 1;
    public static final int SHAPE_CROSS = 2;
    public static final int SHAPE_L = 3;
    public static final int SHAPE_T = 4;
    public static final int SHAPE_SQUARE = 5;
    public static final int SHAPE_CIRCLE = 6;
    public static final int SHAPE_DIAMOND = 7;
    public static final int SHAPE_HALF_CIRCLE = 8;
    public static final int SHAPE_TRIANGLE = 9;
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;
    public boolean buildModeEnabled = false;
    public int selectedShape = 0;
    public boolean isHollow = false;
    public int lineLength = 5;
    public int squareSize = 5;
    public int circleRadius = 5;
    public int spacing = 1;
    public int direction = 0;
    private Client client;
    private BuildModePreviewElement previewElement;
    private Level currentLevel;
    private boolean settingsDirty = false;
    private long lastSettingsSaveTime = 0L;

    private BuildModeManager(Client client) {
        this.client = client;
        this.loadSettings();
    }

    public static BuildModeManager getInstance(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null when getting BuildModeManager instance");
        }
        if (instance == null) {
            instance = new BuildModeManager(client);
        } else if (BuildModeManager.instance.client != client) {
            instance.cleanup();
            instance = new BuildModeManager(client);
        } else if (BuildModeManager.instance.client == null) {
            BuildModeManager.instance.client = client;
        }
        return instance;
    }

    public static BuildModeManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("BuildModeManager not initialized! Call getInstance(Client) first.");
        }
        if (BuildModeManager.instance.client == null) {
            throw new IllegalStateException("BuildModeManager has null client! This should never happen.");
        }
        return instance;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static boolean isActive() {
        return instance != null && BuildModeManager.instance.buildModeEnabled;
    }

    private void cleanup() {
        this.removePreviewElement();
        this.buildModeEnabled = false;
    }

    private Client getValidClient() {
        if (this.client == null) {
            throw new IllegalStateException("BuildModeManager client is null");
        }
        if (this.client.hasDisconnected()) {
            throw new IllegalStateException("BuildModeManager client has disconnected");
        }
        return this.client;
    }

    private boolean isClientValid() {
        return this.client != null && !this.client.hasDisconnected();
    }

    public void setBuildModeEnabled(boolean enabled) {
        PermissionLevel level;
        if (!this.isClientValid()) {
            System.err.println("MedievalSim: ERROR - Cannot set build mode - client is invalid or disconnected");
            return;
        }
        if (enabled && ((level = this.client.getPermissionLevel()) == null || level.getLevel() < PermissionLevel.ADMIN.getLevel())) {
            System.out.println("MedievalSim: WARNING - Player attempted to enable build mode without ADMIN permission");
            return;
        }
        this.buildModeEnabled = enabled;
        if (enabled) {
            this.addPreviewElement();
        } else {
            this.removePreviewElement();
        }
    }

    public void checkLevelChange() {
        Level level;
        if (!this.buildModeEnabled) {
            return;
        }
        Level level2 = level = this.isClientValid() ? this.client.getLevel() : null;
        if (level != this.currentLevel) {
            this.currentLevel = level;
            if (this.previewElement != null) {
                this.removePreviewElement();
                this.addPreviewElement();
            }
        }
    }

    private void addPreviewElement() {
        if (this.client == null || this.client.getPlayer() == null) {
            return;
        }
        Level level = this.client.getLevel();
        if (level == null || level.hudManager == null) {
            return;
        }
        this.removePreviewElement();
        this.previewElement = new BuildModePreviewElement(this.client);
        level.hudManager.addElement((HudDrawElement)this.previewElement);
        this.currentLevel = level;
    }

    private void removePreviewElement() {
        if (this.previewElement != null) {
            this.previewElement.remove();
            this.previewElement = null;
        }
        this.currentLevel = null;
    }

    public void setShape(int shape) {
        if (shape < 0 || shape > 9) {
            System.err.println("MedievalSim: ERROR - Invalid shape " + shape + ", must be 0-9");
            return;
        }
        this.selectedShape = shape;
        this.saveSettings();
    }

    public void setHollow(boolean hollow) {
        this.isHollow = hollow;
        this.saveSettings();
    }

    public boolean canBeHollow() {
        return this.selectedShape == 5 || this.selectedShape == 6 || this.selectedShape == 7 || this.selectedShape == 8 || this.selectedShape == 9;
    }

    public void setLineLength(int length) {
        if (length < 1 || length > 50) {
            System.err.println("MedievalSim: ERROR - Invalid line length " + length + ", must be 1-50");
            return;
        }
        this.lineLength = length;
        this.saveSettings();
    }

    public void setSquareSize(int size) {
        if (size < 1 || size > 25) {
            System.err.println("MedievalSim: ERROR - Invalid square size " + size + ", must be 1-25");
            return;
        }
        this.squareSize = size;
        this.saveSettings();
    }

    public void setCircleRadius(int radius) {
        if (radius < 1 || radius > 25) {
            System.err.println("MedievalSim: ERROR - Invalid circle radius " + radius + ", must be 1-25");
            return;
        }
        this.circleRadius = radius;
        this.saveSettings();
    }

    public void setSpacing(int spacing) {
        if (spacing < 1 || spacing > 10) {
            System.err.println("MedievalSim: ERROR - Invalid spacing " + spacing + ", must be 1-10");
            return;
        }
        this.spacing = spacing;
        this.saveSettings();
    }

    public void setDirection(int direction) {
        if (direction < 0 || direction > 3) {
            System.err.println("MedievalSim: ERROR - Invalid direction " + direction + ", must be 0-3");
            return;
        }
        this.direction = direction;
        this.saveSettings();
    }

    public String getShapeName(int shape, boolean hollow) {
        String baseName;
        switch (shape) {
            case 0: {
                return "Normal";
            }
            case 1: {
                return "Line";
            }
            case 2: {
                return "Cross";
            }
            case 3: {
                return "L";
            }
            case 4: {
                return "T";
            }
            case 5: {
                baseName = "Square";
                break;
            }
            case 6: {
                baseName = "Circle";
                break;
            }
            case 7: {
                baseName = "Diamond";
                break;
            }
            case 8: {
                baseName = "Half Circle";
                break;
            }
            case 9: {
                baseName = "Triangle";
                break;
            }
            default: {
                return "Unknown";
            }
        }
        if (hollow && this.canBeHollow()) {
            return "Hollow " + baseName;
        }
        return baseName;
    }

    private void loadSettings() {
        try {
            ModSettings modSettings;
            LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> m.id.equals("medieval.sim")).findFirst().orElse(null);
            if (mod != null && (modSettings = mod.getSettings()) instanceof MedievalSimSettings) {
                MedievalSimSettings settings = (MedievalSimSettings)modSettings;
                this.selectedShape = settings.savedShape;
                this.isHollow = settings.savedIsHollow;
                this.lineLength = settings.savedLineLength;
                this.squareSize = settings.savedSquareSize;
                this.circleRadius = settings.savedCircleRadius;
                this.spacing = settings.savedSpacing;
                this.direction = settings.savedDirection;
                System.out.println("MedievalSim: INFO - Loaded build mode settings from config");
            }
        }
        catch (Exception e) {
            System.err.println("MedievalSim: WARNING - Failed to load build mode settings: " + e.getMessage());
        }
    }

    public void saveSettings() {
        this.settingsDirty = true;
        this.lastSettingsSaveTime = System.currentTimeMillis();
    }

    private void saveSettingsNow() {
        try {
            ModSettings modSettings;
            LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> m.id.equals("medieval.sim")).findFirst().orElse(null);
            if (mod != null && (modSettings = mod.getSettings()) instanceof MedievalSimSettings) {
                MedievalSimSettings settings = (MedievalSimSettings)modSettings;
                settings.savedShape = this.selectedShape;
                settings.savedIsHollow = this.isHollow;
                settings.savedLineLength = this.lineLength;
                settings.savedSquareSize = this.squareSize;
                settings.savedCircleRadius = this.circleRadius;
                settings.savedSpacing = this.spacing;
                settings.savedDirection = this.direction;
                Settings.saveClientSettings();
                System.out.println("MedievalSim: INFO - Saved build mode settings to config");
                this.settingsDirty = false;
            }
        }
        catch (Exception e) {
            System.err.println("MedievalSim: WARNING - Failed to save build mode settings: " + e.getMessage());
        }
    }

    public void tick() {
        if (this.settingsDirty && System.currentTimeMillis() - this.lastSettingsSaveTime >= 2000L) {
            this.saveSettingsNow();
        }
    }
}

