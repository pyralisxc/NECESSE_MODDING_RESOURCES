/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineBlacksmithRPreset
extends AbandonedMinePreset {
    public AbandonedMineBlacksmithRPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 113, deepstonewall, 52, demonicanvil, 54, tungstenworkstation, 55, tungstenworkstation2, 234, paintingdagger],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 0, 0, 234, 0, 113, 113, 0, 0, 0, 0, 52, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 54, 113, 113, 0, 0, 0, 0, 55, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 0);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        PresetUtils.applyRandomPainting(this, 4, 1, 2, random, PaintingSelectionTable.abandonedMinePaintings);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

