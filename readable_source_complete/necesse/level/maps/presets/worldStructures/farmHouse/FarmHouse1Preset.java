/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class FarmHouse1Preset
extends LandStructurePreset {
    public FarmHouse1Preset(GameRandom random, LootTable farmersChestLootTable) {
        super(7, 5);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 5,\n\ttileIDs = [13, woodpathtile],\n\ttiles = [13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13],\n\tobjectIDs = [0, air, 226, oakchair, 230, oakcabinet, 231, oakbed, 232, oakbed2, 43, woodwall, 235, oakcandelabra, 44, wooddoor, 173, walltorch, 143, woodfence, 239, oaktoilet, 221, oakchest, 222, oakdinnertable, 223, oakdinnertable2],\n\tobjects = [43, 43, 143, 43, 44, 43, 43, 43, 235, 223, 222, 0, 230, 43, 43, 0, 226, 0, 0, 221, 43, 43, 239, 0, 173, 232, 231, 43, 43, 43, 43, 43, 43, 143, 43],\n\trotations = [0, 3, 2, 1, 0, 0, 0, 0, 1, 3, 3, 0, 2, 2, 3, 2, 0, 0, 0, 3, 2, 2, 1, 0, 0, 3, 3, 2, 2, 3, 0, 3, 1, 1, 2],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(farmersChestLootTable, random, 5, 2, new Object[0]);
    }
}

