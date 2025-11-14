/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CozyCaveHomePreset
extends Preset {
    public CozyCaveHomePreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, WallSet walls, FloorSet woodfloor, ColumnSet columns, FurnitureSet furniture, FloorSet stonefloor) {
        super("PRESET = {\n\twidth = 10,\n\theight = 9,\n\ttileIDs = [32, swampstonebrickfloor, 18, dryadfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 18, 18, 18, 18, 18, 18, 32, -1, -1, -1, 18, 18, 18, 18, 18, -1, 32, 32, -1, -1, 18, 18, 18, 18, 18, 32, 32, 32, -1, -1, 18, 18, 18, 18, 18, 18, 32, 32, -1, -1, -1, -1, 18, -1, -1, -1, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 321, oillantern, 675, dryadmodulartable, 1379, crate, 423, minecarttrack, 681, dryadbed, 298, walltorch, 682, dryadbed2, 139, snowstonewall, 140, snowstonedoor, 687, dryaddresser, 144, snowstonewindow, 690, dryaddisplay, 850, oldsoup, 277, snowstonecolumn, 693, dryadtoilet, 790, pottedplant6, 248, woodfence, 765, sink, 671, dryadchest],\n\tobjects = [139, 139, 139, 139, 144, 139, 139, 139, 139, 277, 139, 687, 681, 298, 0, 690, 139, 765, 139, 139, 139, 0, 682, 0, 0, 0, 139, 0, 693, 139, 144, 0, 0, 0, 0, 0, 139, 0, 0, 139, 139, 671, 0, 0, 0, 0, 140, 0, 139, 139, 139, 139, 139, 140, 139, 144, 139, 139, 139, 0, 277, 675, 0, 0, 321, 423, 423, 1379, 277, 0, 248, 790, 0, 0, 0, 0, 1379, 1379, 248, 0, 248, 248, 248, 0, 248, 248, 248, 248, 248, 0],\n\trotations = [0, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 2, 2, 2, 0, 2, 0, 2, 1, 1, 0, 0, 2, 0, 0, 0, 3, 0, 3, 1, 2, 0, 0, 0, 0, 3, 3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 2, 3, 2, 0, 1, 1, 0, 2, 2, 2, 2, 2, 1, 1, 1, 2, 0, 3, 2, 0, 0, 0, 0, 1, 1, 1, 0, 2, 3, 3, 0, 3, 3, 3, 3, 2, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 850, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        WallSet.snowStone.replaceWith(walls, this);
        FloorSet.dryad.replaceWith(woodfloor, this);
        ColumnSet.snowstone.replaceWith(columns, this);
        FurnitureSet.dryad.replaceWith(furniture, this);
        FloorSet.swampStoneBrick.replaceWith(stonefloor, this);
        String settlerID = random.getOneOf("blacksmithhuman", "minerhuman");
        this.addHuman(settlerID, 3, 1, settler -> {}, random);
        this.addInventory(new LootTable(LootItem.between("hardhat", 2, 5)), random, 5, 1, new Object[0]);
        this.addInventory(new LootTable(new LootItem("dinoplush"), LootItem.between("coin", 23, 89), LootItem.between("torch", 7, 23), random.getOneOf(LootTablePresets.oldVinylsLootTable), random.getOneOf(LootItem.between("battlepotion", 2, 4), LootItem.between("speedpotion", 2, 4), LootItem.between("resistancepotion", 2, 4))), random, 1, 4, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

