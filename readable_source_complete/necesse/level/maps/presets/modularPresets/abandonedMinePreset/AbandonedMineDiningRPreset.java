/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineDiningRPreset
extends AbandonedMinePreset {
    public AbandonedMineDiningRPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 113, deepstonewall, 315, sprucedinnertable, 316, sprucedinnertable2, 237, paintingparrot, 319, sprucechair],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 237, 0, 0, 0, 113, 113, 0, 0, 0, 319, 319, 113, 113, 0, 0, 0, 316, 315, 113, 113, 0, 0, 0, 319, 319, 113, 113, 0, 0, 0, 0, 0, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 0);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        PresetUtils.applyRandomPainting(this, 2, 1, 2, random, PaintingSelectionTable.abandonedMinePaintings);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

