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

public class CropFarm5Preset
extends LandStructurePreset {
    public CropFarm5Preset(GameRandom random, CropSet cropSet) {
        super(7, 6);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 6,\n\ttileIDs = [9, farmland, 13, woodpathtile],\n\ttiles = [13, 9, 13, 13, 13, 13, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9],\n\tobjectIDs = [144, woodfencegate, 0, air, 226, oakchair, 227, oakbench, 228, oakbench2, 404, wheatseed, 43, woodwall, 236, oakdisplay, 143, woodfence],\n\tobjects = [43, 43, 144, 43, 43, 143, 43, 227, 228, 0, 0, 226, 236, 226, 0, 0, 0, 0, 0, 0, 0, 143, 143, 144, 143, 143, 143, 143, 143, 0, 404, 404, 404, 404, 143, 143, 143, 144, 143, 143, 143, 143],\n\trotations = [2, 3, 0, 3, 2, 1, 3, 1, 1, 3, 1, 1, 1, 3, 0, 0, 2, 2, 1, 1, 0, 3, 0, 3, 3, 3, 3, 0, 3, 0, 1, 2, 1, 2, 0, 2, 3, 3, 1, 3, 1, 1],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(new LootTable(new LootItem(ItemRegistry.getItemStringID(cropSet.productID), 1)), random, 5, 1, new Object[0]);
        cropSet.replacePreset(CropSet.wheat, this, random);
    }
}

