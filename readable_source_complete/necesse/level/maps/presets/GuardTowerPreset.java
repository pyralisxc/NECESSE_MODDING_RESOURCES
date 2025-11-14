/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.PathSet;
import necesse.level.maps.presets.set.WallSet;

public class GuardTowerPreset
extends Preset {
    public GuardTowerPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, WallSet outerWalls, WallSet interiorWalls, FurnitureSet furniture, FloorSet floorset, GroundSet gravel, PathSet path) {
        super("PRESET = {\n\twidth = 23,\n\theight = 18,\n\ttileIDs = [28, sandstonebrickfloor, 29, sandstonepathtile, 47, sandbrick],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 28, 28, 28, -1, -1, 28, -1, -1, -1, 29, -1, -1, 29, -1, -1, 29, -1, -1, -1, -1, -1, -1, 28, 28, 28, 28, -1, -1, 28, -1, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, 28, 28, 28, 28, 28, 28, -1, 28, -1, 28, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, 28, 28, 28, 28, 28, 28, 28, -1, -1, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 47, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, -1, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 28, 28, 28, -1, 28, 28, 28, 28, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 28, -1, 28, 28, 28, 28, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 576, palmbathtub, 864, leathercarpet, 97, palmwall, 577, palmbathtub2, 98, palmdoor, 578, palmtoilet, 291, barrel, 1379, crate, 331, tikitorch, 556, palmchest, 790, pottedplant6, 566, palmbed, 567, palmbed2, 248, woodfence, 121, stonewall, 410, armorstand, 411, trainingdummy, 573, palmclock, 574, palmcandelabra],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 0, 0, 331, 0, 0, 0, 0, 0, 331, 0, 0, 0, 0, 248, 291, 0, 0, 1379, 1379, 0, 0, 331, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 0, 411, 0, 0, 0, 0, 411, 0, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 0, 0, 0, 0, 0, 0, 0, 0, 248, 0, 0, 121, 121, 121, 98, 121, 121, 121, 0, 0, 0, 0, 248, 0, 0, 0, 0, 291, 0, 0, 1379, 248, 0, 121, 574, 410, 573, 0, 0, 790, 574, 121, 0, 0, 0, 248, 0, 411, 0, 0, 0, 0, 411, 0, 248, 121, 1379, 1379, 0, 0, 0, 0, 97, 97, 97, 121, 0, 0, 248, 0, 0, 0, 0, 0, 0, 0, 0, 121, 790, 1379, 0, 0, 0, 0, 0, 98, 0, 576, 577, 121, 0, 248, 331, 0, 0, 0, 0, 0, 0, 0, 121, 0, 0, 0, 0, 0, 0, 0, 97, 0, 864, 864, 121, 0, 248, 0, 411, 0, 0, 0, 0, 411, 0, 121, 0, 97, 97, 97, 97, 0, 97, 97, 97, 97, 0, 121, 0, 248, 0, 0, 0, 0, 0, 0, 0, 0, 98, 0, 97, 0, 0, 0, 0, 0, 0, 0, 97, 0, 121, 0, 248, 1379, 0, 0, 0, 0, 0, 0, 0, 121, 410, 97, 0, 556, 0, 556, 0, 556, 0, 97, 578, 121, 0, 248, 0, 0, 0, 0, 0, 0, 0, 291, 248, 121, 97, 0, 567, 0, 567, 0, 567, 0, 97, 121, 0, 0, 248, 0, 411, 0, 0, 0, 0, 411, 0, 248, 0, 121, 574, 566, 0, 566, 0, 566, 574, 121, 0, 0, 0, 248, 0, 0, 0, 0, 0, 0, 0, 0, 248, 0, 0, 121, 121, 121, 121, 121, 121, 121, 0, 0, 0, 0, 248, 291, 291, 0, 0, 0, 0, 0, 331, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 3, 2, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 3, 3, 3, 3, 0, 3, 3, 3, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 2, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 864, 864, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        WallSet.stone.replaceWith(outerWalls, this);
        WallSet.palm.replaceWith(interiorWalls, this);
        FurnitureSet.palm.replaceWith(furniture, this);
        FloorSet.sandstoneBrick.replaceWith(floorset, this);
        GroundSet.desert.replaceWith(gravel, this);
        PathSet.sandStone.replaceWith(path, this);
        this.addMob("guardhuman", 16, 9, false);
        this.addMob("guardhuman", 16, 11, false);
        this.addMob("guardhuman", 5, 9, false);
        ArrayList<Point> barrelPositions = new ArrayList<Point>();
        barrelPositions.add(new Point(2, 3));
        barrelPositions.add(new Point(6, 6));
        barrelPositions.add(new Point(9, 13));
        barrelPositions.add(new Point(2, 16));
        barrelPositions.add(new Point(3, 16));
        for (Point barrelPosition : barrelPositions) {
            if (!random.getChance(0.35f)) continue;
            this.addInventory(new LootTable(random.getOneOf(LootItem.between("brokencoppertool", 1, 2), LootItem.between("brokenirontool", 1, 2), LootItem.between("mapfragment", 1, 2)), random.getOneOf(LootItem.between("stonearrow", 13, 28), LootItem.between("firearrow", 13, 28), LootItem.between("frostarrow", 13, 28))), random, barrelPosition.x, barrelPosition.y, new Object[0]);
        }
        ArrayList<Point> armorStandPositions = new ArrayList<Point>();
        armorStandPositions.add(new Point(14, 6));
        armorStandPositions.add(new Point(11, 12));
        for (Point armorStandPosition : armorStandPositions) {
            if (!random.getChance(0.5f)) continue;
            this.addInventory(new LootTable(random.getOneOf(new LootTable(new LootItem("copperhelmet"), new LootItem("copperchestplate"), new LootItem("copperboots")), new LootTable(new LootItem("ironhelmet"), new LootItem("ironchestplate"), new LootItem("ironboots")), new LootTable(new LootItem("goldhelmet"), new LootItem("goldchestplate"), new LootItem("goldboots")))), random, armorStandPosition.x, armorStandPosition.y, new Object[0]);
        }
        this.addInventory(new LootTable(new LootItem("paintingposter")), random, 14, 12, new Object[0]);
        this.addInventory(new LootTable(random.getOneOf(LootTablePresets.oldVinylsLootTable)), random, 16, 12, new Object[0]);
        this.addInventory(new LootTable(new LootItem("dogplush")), random, 18, 12, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

