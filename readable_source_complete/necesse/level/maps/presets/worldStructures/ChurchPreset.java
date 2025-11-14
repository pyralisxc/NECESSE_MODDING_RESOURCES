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

public class ChurchPreset
extends LandStructurePreset {
    public ChurchPreset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(9, 10);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 10,\n\ttileIDs = [10, woodfloor, 13, woodpathtile],\n\ttiles = [-1, 10, 13, 10, 13, 10, 13, 10, -1, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, 13, 10, -1, -1, 13, 10, 10, 10, 13, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 224, oakdesk, 227, oakbench, 228, oakbench2, 229, oakbookshelf, 234, oakclock, 43, woodwall, 235, oakcandelabra, 44, wooddoor, 237, oakbathtub, 238, oakbathtub2, 143, woodfence, 221, oakchest],\n\tobjects = [0, 43, 143, 43, 143, 43, 143, 43, 0, 43, 237, 238, 235, 224, 235, 221, 234, 43, 143, 0, 0, 0, 0, 0, 0, 0, 143, 43, 0, 228, 227, 0, 228, 227, 0, 43, 143, 229, 228, 227, 0, 228, 227, 229, 143, 43, 229, 228, 227, 0, 228, 227, 229, 43, 0, 43, 143, 43, 44, 43, 143, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 3, 1, 3, 1, 3, 1, 0, 0, 2, 2, 2, 0, 2, 2, 2, 1, 3, 0, 0, 2, 2, 2, 1, 3, 3, 0, 0, 3, 3, 0, 3, 3, 2, 0, 0, 1, 3, 3, 0, 3, 3, 3, 1, 2, 1, 3, 3, 0, 3, 3, 3, 0, 0, 2, 3, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        this.addInventory(LootTablePresets.templeChest, random, 6, 1, new Object[0]);
    }
}

