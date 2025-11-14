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

public class Blacksmith1Preset
extends LandStructurePreset {
    public Blacksmith1Preset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(9, 6);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 6,\n\ttileIDs = [17, stonefloor, 36, graveltile, 20, stonepathtile],\n\ttiles = [-1, -1, -1, -1, 17, 17, 17, 17, 17, -1, 36, 20, 20, 17, 17, 17, 17, 17, -1, 20, 36, 36, 36, 17, 17, 17, 17, -1, 36, 20, 20, 36, 17, 17, 17, 17, -1, -1, -1, -1, -1, -1, 17, 17, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, stonewall, 35, forge, 611, campfire, 179, walltorch, 152, stonefence, 40, ironanvil, 409, dungeondisplay, 395, dungeondinnertable, 396, dungeondinnertable2, 76, swampstonedooropen],\n\tobjects = [0, 0, 0, 0, 64, 64, 64, 64, 64, 0, 64, 152, 152, 64, 35, 395, 396, 64, 0, 152, 611, 0, 76, 0, 0, 40, 64, 0, 64, 152, 152, 64, 76, 179, 409, 64, 0, 0, 0, 0, 0, 179, 64, 64, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 0, 3, 1, 3, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.stone.replaceWith(wallSet, this);
        FenceSet.stone.replaceWith(fenceSet, this);
        FurnitureSet.dungeon.replaceWith(furnitureSet, this);
        PresetUtils.addFuelToInventory(this, 2, 2, random, "oaklog", 5, 14, true);
        PresetUtils.addFuelToInventory(this, 5, 1, random, "oaklog", 8, 16, false);
        this.addInventory(new LootTable(new LootItem("ironbar")), random, 7, 3, new Object[0]);
    }
}

