/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeHouse2Preset
extends RuneboundTribePreset {
    public RuneboundTribeHouse2Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 41, -1, 41, 41, 41, 41, -1, 41, -1, -1, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, -1, -1, 41, 41, 17, 17, 17, 17, 17, 17, 41, 41, -1, -1, -1, 41, 17, 17, 17, 17, 17, 17, 41, -1, -1, -1, 41, 41, 17, 17, 17, 17, 17, 17, 41, 41, -1, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, -1, -1, -1],\n\tobjectIDs = [432, maplechair, 0, air, 496, bonechest, 81, dryadwall, 82, dryaddoor, 260, wallcandle, 502, bonebench, 503, bonebench2, 265, tikitorch, 811, granitecaverocksmall, 428, mapledinnertable, 429, mapledinnertable2],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 81, -1, 81, 81, 81, 81, -1, 81, -1, -1, -1, 81, 81, 81, 81, 502, 503, 81, 81, 81, 81, -1, -1, 81, 81, 260, 432, 0, 0, 0, 260, 81, 81, -1, -1, -1, 81, 428, 429, 0, 0, 0, 496, 81, -1, -1, -1, 81, 81, 432, 0, 0, 0, 0, 0, 81, 81, -1, -1, 81, 81, 81, 81, 0, 0, 81, 81, 81, 81, -1, -1, 811, 81, 0, 81, 82, 82, 81, 811, 81, 0, -1, -1, 0, 0, 265, 0, 0, 0, 0, 265, 0, 0, -1, -1, -1, 0, 0, 811, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 2, 3, 0, 0, 0, 0, 3, 0, 1, 1, 2, 0, 2, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 1, 1, 0, 1, 3, 3, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 2, 0, 0, 2, 2, 0, 1, 0, 3, 2, 2, 0, 2, 3, 3, 2, 2, 2, 2, 2, 0, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 3, 0, 0, 0, 0, 0, 0, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.open(0, 0, 2);
        this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 8, 4, new Object[0]);
        this.addCustomPreApplyRectEach(-1, -1, this.width + 2, this.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.graniteRockID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addRuneboundMobSpawn(6, 4);
    }
}

