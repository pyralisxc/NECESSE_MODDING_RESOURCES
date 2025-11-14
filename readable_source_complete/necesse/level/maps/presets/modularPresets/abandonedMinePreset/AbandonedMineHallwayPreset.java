/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineHallwayPreset
extends AbandonedMinePreset {
    public AbandonedMineHallwayPreset(GameRandom random, boolean openTop, boolean openRight, boolean openBottom, boolean openLeft) {
        super(1, 1, 7, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 78, stonewall],\n\tobjects = [78, 78, 78, 78, 78, 78, 78, 78, 0, 0, 0, 0, 0, 78, 78, 0, 0, 0, 0, 0, 78, 78, 0, 0, 0, 0, 0, 78, 78, 0, 0, 0, 0, 0, 78, 78, 0, 0, 0, 0, 0, 78, 78, 78, 78, 78, 78, 78, 78],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject(this.wall, 0);
        this.replaceObject("stonewall", "air");
        this.replaceTile(TileRegistry.stoneFloorID, TileRegistry.deepStoneFloorID);
        this.closeObject = this.wall;
        if (openTop) {
            this.open(0, 0, 0);
        }
        if (openRight) {
            this.open(0, 0, 1);
        }
        if (openBottom) {
            this.open(0, 0, 2);
        }
        if (openLeft) {
            this.open(0, 0, 3);
        }
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.05f);
    }

    @Override
    public void fillOpeningReal(Level level, int x, int y, int dir, int object, int tile) {
        super.fillOpeningRealSuper(level, x, y, dir, object, tile);
    }
}

