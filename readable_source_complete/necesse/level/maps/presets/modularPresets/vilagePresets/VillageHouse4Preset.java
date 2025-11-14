/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageHouse4Preset
extends VillagePreset {
    public VillageHouse4Preset(GameRandom random) {
        super(3, 3, false, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [17, stonefloor, 20, stonepathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, 17, 17, 17, 17, -1, -1, -1, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, -1, -1, -1, 17, 17, 17, 17, 17, -1, -1, -1, -1, -1, 20, 20, 20, -1, -1, -1],\n\tobjectIDs = [0, air, 880, ladderdown, 81, stonewall, 82, stonedoor, 213, torch, 86, stonewindow, 263, paintingswampcaveling],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 81, 81, 81, 81, 81, 0, 0, 0, 81, 81, 0, 263, 0, 81, 81, 0, 0, 81, 213, 0, 880, 0, 213, 81, 0, 0, 86, 0, 0, 0, 0, 0, 86, 0, 0, 81, 0, 0, 0, 0, 0, 81, 0, 0, 81, 81, 0, 0, 0, 81, 81, 0, 0, 0, 81, 81, 82, 81, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        PresetUtils.applyRandomPainting(this, 4, 2, 2, random, PaintingSelectionTable.rarePaintings);
        this.open(1, 2, 2);
        this.addHumanMob(4, 4, "minerhuman");
        this.addHumanMob(4, 6, "guardhuman");
    }
}

