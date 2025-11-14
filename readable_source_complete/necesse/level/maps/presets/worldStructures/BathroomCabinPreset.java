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

public class BathroomCabinPreset
extends LandStructurePreset {
    public BathroomCabinPreset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(10, 9);
        this.applyScript("PRESET = {\n\twidth = 10,\n\theight = 9,\n\ttileIDs = [1, dirttile, 3, grasstile, 19, stonetiledfloor, 12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 15, 12, 15, 3, 3, 3, 3, -1, -1, 3, 12, 15, 12, 3, 19, 19, 3, -1, -1, 3, 15, 12, 15, 12, 19, 19, 3, -1, -1, 3, 12, 15, 12, 1, 19, 19, 3, -1, -1, 3, 15, 12, 15, 12, 19, 19, 3, -1, -1, 3, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 289, oakcabinet, 290, oakbed, 291, oakbed2, 293, oakclock, 294, oakcandelabra, 296, oakbathtub, 297, oakbathtub2, 336, oaktoilet, 49, woodwall, 50, wooddoor, 179, walltorch, 149, woodfence, 151, woodfencegateopen, 280, oakchest, 153, woodfencegate, 283, oakdesk],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 49, 50, 49, 49, 49, 49, 49, 0, 0, 49, 283, 0, 294, 149, 296, 297, 49, 0, 0, 49, 293, 0, 0, 149, 0, 153, 49, 0, 0, 49, 291, 0, 0, 151, 0, 336, 49, 0, 0, 49, 290, 280, 289, 149, 179, 179, 49, 0, 0, 49, 49, 49, 49, 49, 49, 49, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 1, 1, 1, 3, 1, 0, 0, 3, 1, 2, 3, 0, 0, 0, 0, 0, 0, 3, 1, 2, 2, 0, 2, 3, 0, 0, 0, 2, 0, 2, 2, 1, 2, 3, 0, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 0, 0, 2, 2, 2, 2, 1, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        this.addInventory(LootTablePresets.basicCrate, random, 3, 6, new Object[0]);
    }
}

