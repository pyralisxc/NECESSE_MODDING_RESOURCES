/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeCaveHouse6Preset
extends RuneboundTribePreset {
    public RuneboundTribeCaveHouse6Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [41, 41, 41, -1, -1, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 17, 17, 41, 41, 41, 41, 41, -1, -1, 41, 41, 17, 17, 41, 41, 17, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 17, 17, 41, 41, -1, -1, 41, 41, 41, 17, 41, 41, 17, 41, 41, 41, -1, -1, -1, 41, 41, 41, 17, 41, 41, 17, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 17, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 17, 41, 41, 41, 41, -1, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 259, candle, 260, wallcandle, 808, graniterock, 265, tikitorch, 811, granitecaverocksmall, 428, mapledinnertable, 429, mapledinnertable2, 432, maplechair, 81, dryadwall, 82, dryaddoor, 436, maplecabinet, 506, bonebed, 507, bonebed2],\n\tobjects = [808, 808, 808, -1, -1, 808, 808, 808, 808, -1, -1, -1, 808, 808, 808, 808, 808, 808, 808, 808, -1, -1, -1, -1, 808, 808, 808, 808, 808, 808, 808, 808, 808, 808, -1, -1, -1, 808, 808, 808, 0, 436, 81, 808, 808, 808, 808, -1, -1, 808, 808, 81, 260, 0, 0, 432, 808, 808, 808, -1, 808, 808, 808, 506, 0, 0, 429, 428, 808, 808, -1, -1, 808, 808, 808, 507, 0, 0, 432, 0, 808, 808, -1, -1, -1, 808, 808, 808, 0, 0, 0, 81, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 0, 808, 808, 808, 808, -1, 808, 808, 808, 808, 811, 808, 82, 808, 808, 808, 808, -1, 808, 808, 811, 0, 0, 0, 0, 265, 808, 808, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 0, 0, 3, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 3, 3, 0, 0, 0, 2, 0, 0, 0, 0, 3, 2, 3, 0, 0, 0, 1, 0, 2, 2, 2, 3, 3, 2, 2, 0, 0, 2, 0, 0, 3, 3, 2, 2, 0, 2, 2, 2, 2, 2, 0, 0, 1, 0, 0, 2, 0, 2, 3, 2, 2, 2, 0, 0, 0, 0, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 2, 2, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 3, 3, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 259, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.open(0, 0, 2);
        int runeboundCosmeticPieces = random.getIntBetween(0, 2);
        for (int i = 0; i < runeboundCosmeticPieces; ++i) {
            this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 5, 3, new Object[0]);
        }
        this.addRuneboundMobSpawn(5, 5);
    }
}

