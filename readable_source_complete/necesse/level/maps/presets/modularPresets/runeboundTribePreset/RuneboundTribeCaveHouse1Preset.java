/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeCaveHouse1Preset
extends RuneboundTribePreset {
    public RuneboundTribeCaveHouse1Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [41, 41, 41, -1, -1, -1, -1, -1, -1, 41, 41, -1, 41, 41, 41, 41, 41, 41, 41, -1, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 17, 17, 41, 41, 41, 41, 41, -1, 41, 41, 41, 41, 17, 41, 41, 17, 41, 41, 41, -1, 41, 41, 41, 17, 17, 41, 41, 17, 41, 41, 41, -1, -1, 41, 41, 41, 17, 17, 17, 41, 41, 41, -1, -1, -1, -1, 41, 41, 41, 17, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 81, dryadwall, 514, bonecandelabra, 82, dryaddoor, 260, wallcandle, 501, bonechair, 808, graniterock, 265, tikitorch, 443, mapledresser, 811, granitecaverocksmall, 430, mapledesk, 431, maplemodulartable],\n\tobjects = [808, 808, 808, -1, -1, -1, -1, -1, -1, 808, 808, -1, 808, 808, 808, 808, 808, 808, 808, -1, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 808, 808, 808, 808, -1, -1, -1, -1, 808, 808, 808, 81, 808, 808, 808, 808, -1, -1, -1, 808, 808, 808, 443, 260, 431, 808, 808, 808, 808, -1, 808, 808, 808, 430, 0, 501, 431, 81, 808, 808, 808, -1, 808, 808, 808, 81, 0, 0, 501, 811, 808, 808, 808, -1, -1, 808, 808, 808, 514, 0, 0, 808, 808, 808, -1, -1, -1, -1, 808, 808, 808, 82, 808, 808, 808, 808, -1, -1, 808, 808, 808, 808, 808, 0, 808, 811, 808, 808, 808, -1, -1, 808, 808, 808, 811, 0, 265, 0, -1, 808, 808, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1],\n\trotations = [2, 3, 2, 1, 1, 1, 1, 1, 1, 3, 3, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 2, 1, 1, 0, 1, 2, 1, 1, 1, 1, 1, 3, 2, 1, 1, 0, 1, 0, 0, 1, 2, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 2, 2, 0, 2, 1, 1, 1, 1, 2, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.open(0, 0, 2);
        int runeboundCosmeticPieces = random.getIntBetween(0, 2);
        for (int i = 0; i < runeboundCosmeticPieces; ++i) {
            this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 4, 4, new Object[0]);
        }
        this.addRuneboundMobSpawn(5, 6);
    }
}

