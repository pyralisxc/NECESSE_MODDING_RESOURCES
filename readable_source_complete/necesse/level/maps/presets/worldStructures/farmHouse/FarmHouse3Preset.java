/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class FarmHouse3Preset
extends LandStructurePreset {
    public FarmHouse3Preset(GameRandom random, LootTable farmersChestLootTable) {
        super(7, 5);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 5,\n\ttileIDs = [9, farmland, 13, woodpathtile],\n\ttiles = [13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 9, 13, 13, 13, 13, 13],\n\tobjectIDs = [0, air, 288, oakcandelabra, 274, oakchest, 292, oaktoilet, 43, woodwall, 283, oakcabinet, 44, wooddoor, 284, oakbed, 285, oakbed2, 173, walltorch, 143, woodfence],\n\tobjects = [43, 43, 143, 43, 43, 44, 43, 43, 284, 285, 43, 283, 0, 43, 43, 0, 0, 0, 0, 173, 43, 43, 288, 0, 292, 0, 274, 43, 143, 43, 143, 43, 143, 43, 43],\n\trotations = [0, 2, 3, 1, 3, 0, 1, 0, 1, 1, 2, 2, 2, 3, 1, 2, 0, 2, 1, 0, 2, 2, 2, 0, 0, 1, 3, 1, 3, 3, 2, 3, 2, 0, 3],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(farmersChestLootTable, random, 5, 3, new Object[0]);
    }
}

