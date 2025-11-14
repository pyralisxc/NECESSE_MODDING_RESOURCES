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

public class AbandonedMineBedroomRPreset
extends AbandonedMinePreset {
    public AbandonedMineBedroomRPreset(GameRandom random, int floorID) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 256, flowerpot, 113, deepstonewall, 257, armorstand, 227, paintingbroken, 324, sprucebed, 325, sprucebed2, 329, sprucedisplay, 187, storagebox],\n\tobjects = [113, 113, 113, 113, 113, 113, 113, 113, 0, 227, 0, 0, 0, 113, 113, 0, 0, 0, 0, 257, 113, 113, 0, 0, 0, 0, 329, 113, 113, 0, 0, 0, 0, 325, 113, 113, 256, 187, 0, 0, 324, 113, 113, 113, 113, 113, 113, 113, 113],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceObject("stonewall", "deepstonewall");
        this.replaceTile(TileRegistry.stoneFloorID, floorID);
        this.open(0, 0, 0);
        this.open(0, 0, 3);
        this.addInventory(LootTablePresets.abandonedMineChest, random, 2, 5, new Object[0]);
        PresetUtils.applyRandomPainting(this, 2, 1, 2, random, PaintingSelectionTable.abandonedMinePaintings);
        this.addSkeletonMiner(this.width / 2, this.height / 2, random, 0.3f);
    }
}

