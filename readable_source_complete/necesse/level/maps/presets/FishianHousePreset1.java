/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.Preset;

public class FishianHousePreset1
extends Preset {
    public FishianHousePreset1(GameRandom random, AtomicInteger chestRotation) {
        super(10, 9);
        this.applyScript("PRESET = {\n\twidth = 10,\n\theight = 9,\n\ttileIDs = [16, bamboofloor, 72, puddlecobble, 47, deepswampstonefloor],\n\ttiles = [-1, 72, 72, 72, 72, 72, 72, 72, -1, -1, 72, 16, 16, 16, 16, 16, 72, 72, 72, -1, 72, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 72, 72, 16, 72, 16, 16, 16, 16, 72, 72, 72, 47, 16, 16, 72, 47, 72, 72, -1, -1, 72, 72, 16, 47, 72, 72, 47, 72, -1, -1, -1, 47, 72, 72, 47, -1, 72, 72, -1],\n\tobjectIDs = [0, air, 243, oillantern, 183, bamboowall, 215, barrel, 247, candle, 856, glowcoral, 184, bamboodoor, 857, seashell, 394, palmdesk, 795, deepswampcaverocksmall, 253, tikitorch, 254, paintingbroken],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 857, 0, 0, 0, 183, 183, 183, 183, 183, 856, 795, 0, 0, 0, 183, 0, 254, 215, 183, 183, 183, 183, 0, 0, 183, 394, 0, 0, 184, 0, 243, 183, 0, 0, 183, 183, 184, 183, 183, 0, 0, 183, 0, 0, 856, 0, 0, 795, 183, 184, 183, 183, 0, 0, 795, 253, 0, 0, 0, 0, 0, 857, 0, 0, 857, 0, 0, 0, 253, 0, 795, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 856, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 3, 2, 1, 3, 3, 1, 0, 0, 0, 3, 1, 2, 0, 3, 3, 3, 3, 0, 0, 2, 1, 0, 0, 3, 0, 3, 0, 0, 0, 2, 1, 2, 2, 2, 1, 0, 0, 0, 3, 3, 0, 0, 1, 2, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 3, 0, 0, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 254, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 247, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.addInventory(LootTablePresets.fishianBarrel, random, 4, 2, chestRotation);
        this.addMob("fishianhookwarrior", 3, 3, false);
        this.addMob("fishianhealer", 6, 3, false);
        this.addCustomApply(3, 8, 2, (level, levelX, levelY, dir, blackboard) -> {
            blackboard.set("doorTile", new Point(levelX, levelY));
            return null;
        });
    }
}

