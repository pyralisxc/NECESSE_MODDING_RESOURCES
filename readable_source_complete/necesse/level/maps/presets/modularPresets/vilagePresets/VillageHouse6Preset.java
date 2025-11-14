/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageHouse6Preset
extends VillagePreset {
    public VillageHouse6Preset(GameRandom random) {
        super(4, 3, false, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 9,\n\ttileIDs = [20, stonepathtile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, wooddoor, 545, velourcarpet, 259, paintingduck, 355, sprucedresser, 68, woodwindow, 356, spruceclock, 357, sprucecandelabra, 524, pottedflower5, 687, iceblossom, 339, sprucechest, 340, sprucedinnertable, 341, sprucedinnertable2, 344, sprucechair, 347, sprucebookshelf, 251, paintingbanana, 348, sprucecabinet, 349, sprucebed, 350, sprucebed2, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 68, 63, 63, 63, 63, 0, 0, 63, 357, 687, 347, 344, 339, 344, 340, 344, 63, 0, 0, 63, 259, 0, 545, 545, 0, 344, 341, 344, 63, 0, 0, 64, 0, 0, 545, 545, 0, 0, 0, 251, 63, 0, 0, 63, 0, 0, 0, 0, 0, 0, 0, 355, 63, 0, 0, 68, 0, 356, 348, 344, 524, 357, 350, 349, 63, 0, 0, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 1, 2, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 3, 0, 1, 0, 0, 1, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 2, 1, 0, 0, 0, 1, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.addInventory(LootTablePresets.hunterChest, random, 6, 2, new Object[0]);
        PresetUtils.applyRandomFlower(this, 3, 2, random);
        PresetUtils.applyRandomPot(this, 6, 6, random);
        PresetUtils.applyRandomPainting(this, 2, 3, 1, random, PaintingSelectionTable.uncommonPaintings);
        PresetUtils.applyRandomPainting(this, 9, 4, 3, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomCarpetToSelection(this, 4, 3, 2, 2, 0, random);
        this.open(0, 1, 3);
        this.addHumanMob(4, 4, "human");
        this.addHumanMob(7, 4, "hunterhuman", "animalkeeperhuman");
    }
}

