/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.Preset;

public class FishianHousePreset2
extends Preset {
    public FishianHousePreset2(GameRandom random, AtomicInteger chestRotation) {
        super(13, 10);
        this.applyScript("PRESET = {\n\twidth = 13,\n\theight = 10,\n\ttileIDs = [16, bamboofloor, 72, puddlecobble, 47, deepswampstonefloor],\n\ttiles = [-1, 47, 16, 47, 72, 47, 72, 72, -1, -1, -1, -1, -1, -1, 72, 47, 16, 16, 72, 72, 72, 72, 72, -1, -1, -1, 72, 16, 16, 16, 16, 16, 72, 72, 72, 72, 72, -1, -1, 72, 16, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, -1, 72, 16, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 72, 72, 16, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 72, 72, 16, 16, 16, 16, 16, 16, 16, 16, 16, 72, 72, 72, 72, 47, 72, 16, 72, 16, 16, 16, 16, 16, 72, 72, 72, -1, 72, 16, 47, 16, 72, 47, 16, 16, 72, 72, 72, 72, -1, 72, 72, 72, 47, 72, 72, 16, 72, 47, 72, 72, -1],\n\tobjectIDs = [0, air, 243, oillantern, 183, bamboowall, 215, barrel, 184, bamboodoor, 856, glowcoral, 857, seashell, 795, deepswampcaverocksmall, 253, tikitorch],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 857, 183, 183, 184, 183, 183, 795, 0, 856, 0, 0, 253, 0, 0, 183, 243, 0, 0, 183, 183, 183, 183, 183, 0, 183, 0, 0, 183, 0, 0, 0, 183, 0, 215, 0, 183, 856, 0, 0, 0, 183, 0, 0, 0, 184, 0, 0, 243, 183, 0, 183, 795, 0, 183, 183, 184, 183, 183, 0, 0, 0, 183, 0, 0, 0, 0, 0, 856, 0, 795, 183, 183, 184, 183, 183, 0, 183, 0, 0, 0, 0, 0, 253, 0, 0, 0, 0, 857, 0, 0, 856, 0, 857, 0, 0, 0, 0, 856, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 2, 1, 0, 3, 3, 0, 1, 1, 0, 0, 1, 0, 2, 2, 0, 1, 1, 2, 0, 0, 1, 0, 3, 2, 2, 0, 1, 0, 2, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 3, 2, 0, 2, 1, 0, 2, 2, 2, 1, 1, 0, 0, 3, 2, 0, 0, 0, 0, 0, 2, 0, 0, 3, 3, 2, 3, 3, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 3, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.addInventory(LootTablePresets.fishianBarrel, random, 7, 4, chestRotation);
        this.addMob("fishianhookwarrior", 3, 4, false);
        this.addMob("fishianhealer", 7, 5, false);
        this.addCustomApply(8, 9, 2, (level, levelX, levelY, dir, blackboard) -> {
            blackboard.set("doorTile", new Point(levelX, levelY));
            return null;
        });
    }
}

