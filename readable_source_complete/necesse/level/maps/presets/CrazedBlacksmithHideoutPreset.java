/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.util.ArrayList;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CrazedBlacksmithHideoutPreset
extends Preset {
    public CrazedBlacksmithHideoutPreset(GameRandom random, WallSet wall, FloorSet floor, FurnitureSet furniture, ColumnSet column) {
        super("PRESET = {\n\twidth = 15,\n\theight = 11,\n\ttileIDs = [2, watertile, 44, granitebrickfloor, 13, farmland, 46, graveltile, 63, sandgraveltile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, 44, 44, 44, 44, -1, 46, -1, -1, -1, -1, -1, -1, -1, -1, 44, 44, 44, 44, 44, 63, 63, -1, 46, -1, -1, -1, -1, 2, -1, 44, 44, 44, 44, 44, 46, 63, 63, -1, -1, -1, 2, 2, 2, -1, 44, 44, 44, 44, 44, -1, -1, 63, 46, -1, -1, 2, 2, 2, -1, 44, 44, 44, 44, 44, -1, -1, 63, -1, 46, -1, -1, -1, 2, -1, 44, 44, 44, 44, 44, 46, 46, 63, 63, -1, -1, -1, -1, -1, -1, 63, -1, 46, -1, 46, -1, -1, 63, -1, -1, -1, -1, -1, -1, 63, -1, 63, -1, 63, -1, 63, 63, -1, -1, -1, -1, -1, 2, 2, -1, -1, 46, -1, 46, -1, 46, 46, 46, -1, -1, -1, -1, 2, -1, -1, 13, 13, 13, 13, 13, -1, -1, 46, -1, -1],\n\tobjectIDs = [0, air, 69, ironanvil, 391, flowerpot, 267, sandstonecolumn, 982, firemoneseed4, 23, cactus, 1048, sandsurfacerock, 920, chilipepperseed4, 1049, sandsurfacerockr, 1050, sandsurfacerocksmall, 283, sack, 541, palmdesk, 542, palmmodulartable, 543, palmchair, 98, sandstonedoor, 548, palmbed, 549, palmbed2, 43, coconuttree, 1070, granitecaverock, 1071, granitecaverockr, 56, forge, 313, wallcandle, 955, onionseed4, 318, tikitorch, 127, sandstonewall],\n\tobjects = [-1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 1050, 0, 0, 0, -1, -1, -1, -1, 0, 0, 127, 127, 127, 127, 127, 0, 0, 23, 0, 0, -1, 0, 0, 0, 0, 127, 548, 313, 0, 98, 0, 0, 0, 0, 1050, 0, 0, 0, 0, 0, 127, 549, 543, 541, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 127, 127, 127, 127, 0, 0, 0, 0, 318, 0, 0, 0, 0, 0, 127, 56, 283, 542, 127, 0, 43, 0, 0, 0, 0, 0, 0, 0, 0, 127, 69, 0, 542, 127, 1048, 1049, 0, 0, 0, 0, 23, 318, 0, 0, 0, 0, 0, 0, 0, 0, 391, 0, 0, 0, 1050, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 267, 0, 267, 0, 267, 0, 0, 0, 0, -1, 43, 0, 0, 0, 0, 920, 920, 982, 955, 955, 318, 1070, 1071, 0, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 3, 2, 1, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 1, 2, 2, 3, 2, 0, 3, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 3, 2, 3, 3, 2, 2, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 1, 0, 1, 2, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 3, 0, 1, 3, 1, 0, 0, 2, 0, 0, 3, 0, 0, 0, 1, 1, 1, 1, 3, 1, 2, 1, 1, 2, 0],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 313, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        WallSet.sandstone.replaceWith(wall, this);
        FloorSet.graniteBrick.replaceWith(floor, this);
        FurnitureSet.palm.replaceWith(furniture, this);
        ColumnSet.sandstone.replaceWith(column, this);
        this.replaceObject(ObjectRegistry.getObjectID("sandstonedoor"), ObjectRegistry.getObjectID("sandstonedooropen"));
        this.addMobs(7, 2, false, "ancientarmoredskeleton");
        LootTable chestLoot = new LootTable();
        chestLoot.items.add(new LootItem("smithingapron"));
        chestLoot.items.add(new LootItem("coin", random.getIntBetween(25, 75)));
        chestLoot.items.add(new LootItem("ironore", random.getIntBetween(5, 15)));
        chestLoot.items.add(new LootItem("demonicsword"));
        ArrayList<String> ironArmorLoot = new ArrayList<String>();
        ironArmorLoot.add("ironhelmet");
        ironArmorLoot.add("ironchestplate");
        ironArmorLoot.add("ironboots");
        chestLoot.items.add(new LootItem((String)ironArmorLoot.remove(random.getIntBetween(0, ironArmorLoot.size() - 1))));
        chestLoot.items.add(new ChanceLootItem(0.3f, (String)ironArmorLoot.remove(random.getIntBetween(0, ironArmorLoot.size() - 1))));
        this.addInventory(chestLoot, random, 7, 5, new Object[0]);
        this.addInventory(new LootTable(LootItem.between("palmlog", 2, 5)), random, 6, 5, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

