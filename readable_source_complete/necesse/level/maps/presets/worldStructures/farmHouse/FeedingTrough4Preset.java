/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class FeedingTrough4Preset
extends LandStructurePreset {
    public FeedingTrough4Preset(GameRandom random) {
        super(7, 5);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 5,\n\ttileIDs = [1, graveltile, 13, woodpathtile],\n\ttiles = [1, 1, 1, 1, -1, -1, 13, 1, 1, 1, 1, -1, -1, 13, 1, 1, 1, 1, -1, -1, 13, 1, 1, 1, 1, -1, -1, 13, 1, 1, 1, 1, -1, -1, 13],\n\tobjectIDs = [0, air, 144, woodfencegate, 518, feedingtrough, 519, feedingtrough2, 43, woodwall, 143, woodfence],\n\tobjects = [143, 143, 143, 143, 0, 0, 43, 143, 518, 519, 143, 0, 0, 43, 143, 0, 0, 144, 0, 0, 144, 143, 0, 0, 143, 0, 0, 43, 143, 143, 143, 143, 0, 0, 43],\n\trotations = [2, 2, 2, 3, 3, 2, 0, 2, 1, 1, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 3, 2, 2, 0, 3, 0, 0, 2, 3, 3, 2, 0, 2, 2, 2],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(new LootTable(new LootItem("wheat", random.getIntBetween(10, 20))), random, 1, 1, new Object[0]);
    }
}

