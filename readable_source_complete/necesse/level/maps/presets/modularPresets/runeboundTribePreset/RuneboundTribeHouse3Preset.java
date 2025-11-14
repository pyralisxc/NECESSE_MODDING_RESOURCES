/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeHouse3Preset
extends RuneboundTribePreset {
    public RuneboundTribeHouse3Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 17, 17, 17, 17, 17, 41, 41, 41, -1, 41, 41, 41, 17, 17, 17, 17, 17, 41, 41, 41, -1, 41, 41, 41, 17, 17, 17, 41, 41, 41, 41, 41, -1, 41, 41, 41, 17, 17, 17, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 17, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, -1, -1, 41, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [432, maplemodulartable, 0, air, 81, dryadwall, 433, maplechair, 817, granitecaverocksmall, 82, dryaddoor, 260, candle, 261, wallcandle, 438, maplebed, 439, maplebed2, 266, tikitorch, 444, mapledresser],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 81, -1, -1, -1, -1, -1, 81, -1, -1, -1, -1, 81, 81, 81, 81, 81, 81, 81, 81, 81, -1, -1, -1, -1, 81, 432, 433, 444, 0, 438, 81, -1, -1, -1, -1, -1, 81, 432, 433, 0, 0, 439, 81, -1, -1, -1, -1, -1, 81, 261, 0, 0, 81, 81, 81, 81, -1, -1, -1, -1, 81, 0, 0, 0, 81, 817, 81, -1, -1, -1, -1, 81, 81, 81, 82, 81, 81, 81, 0, -1, -1, -1, -1, 817, 81, 0, 0, 0, 81, 0, 266, -1, -1, -1, -1, 0, 0, 266, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 3, 3, 3, 1, 1, 1, 1, 1, 2, 0, 0, 2, 2, 3, 3, 3, 3, 3, 3, 3, 2, 2, 0, 2, 2, 2, 3, 2, 3, 2, 0, 2, 2, 0, 0, 2, 2, 2, 3, 2, 3, 0, 0, 2, 2, 0, 0, 2, 2, 2, 3, 1, 0, 0, 3, 3, 2, 2, 0, 2, 2, 2, 3, 0, 0, 0, 3, 1, 2, 0, 0, 2, 2, 3, 3, 3, 2, 3, 3, 3, 0, 3, 0, 2, 2, 3, 3, 0, 0, 0, 3, 0, 3, 3, 3, 2, 2, 0, 0, 3, 0, 0, 0, 0, 0, 3, 3, 2, 2, 2, 0, 0, 0, 0, 0, 0, 3, 3, 3, 2, 3, 3, 0, 0, 0, 0, 0, 0, 3, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 260, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.open(0, 0, 2);
        int runeboundCosmeticPieces = random.getIntBetween(0, 2);
        for (int i = 0; i < runeboundCosmeticPieces; ++i) {
            this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 5, 3, new Object[0]);
        }
        this.addCustomPreApplyRectEach(-1, -1, this.width + 2, this.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.graniteRockID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addRuneboundMobSpawn(5, 5);
    }
}

