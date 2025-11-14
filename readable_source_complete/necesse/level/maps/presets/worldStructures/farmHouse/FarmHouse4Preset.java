/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class FarmHouse4Preset
extends LandStructurePreset {
    public FarmHouse4Preset(GameRandom random, LootTable farmersChestLootTable, CropSet cropSet) {
        super(7, 5);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 5,\n\ttileIDs = [9, farmland, 13, woodpathtile],\n\ttiles = [13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 9, 13, 13, 13, 13, 13],\n\tobjectIDs = [0, air, 226, oakchair, 230, oakcabinet, 231, oakbed, 232, oakbed2, 43, woodwall, 235, oakcandelabra, 44, wooddoor, 173, walltorch, 143, woodfence, 239, oaktoilet, 221, oakchest, 222, oakdinnertable, 223, oakdinnertable2],\n\tobjects = [43, 43, 143, 43, 43, 43, 43, 43, 221, 223, 222, 230, 235, 143, 43, 0, 226, 0, 0, 239, 43, 43, 173, 0, 231, 232, 0, 44, 43, 43, 43, 43, 43, 143, 43],\n\trotations = [0, 2, 3, 1, 3, 3, 1, 0, 2, 3, 3, 2, 2, 3, 1, 2, 0, 2, 1, 3, 2, 2, 3, 0, 1, 1, 3, 1, 2, 3, 2, 3, 2, 1, 3],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(farmersChestLootTable, random, 1, 1, new Object[0]);
        this.addInventory(new LootTable(new LootItem(ItemRegistry.getItemStringID(cropSet.productID), 1)), random, 4, 1, new Object[0]);
    }
}

