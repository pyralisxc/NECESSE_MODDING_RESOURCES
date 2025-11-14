/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageHouse2Preset
extends VillagePreset {
    public VillageHouse2Preset(GameRandom random) {
        super(5, 5, false, random);
        this.applyScript("PRESET = {\n\twidth = 15,\n\theight = 15,\n\ttileIDs = [17, stonefloor, 20, stonepathtile, 11, farmland, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, -1, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, -1, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, -1, -1, 20, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 17, 17, 17, 17, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 17, 17, 17, 17, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 17, 17, 17, 17, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 259, paintingduck, 9, sprucesapling, 265, paintinglargeworldmap, 266, paintinglargeworldmap2, 267, paintinglargeship, 268, paintinglargeship2, 524, pottedflower5, 208, storagebox, 81, stonewall, 82, stonedoor, 213, torch, 86, stonewindow, 343, sprucemodulartable, 344, sprucechair, 345, sprucebench, 346, sprucebench2, 347, sprucebookshelf, 349, sprucebed, 542, purplecarpet, 350, sprucebed2, 608, tomatoseed3, 355, sprucedresser, 357, sprucecandelabra, 40, workstationduo, 41, workstationduo2, 48, alchemytable, 688, mushroomflower, 241, candle, 184, woodfence, 250, paintingavocado],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 81, 81, 81, 81, 86, 81, 81, 81, 184, 184, 184, 184, 184, 0, 0, 81, 357, 40, 41, 208, 48, 250, 81, 608, 608, 608, 608, 184, 0, 0, 81, 0, 542, 542, 542, 542, 0, 81, 608, 608, 608, 608, 184, 0, 0, 82, 0, 542, 542, 542, 542, 688, 81, 608, 608, 608, 608, 184, 0, 0, 81, 0, 542, 542, 542, 542, 343, 81, 608, 608, 608, 608, 184, 0, 0, 81, 357, 0, 0, 0, 0, 343, 81, 0, 0, 0, 0, 184, 0, 0, 81, 81, 81, 259, 0, 0, 0, 82, 0, 0, 0, 0, 184, 0, 0, 0, 0, 81, 0, 542, 542, 268, 81, 524, 346, 345, 213, 184, 0, 0, 9, 0, 81, 347, 542, 542, 267, 81, 81, 81, 81, 81, 81, 0, 0, 0, 0, 81, 347, 542, 542, 0, 0, 343, 343, 343, 343, 81, 0, 0, 9, 0, 81, 350, 0, 0, 0, 0, 344, 344, 344, 344, 81, 0, 0, 0, 0, 81, 349, 355, 357, 0, 0, 0, 0, 265, 266, 81, 0, 0, 9, 0, 81, 81, 81, 81, 81, 86, 81, 81, 81, 81, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 3, 3, 3, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 241, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 524, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.addInventory(LootTablePresets.alchemistChest, random, 5, 2, new Object[0]);
        int sapling = ObjectRegistry.getObjectID(random.nextBoolean() ? "oaksapling" : "sprucesapling");
        this.setObject(1, 9, sapling);
        this.setObject(1, 11, sapling);
        this.setObject(1, 13, sapling);
        PresetUtils.applyRandomSeedArea(this, 9, 2, 4, 4, random);
        PresetUtils.applyRandomFlower(this, 7, 4, random);
        PresetUtils.applyRandomPot(this, 7, 6, random);
        PresetUtils.applyRandomPot(this, 9, 8, random);
        PresetUtils.applyRandomPainting(this, 7, 2, 2, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomPainting(this, 4, 7, 1, random, PaintingSelectionTable.uncommonPaintings);
        PresetUtils.applyRandomPainting(this, 7, 9, 3, random, PaintingSelectionTable.largeRarePaintings);
        PresetUtils.applyRandomPainting(this, 11, 12, 0, random, PaintingSelectionTable.largeRarePaintings);
        PresetUtils.applyRandomCarpetToSelection(this, 3, 3, 4, 3, 0, random);
        PresetUtils.applyRandomCarpetToSelection(this, 5, 8, 2, 3, 0, random);
        this.open(0, 1, 3);
        this.addHumanMob(5, 4, "alchemisthuman");
        this.addHumanMob(6, 10, "human");
        this.addHumanMob(3, 4, "guardhuman");
    }
}

