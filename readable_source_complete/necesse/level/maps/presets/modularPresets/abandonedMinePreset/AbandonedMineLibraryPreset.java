/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;

public class AbandonedMineLibraryPreset
extends AbandonedMinePreset {
    public AbandonedMineLibraryPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 113, deepstonewall, 322, sprucebookshelf, 227, paintingbroken, 187, storagebox, 318, sprucemodulartable, 319, sprucechair],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 318, 318, 322, 322, 113, 113, 0, 319, 319, 0, 0, 113, 113, 0, 0, 0, 0, 187, 113, 113, 227, 0, 0, 0, 0, 113, 113, 0, 0, 0, 0, 0, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
        this.addInventory(LootTablePresets.abandonedMineChest, random, 5, 3, new Object[0]);
        PresetUtils.applyRandomPainting(this, 1, 4, 1, random, PaintingSelectionTable.abandonedMinePaintings);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

