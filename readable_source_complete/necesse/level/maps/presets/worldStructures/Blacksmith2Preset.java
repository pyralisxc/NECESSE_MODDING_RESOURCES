/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class Blacksmith2Preset
extends LandStructurePreset {
    public Blacksmith2Preset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(12, 6);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 6,\n\ttileIDs = [17, stonefloor, 20, stonepathtile, 36, graveltile],\n\ttiles = [-1, -1, -1, 17, 17, 17, 17, 17, -1, -1, -1, -1, 17, 17, 17, 17, 17, 17, 17, 17, 20, 20, 36, -1, 17, 17, 17, 17, 17, 17, 17, 36, 36, 36, 20, -1, 17, 17, 17, 17, 17, 17, 17, 17, 20, 20, 36, -1, 17, 17, 17, 17, 17, 17, 17, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, stonewall, 66, stonedooropen, 611, campfire, 35, forge, 40, ironanvil, 395, dungeondinnertable, 396, dungeondinnertable2, 206, wallcandle, 50, wooddoor, 179, walltorch, 152, stonefence, 409, dungeondisplay],\n\tobjects = [0, 0, 0, 64, 64, 64, 64, 64, 0, 0, 0, 0, 64, 64, 64, 64, 395, 396, 409, 64, 152, 152, 64, 0, 64, 179, 66, 0, 0, 0, 0, 66, 0, 611, 152, 0, 64, 0, 64, 206, 40, 206, 35, 64, 152, 152, 64, 0, 64, 50, 64, 64, 64, 64, 64, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 1, 1, 0, 2, 2, 2, 2, 2, 3, 1, 3, 1, 1, 2, 2, 2, 2, 1, 2, 2, 2, 0, 3, 2, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.stone.replaceWith(wallSet, this);
        FenceSet.stone.replaceWith(fenceSet, this);
        FurnitureSet.dungeon.replaceWith(furnitureSet, this);
        PresetUtils.addFuelToInventory(this, 9, 2, random, "oaklog", 5, 14, true);
        PresetUtils.addFuelToInventory(this, 6, 3, random, "oaklog", 8, 16, false);
        this.addInventory(new LootTable(new LootItem("ironbar")), random, 6, 1, new Object[0]);
    }
}

