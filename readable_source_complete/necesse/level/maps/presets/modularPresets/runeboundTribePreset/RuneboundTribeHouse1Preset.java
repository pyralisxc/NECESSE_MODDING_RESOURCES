/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribePreset;

public class RuneboundTribeHouse1Preset
extends RuneboundTribePreset {
    public RuneboundTribeHouse1Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, dryadfloor, 41, graniterocktile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 41, -1, 41, 41, 41, 41, -1, 41, -1, -1, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, -1, -1, -1, 41, 17, 17, 17, 17, 17, 17, 41, -1, -1, -1, -1, 41, 17, 17, 17, 17, 17, 17, 41, -1, -1, -1, 41, 41, 17, 17, 17, 17, 17, 17, 41, 41, -1, -1, -1, 41, 17, 17, 17, 17, 17, 17, 41, -1, -1, -1, -1, 41, 17, 17, 17, 17, 17, 17, 41, -1, -1, -1, 41, 41, 41, 41, 17, 17, 41, 41, 41, 41, -1, -1, -1, 41, 41, 41, 17, 17, 41, 41, 41, -1, -1, -1, -1, -1, 41, 41, 41, 41, 41, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 579, brownbearcarpet, 260, wallcandle, 265, tikitorch, 811, granitecaverocksmall, 431, maplemodulartable, 81, dryadwall, 433, maplebench, 434, maplebench2, 82, dryaddoor, 499, bonedesk, 439, mapledoublebed, 440, mapledoublebedfoot1, 504, bonebookshelf, 441, mapledoublebed2, 442, mapledoublebedfoot2, 443, mapledresser, 63, woodwall],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 81, -1, 81, 81, 81, 81, -1, 81, -1, -1, -1, 81, 81, 81, 81, 439, 441, 81, 81, 81, 81, -1, -1, -1, 81, 0, 0, 440, 442, 0, 443, 81, -1, -1, -1, -1, 81, 0, 0, 433, 434, 0, 0, 81, -1, -1, -1, 81, 81, 260, 63, 431, 431, 63, 260, 81, 81, -1, -1, -1, 81, 0, 504, 579, 579, 499, 0, 81, -1, -1, -1, -1, 81, 0, 0, 0, 0, 0, 0, 81, -1, -1, -1, 81, 81, 81, 81, 0, 0, 81, 81, 81, 81, -1, -1, -1, 81, 811, 81, 82, 82, 81, 0, 81, -1, -1, -1, -1, -1, 0, 0, 0, 0, 811, 265, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 0, 0, 0, 1, 0, 3, 3, 1, 2, 2, 2, 0, 2, 2, 0, 0, 0, 0, 1, 1, 1, 2, 2, 0, 2, 2, 0, 2, 0, 1, 1, 1, 1, 2, 0, 0, 1, 1, 0, 0, 0, 1, 1, 2, 2, 2, 3, 0, 2, 2, 0, 1, 0, 0, 1, 2, 2, 2, 3, 2, 0, 0, 2, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 1, 3, 2, 2, 2, 2, 1, 3, 0, 0, 0, 0, 3, 3, 3, 2],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 579, 579, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 579, 579, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.open(0, 0, 2);
        int runeboundCosmeticPieces = random.getIntBetween(0, 3);
        for (int i = 0; i < runeboundCosmeticPieces; ++i) {
            this.addInventory(LootTablePresets.runeboundCosmeticArmorLootTable, random, 8, 3, new Object[0]);
        }
        this.addCustomPreApplyRectEach(-1, -1, this.width + 2, this.height + 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.graniteRockID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addRuneboundMobSpawn(6, 7);
    }
}

