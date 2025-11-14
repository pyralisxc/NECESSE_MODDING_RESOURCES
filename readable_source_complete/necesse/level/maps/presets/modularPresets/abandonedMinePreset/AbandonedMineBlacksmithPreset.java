/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineBlacksmithPreset
extends AbandonedMinePreset {
    public AbandonedMineBlacksmithPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 113, deepstonewall, 227, paintingbroken, 52, demonicanvil, 54, tungstenworkstation, 55, tungstenworkstation2],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 52, 0, 54, 55, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 227, 0, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        PresetUtils.applyRandomPainting(this, 4, 5, 0, random, PaintingSelectionTable.abandonedMinePaintings);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

