/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.presets.Preset;

public class PresetRedoData {
    public final int uniqueID;
    public final LevelIdentifier levelIdentifier;
    public final int tileX;
    public final int tileY;
    public final Preset preset;

    public PresetRedoData(int uniqueID, LevelIdentifier levelIdentifier, Preset preset, int tileX, int tileY) {
        this.uniqueID = uniqueID;
        this.levelIdentifier = levelIdentifier;
        this.preset = preset;
        if (preset != null) {
            preset.clearOtherWires = true;
        }
        this.tileX = tileX;
        this.tileY = tileY;
    }
}

