/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class BedCabinPreset
extends LandStructurePreset {
    public BedCabinPreset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(5, 7);
        this.applyScript("PRESET = {\n\twidth = 5,\n\theight = 7,\n\ttileIDs = [12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, -1, -1, 12, 15, 12, -1, -1, 12, 15, 12, -1, -1, 12, 15, 12, -1, -1, -1, 15, -1, -1],\n\tobjectIDs = [0, air, 49, woodwall, 50, wooddoor, 290, oakbed, 291, oakbed2, 149, woodfence, 150, woodfencegate, 280, oakchest, 298, oaktoilet, 206, wallcandle],\n\tobjects = [149, 149, 150, 149, 149, 149, 0, 0, 0, 149, 49, 49, 50, 49, 49, 49, 206, 0, 206, 49, 49, 291, 0, 298, 49, 49, 290, 0, 280, 49, 49, 49, 49, 49, 49],\n\trotations = [3, 3, 2, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        this.addInventory(LootTablePresets.basicCrate, random, 3, 5, new Object[0]);
    }
}

