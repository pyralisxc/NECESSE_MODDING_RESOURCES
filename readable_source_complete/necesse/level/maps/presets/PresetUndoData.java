/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.util.Collection;
import java.util.LinkedList;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRedoData;

public class PresetUndoData {
    public final int uniqueID;
    public final LevelIdentifier levelIdentifier;
    public final int tileX;
    public final int tileY;
    public final Preset clientPreset;
    public Preset serverPreset;
    public LinkedList<Preset.UndoLogic> serverUndos = new LinkedList();
    public LinkedList<Preset.UndoLogic> clientUndos = new LinkedList();

    public PresetUndoData(int uniqueID, LevelIdentifier levelIdentifier, Preset clientPreset, int tileX, int tileY) {
        this.uniqueID = uniqueID;
        this.levelIdentifier = levelIdentifier;
        this.clientPreset = clientPreset;
        if (clientPreset != null) {
            clientPreset.clearOtherWires = true;
        }
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void applyClient(Level level) {
        this.clientPreset.applyToLevel(level, this.tileX, this.tileY);
        this.clientUndos.forEach(undoLogic -> undoLogic.applyUndo(level, this.tileX, this.tileY));
    }

    public void applyServer(Level level, Collection<PresetRedoData> redoDataList) {
        if (this.serverPreset != null) {
            if (redoDataList != null) {
                Preset redoPreset = new Preset(this.serverPreset.width, this.serverPreset.height);
                redoPreset.copyFromLevel(level, this.tileX, this.tileY);
                redoDataList.add(new PresetRedoData(this.uniqueID, this.levelIdentifier, redoPreset, this.tileX, this.tileY));
            }
            this.serverPreset.applyToLevel(level, this.tileX, this.tileY);
            this.serverUndos.forEach(undoLogic -> undoLogic.applyUndo(level, this.tileX, this.tileY));
        }
    }
}

