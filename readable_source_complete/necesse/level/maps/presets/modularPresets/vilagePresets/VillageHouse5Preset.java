/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageHouse5Preset
extends VillagePreset {
    public VillageHouse5Preset(GameRandom random) {
        super(4, 4, false, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [17, stonefloor, 20, stonepathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, 20, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, 20, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, 20, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, wooddoor, 257, paintingmouse, 68, woodwindow, 267, paintinglargeship, 268, paintinglargeship2, 524, pottedflower5, 339, sprucechest, 276, sign, 340, sprucedinnertable, 341, sprucedinnertable2, 342, sprucedesk, 343, sprucemodulartable, 344, sprucechair, 345, sprucebench, 346, sprucebench2, 347, sprucebookshelf, 348, sprucecabinet, 540, greencarpet, 541, heartcarpet, 349, sprucebed, 350, sprucebed2, 355, sprucedresser, 356, spruceclock, 357, sprucecandelabra, 359, sprucebathtub, 360, sprucebathtub2, 361, sprucetoilet, 241, candle, 251, paintingbanana, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 68, 63, 63, 63, 63, 0, 0, 63, 339, 276, 357, 359, 360, 268, 267, 357, 63, 0, 0, 63, 0, 0, 541, 541, 541, 541, 344, 344, 63, 0, 0, 64, 0, 0, 541, 541, 541, 541, 340, 341, 63, 0, 0, 63, 0, 0, 0, 347, 347, 0, 344, 344, 63, 0, 0, 63, 356, 0, 0, 348, 348, 0, 0, 0, 68, 0, 0, 63, 361, 0, 540, 540, 540, 540, 346, 343, 63, 0, 0, 63, 257, 0, 540, 540, 540, 540, 345, 343, 63, 0, 0, 63, 349, 350, 357, 0, 355, 0, 344, 342, 63, 0, 0, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 2, 0, 2, 3, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 251, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 524, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 241, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.addInventory(LootTablePresets.carpenterChest, random, 2, 2, new Object[0]);
        PresetUtils.applyRandomPot(this, 9, 7, random);
        PresetUtils.applyRandomPainting(this, 8, 2, 2, random, PaintingSelectionTable.largeRarePaintings);
        PresetUtils.applyRandomPainting(this, 2, 8, 1, random, PaintingSelectionTable.uncommonPaintings);
        PresetUtils.applyRandomPainting(this, 9, 4, 3, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomCarpetToSelection(this, 4, 3, 4, 2, 0, random);
        PresetUtils.applyRandomCarpetToSelection(this, 4, 7, 4, 2, 0, random);
        this.open(0, 1, 3);
        this.addHumanMob(4, 4, "stylisthuman");
        this.addHumanMob(4, 7, "human");
    }
}

