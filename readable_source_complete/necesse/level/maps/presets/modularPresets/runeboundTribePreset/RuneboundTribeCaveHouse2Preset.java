/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeCaveHouse2Preset
extends RuneboundTribePreset {
    public RuneboundTribeCaveHouse2Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [-1, -1, 41, 41, 41, 41, -1, 41, 41, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 17, 41, 41, 41, 41, 41, -1, -1, 41, 41, 41, 17, 41, 17, 41, 17, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 17, 17, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 17, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, -1, -1, 41, 41, 41, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [432, maplechair, 0, air, 496, bonechest, 81, dryadwall, 82, dryaddoor, 259, candle, 808, graniterock, 265, tikitorch, 811, granitecaverocksmall, 443, mapledresser, 431, maplemodulartable],\n\tobjects = [-1, -1, 808, 808, 808, 808, -1, 808, 808, -1, -1, -1, -1, 808, 808, 808, 808, 808, 808, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 808, 808, 808, 808, 808, 808, -1, 808, 808, 808, 808, 81, 431, 808, 808, 808, 808, 808, -1, 808, 808, 808, 811, 432, 431, 432, 808, 808, 808, -1, -1, 808, 808, 808, 81, 0, 443, 0, 496, 808, 808, 808, -1, -1, 808, 808, 808, 0, 0, 0, 81, 808, 808, 808, -1, -1, -1, 808, 808, 808, 0, 0, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 82, 808, 808, 808, 808, 808, -1, 808, 808, 808, 808, 265, 0, 808, 808, 808, 808, 808, -1, -1, 808, 808, -1, 0, 0, 811, 808, 808, 808, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 3, 2, 1, 0, 0, 0, 0, 3, 0, 2, 0, 0, 2, 2, 1, 3, 0, 0, 0, 1, 0, 2, 0, 0, 2, 2, 2, 2, 3, 0, 0, 2, 0, 3, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 0, 3, 3, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 2, 0, 3, 0, 2, 2, 3, 3, 3, 3, 2, 0, 3, 1, 0, 3, 2, 2, 2, 1, 1, 1, 0, 0, 3, 3, 3, 3, 0, 0, 0, 3, 3, 3, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 259, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.open(0, 0, 2);
        int runeboundCosmeticPieces = random.getIntBetween(0, 2);
        for (int i = 0; i < runeboundCosmeticPieces; ++i) {
            this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 5, 5, new Object[0]);
        }
        this.addRuneboundMobSpawn(5, 6);
    }
}

