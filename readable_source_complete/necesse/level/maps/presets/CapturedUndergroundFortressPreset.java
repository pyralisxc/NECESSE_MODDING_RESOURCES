/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.presets.CaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CapturedUndergroundFortressPreset
extends Preset {
    public CapturedUndergroundFortressPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, FurnitureSet furniture, WallSet walls, FloorSet floor, ColumnSet column, WallSet wallblock) {
        super("PRESET = {\n\twidth = 18,\n\theight = 14,\n\ttileIDs = [3, grasstile, 4, overgrowngrasstile, 22, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, -1, -1, 22, 22, -1, 22, 22, 22, -1, 22, 22, -1, -1, -1, -1, -1, 22, 22, 22, 22, 22, 22, -1, 22, 22, 22, -1, 22, 22, 22, -1, -1, -1, -1, 22, 22, 22, 22, 22, 22, 4, 22, 22, 3, -1, 22, 22, 22, 22, -1, -1, -1, 22, 22, -1, -1, 22, 22, -1, 22, 22, 22, -1, 22, 22, 22, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, 22, 22, 22, 22, 22, -1, -1, 22, 22, 22, 22, 22, 22, -1, -1, 22, -1, -1, 22, 22, 22, 22, 22, -1, -1, 22, 22, 22, 22, 22, 22, 4, 22, 22, 22, -1, 22, 22, 22, 22, 22, -1, -1, 22, 22, 22, 22, 22, 22, 3, 22, 22, 22, -1, 22, 22, 22, 22, -1, -1, -1, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 3, -1, -1, -1, -1, 22, 22, 22, 22, 22, 22, -1, 22, 22, 22, -1, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 448, oakdinnertable2, 451, oakchair, 1029, surfacerocksmall, 325, paintingbroken, 454, oakbookshelf, 264, stonecolumn, 401, gravestone1, 402, gravestone2, 404, cryptgravestone2, 85, woodwall, 285, walltorch, 479, sprucebed, 480, sprucebed2, 355, hoodedknightstatue, 811, rottenpigdish, 812, rottenfishstew, 814, brokenplate, 239, stonefence, 816, oldchalices, 434, bloodstains, 757, skull, 309, lantern, 1334, crate, 758, mosscoveredskull, 312, candle, 1208, cobweb, 760, bloodgobletspilled, 121, stonewall, 1338, vase, 122, stonedoor, 828, brownbearcarpet, 446, oakchest, 447, oakdinnertable],\n\tobjects = [121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, -1, -1, -1, 121, 446, 1334, 121, 121, 1334, 1029, 121, 1338, 325, 1338, 121, 1338, 1334, 121, 121, -1, -1, 121, 0, 0, 312, 1208, 1208, 0, 121, 0, 0, 1334, 121, 0, 451, 451, 121, 121, -1, 121, 0, 0, 0, 0, 434, 0, 85, 0, 0, 758, 121, 0, 447, 448, 0, 121, 121, 121, 828, 828, 121, 121, 757, 434, 121, 1338, 1208, 1334, 121, 0, 0, 451, 0, 0, 121, 121, 121, 121, 121, 121, 121, 121, 121, 121, 85, 121, 121, 0, 0, 0, 447, 451, 121, 121, 402, 239, 401, 239, 404, 1334, 121, 121, 0, 121, 121, 0, 0, 0, 448, 814, 121, 121, 0, 0, 0, 0, 0, 758, 121, 454, 454, 454, 121, 0, 0, 0, 0, 0, 121, 121, 0, 0, 0, 0, 0, 0, 121, 0, 0, 285, 121, 0, 448, 447, 760, 121, 121, 121, 480, 434, 480, 0, 0, 0, 122, 0, 0, 0, 122, 0, 451, 451, 121, 121, -1, 121, 479, 757, 479, 309, 434, 434, 121, 264, 0, 355, 121, 285, 0, 121, 121, -1, -1, 121, 121, 121, 121, 121, 121, 121, 121, 121, 122, 121, 121, 121, 121, 121, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 3, 3, 0, 2, 1, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 1, 3, 1, 1, 2, 2, 2, 1, 0, 2, 2, 0, 0, 0, 0, 3, 3, 2, 1, 1, 2, 3, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 2, 2, 3, 2, 1, 1, 1, 2, 0, 3, 1, 2, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 1, 0, 0, 0, 2, 3, 0, 3, 2, 2, 2, 2, 2, 1, 3, 1, 2, 1, 1, 0, 0, 0, 2, 2, 0, 3, 2, 2, 0, 2, 2, 1, 3, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 3, 1, 0, 3, 3, 2, 0, 0, 3, 0, 2, 0, 0, 2, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 0, 2, 0, 1, 2, 2, 2, 2, 0, 3, 1, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 828, 828, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 325, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 757, 816, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 312, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 812, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 811, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        FurnitureSet.oak.replaceWith(furniture, this);
        WallSet.stone.replaceWith(walls, this);
        FloorSet.stone.replaceWith(floor, this);
        ColumnSet.stone.replaceWith(column, this);
        WallSet.wood.replaceWith(wallblock, this);
        String mobStringIDsForBiomeLevel = this.getRandomHostileMobNameForBiomeLevelFromGiven(biome, levelIdentifier, random, "skeleton", "zombie", "goblin", "ninja", "vampire", "dwarf", "mummy", "bonewalker");
        this.addMob(mobStringIDsForBiomeLevel, 9, 8, false);
        this.addMob(mobStringIDsForBiomeLevel, 4, 8, false);
        this.addMob(mobStringIDsForBiomeLevel, 13, 5, false);
        this.addMobs(1, 3, false, "human");
        LootTable loot = CaveChestLootTable.basicChest;
        if (levelIdentifier == LevelIdentifier.CAVE_IDENTIFIER) {
            if (biome == BiomeRegistry.SNOW) {
                loot = CaveChestLootTable.snowChest;
            } else if (biome == BiomeRegistry.PLAINS) {
                loot = CaveChestLootTable.plainsChest;
            } else if (biome == BiomeRegistry.SWAMP) {
                loot = CaveChestLootTable.swampChest;
            } else if (biome == BiomeRegistry.DESERT) {
                loot = CaveChestLootTable.desertChest;
            }
        } else if (levelIdentifier == LevelIdentifier.DEEP_CAVE_IDENTIFIER) {
            if (biome == BiomeRegistry.SNOW) {
                loot = DeepCaveChestLootTable.snowDeepCaveChest;
            } else if (biome == BiomeRegistry.PLAINS) {
                loot = DeepCaveChestLootTable.plainsDeepCaveChest;
            } else if (biome == BiomeRegistry.SWAMP) {
                loot = DeepCaveChestLootTable.swampDeepCaveChest;
            } else if (biome == BiomeRegistry.DESERT) {
                loot = DeepCaveChestLootTable.desertDeepCaveChest;
            }
        }
        this.addInventory(loot, random, 1, 1, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

