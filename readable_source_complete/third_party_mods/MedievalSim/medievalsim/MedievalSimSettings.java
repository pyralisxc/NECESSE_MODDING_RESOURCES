/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 */
package medievalsim;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class MedievalSimSettings
extends ModSettings {
    public int maxBlocksPerPlacement = 1500;
    public int defaultLineLength = 5;
    public int defaultSquareSize = 5;
    public int defaultCircleRadius = 5;
    public int defaultSpacing = 1;
    public boolean rememberBuildModeState = false;
    public int savedShape = 0;
    public boolean savedIsHollow = false;
    public int savedLineLength = 5;
    public int savedSquareSize = 5;
    public int savedCircleRadius = 5;
    public int savedSpacing = 1;
    public int savedDirection = 0;
    public long pvpReentryCooldownMs = 6000L;
    public float pvpSpawnImmunitySeconds = 5.0f;
    public float pvpDefaultDamageMultiplier = 0.05f;
    public int pvpMaxBarrierTiles = 10000;
    public int pvpBarrierBatchSize = 500;
    public int pvpBarrierMaxTilesPerTick = 1000;
    public int pvpForceCleanRadius = 50;
    public List<String> favoriteCommands = new ArrayList<String>();
    public List<String> commandHistory = new ArrayList<String>();
    public int commandCenterWidth = 600;
    public int commandCenterHeight = 700;
    public int activeTab = 0;
    public String lastSelectedCategory = "All";

    public void addSaveData(SaveData save) {
        SaveData buildMode = new SaveData("BUILD_MODE");
        buildMode.addInt("maxBlocksPerPlacement", this.maxBlocksPerPlacement, "Maximum number of blocks that can be placed at once (anti-abuse limit)");
        buildMode.addInt("defaultLineLength", this.defaultLineLength, "Default line length for line shapes [1-50]");
        buildMode.addInt("defaultSquareSize", this.defaultSquareSize, "Default square size [1-50]");
        buildMode.addInt("defaultCircleRadius", this.defaultCircleRadius, "Default circle radius [1-50]");
        buildMode.addInt("defaultSpacing", this.defaultSpacing, "Default spacing between placements [1-10]");
        save.addSaveData(buildMode);
        SaveData ui = new SaveData("UI");
        ui.addBoolean("rememberBuildModeState", this.rememberBuildModeState, "Remember build mode enabled state between sessions");
        save.addSaveData(ui);
        SaveData state = new SaveData("BUILD_MODE_STATE");
        state.addInt("savedShape", this.savedShape, "Last selected shape");
        state.addBoolean("savedIsHollow", this.savedIsHollow, "Last hollow state");
        state.addInt("savedLineLength", this.savedLineLength, "Last line length");
        state.addInt("savedSquareSize", this.savedSquareSize, "Last square size");
        state.addInt("savedCircleRadius", this.savedCircleRadius, "Last circle radius");
        state.addInt("savedSpacing", this.savedSpacing, "Last spacing");
        state.addInt("savedDirection", this.savedDirection, "Last direction");
        save.addSaveData(state);
        SaveData pvp = new SaveData("PVP");
        pvp.addLong("pvpReentryCooldownMs", this.pvpReentryCooldownMs, "Re-entry cooldown (ms) after leaving a PvP zone");
        pvp.addFloat("pvpSpawnImmunitySeconds", this.pvpSpawnImmunitySeconds, "Seconds of immunity on entering/spawning in a PvP zone");
        pvp.addFloat("pvpDefaultDamageMultiplier", this.pvpDefaultDamageMultiplier, "Default PvP damage multiplier (0.05 = 5%)");
        pvp.addInt("pvpMaxBarrierTiles", this.pvpMaxBarrierTiles, "Maximum number of barrier tiles to place before skipping");
        pvp.addInt("pvpBarrierBatchSize", this.pvpBarrierBatchSize, "Batch size for placing barriers to avoid tick spikes");
        pvp.addInt("pvpBarrierMaxTilesPerTick", this.pvpBarrierMaxTilesPerTick, "Maximum number of barrier tiles processed per server tick by the placement worker");
        pvp.addInt("pvpForceCleanRadius", this.pvpForceCleanRadius, "Default force-clean radius (10-500)");
        save.addSaveData(pvp);
        SaveData commandCenter = new SaveData("COMMAND_CENTER");
        commandCenter.addInt("width", this.commandCenterWidth, "Last Command Center window width");
        commandCenter.addInt("height", this.commandCenterHeight, "Last Command Center window height");
        commandCenter.addInt("activeTab", this.activeTab, "Last active tab (0=Console, 1=Mod Settings, 2=History)");
        commandCenter.addUnsafeString("lastSelectedCategory", this.lastSelectedCategory, "Last selected command category filter");
        commandCenter.addInt("favoriteCount", this.favoriteCommands.size(), "Number of favorite commands");
        for (int i = 0; i < this.favoriteCommands.size() && i < 10; ++i) {
            SaveData favorite = new SaveData("favorite_" + i);
            favorite.addUnsafeString("commandId", this.favoriteCommands.get(i));
            commandCenter.addSaveData(favorite);
        }
        int historyCount = Math.min(this.commandHistory.size(), 20);
        commandCenter.addInt("historyCount", historyCount, "Number of history entries");
        for (int i = 0; i < historyCount; ++i) {
            SaveData historyEntry = new SaveData("history_" + i);
            historyEntry.addUnsafeString("command", this.commandHistory.get(i));
            commandCenter.addSaveData(historyEntry);
        }
        save.addSaveData(commandCenter);
    }

    public void applyLoadData(LoadData load) {
        LoadData commandCenter;
        LoadData pvp;
        LoadData state;
        LoadData ui;
        LoadData buildMode = load.getFirstLoadDataByName("BUILD_MODE");
        if (buildMode != null) {
            this.maxBlocksPerPlacement = buildMode.getInt("maxBlocksPerPlacement", 1500);
            this.defaultLineLength = buildMode.getInt("defaultLineLength", 5);
            this.defaultSquareSize = buildMode.getInt("defaultSquareSize", 5);
            this.defaultCircleRadius = buildMode.getInt("defaultCircleRadius", 5);
            this.defaultSpacing = buildMode.getInt("defaultSpacing", 1);
            this.maxBlocksPerPlacement = Math.max(1, Math.min(1500, this.maxBlocksPerPlacement));
            this.defaultLineLength = Math.max(1, Math.min(50, this.defaultLineLength));
            this.defaultSquareSize = Math.max(1, Math.min(50, this.defaultSquareSize));
            this.defaultCircleRadius = Math.max(1, Math.min(50, this.defaultCircleRadius));
            this.defaultSpacing = Math.max(1, Math.min(10, this.defaultSpacing));
        }
        if ((ui = load.getFirstLoadDataByName("UI")) != null) {
            this.rememberBuildModeState = ui.getBoolean("rememberBuildModeState", false);
        }
        if ((state = load.getFirstLoadDataByName("BUILD_MODE_STATE")) != null) {
            this.savedShape = state.getInt("savedShape", 0);
            this.savedIsHollow = state.getBoolean("savedIsHollow", false);
            this.savedLineLength = state.getInt("savedLineLength", 5);
            this.savedSquareSize = state.getInt("savedSquareSize", 5);
            this.savedCircleRadius = state.getInt("savedCircleRadius", 5);
            this.savedSpacing = state.getInt("savedSpacing", 1);
            this.savedDirection = state.getInt("savedDirection", 0);
            this.savedShape = Math.max(0, Math.min(9, this.savedShape));
            this.savedLineLength = Math.max(1, Math.min(50, this.savedLineLength));
            this.savedSquareSize = Math.max(1, Math.min(50, this.savedSquareSize));
            this.savedCircleRadius = Math.max(1, Math.min(50, this.savedCircleRadius));
            this.savedSpacing = Math.max(1, Math.min(10, this.savedSpacing));
            this.savedDirection = Math.max(0, Math.min(3, this.savedDirection));
        }
        if ((pvp = load.getFirstLoadDataByName("PVP")) != null) {
            this.pvpReentryCooldownMs = pvp.getLong("pvpReentryCooldownMs", 6000L);
            this.pvpSpawnImmunitySeconds = pvp.getFloat("pvpSpawnImmunitySeconds", 5.0f);
            this.pvpDefaultDamageMultiplier = pvp.getFloat("pvpDefaultDamageMultiplier", 0.05f);
            this.pvpMaxBarrierTiles = pvp.getInt("pvpMaxBarrierTiles", 10000);
            this.pvpBarrierBatchSize = Math.max(1, Math.min(5000, pvp.getInt("pvpBarrierBatchSize", 500)));
            this.pvpBarrierMaxTilesPerTick = Math.max(1, Math.min(10000, pvp.getInt("pvpBarrierMaxTilesPerTick", 1000, false)));
            try {
                this.pvpForceCleanRadius = Math.max(10, Math.min(500, pvp.getInt("pvpForceCleanRadius", this.pvpForceCleanRadius)));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if ((commandCenter = load.getFirstLoadDataByName("COMMAND_CENTER")) != null) {
            this.commandCenterWidth = Math.max(400, Math.min(800, commandCenter.getInt("width", 600)));
            this.commandCenterHeight = Math.max(300, Math.min(900, commandCenter.getInt("height", 700)));
            this.activeTab = Math.max(0, Math.min(2, commandCenter.getInt("activeTab", 0)));
            this.lastSelectedCategory = commandCenter.getUnsafeString("lastSelectedCategory", "All");
            int favoriteCount = commandCenter.getInt("favoriteCount", 0);
            this.favoriteCommands.clear();
            for (int i = 0; i < favoriteCount && i < 10; ++i) {
                String commandId;
                LoadData favorite = commandCenter.getFirstLoadDataByName("favorite_" + i);
                if (favorite == null || (commandId = favorite.getUnsafeString("commandId", "")).isEmpty()) continue;
                this.favoriteCommands.add(commandId);
            }
            int historyCount = commandCenter.getInt("historyCount", 0);
            this.commandHistory.clear();
            for (int i = 0; i < historyCount; ++i) {
                String command;
                LoadData historyEntry = commandCenter.getFirstLoadDataByName("history_" + i);
                if (historyEntry == null || (command = historyEntry.getUnsafeString("command", "")).isEmpty()) continue;
                this.commandHistory.add(command);
            }
        }
    }
}

