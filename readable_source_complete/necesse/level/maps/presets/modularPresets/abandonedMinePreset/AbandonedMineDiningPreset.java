/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineDiningPreset
extends AbandonedMinePreset {
    public AbandonedMineDiningPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 113, deepstonewall, 315, sprucedinnertable, 316, sprucedinnertable2, 319, sprucechair],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 319, 315, 319, 0, 113, 113, 0, 319, 316, 319, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

