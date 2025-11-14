/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SmallCaveHomeRuinPreset
extends Preset {
    public SmallCaveHomeRuinPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, FloorSet floor1, FloorSet floor2, FloorSet floor3, FurnitureSet furniture, CarpetSet carpet, WallSet walls) {
        super("PRESET = {\n\twidth = 11,\n\theight = 10,\n\ttileIDs = [16, palmfloor, 27, sandstonefloor, 28, sandstonebrickfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 27, 28, -1, -1, -1, -1, -1, -1, -1, 16, 16, 16, 16, 28, 16, 27, -1, -1, -1, 16, 27, 16, 16, 27, 16, 16, 16, -1, -1, -1, 28, 27, 27, 28, 27, 16, 16, 27, -1, -1, -1, 16, 16, 27, 28, 16, 27, 27, 27, 16, -1, -1, 28, 27, 27, 16, 16, 16, 28, 16, 16, -1, -1, -1, 28, 27, 27, 27, 16, 28, -1, -1, -1, -1, -1, 27, 27, 27, 27, 27, 27, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 546, palmbookshelf, 772, dirtyplate, 548, palmbed, 549, palmbed2, 69, ironanvil, 808, spilledgoldchalice, 814, brokenplate, 338, paintingstonecaveling, 757, skull, 54, workstationduo, 1334, crate, 55, workstationduo2, 1208, cobweb, 56, forge, 283, sack, 828, brownbearcarpet, 542, palmmodulartable, 734, decorativepot1, 798, brokenherbglassdisplay, 127, sandstonewall, 543, palmchair],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1208, 127, 127, -1, -1, -1, -1, -1, -1, -1, 127, 1208, 772, 54, 55, 127, 127, -1, -1, -1, 127, 548, 283, 0, 0, 1208, 546, 127, -1, -1, -1, 127, 549, 0, 828, 828, 0, 757, 338, 1334, -1, -1, 1208, 1208, 543, 828, 808, 828, 0, 69, 127, -1, -1, 127, 542, 828, 1208, 0, 0, 814, 56, 127, -1, -1, -1, 1208, 757, 1334, 1334, 0, 734, 1208, -1, -1, -1, -1, 127, 1208, 0, 127, 127, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 1, 1, 3, 3, 0, 0, 0, 3, 2, 2, 3, 0, 2, 2, 3, 0, 0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 3, 2, 2, 2, 0, 0, 3, 0, 0, 3, 0, 2, 2, 2, 2, 2, 3, 3, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 2, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 828, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 798, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        FloorSet.palm.replaceWith(floor1, this);
        FloorSet.sandstoneBrick.replaceWith(floor2, this);
        FloorSet.sandstone.replaceWith(floor3, this);
        FurnitureSet.palm.replaceWith(furniture, this);
        CarpetSet.brownbear.replaceWith(carpet, this);
        WallSet.sandstone.replaceWith(walls, this);
        if (biome.equals(BiomeRegistry.SNOW)) {
            this.replaceObject(ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("snowcrate"));
        } else if (biome.equals(BiomeRegistry.SWAMP)) {
            this.replaceObject(ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("swampcrate"));
        }
        this.addInventory(new LootTable(LootItem.between("coin", 23, 103), random.getOneOf(LootItem.between("brokencoppertool", 1, 3), LootItem.between("brokenirontool", 1, 3), LootItem.between("mapfragment", 1, 2)), random.getOneOf(LootTablePresets.oldVinylsLootTable)), random, 3, 3, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

