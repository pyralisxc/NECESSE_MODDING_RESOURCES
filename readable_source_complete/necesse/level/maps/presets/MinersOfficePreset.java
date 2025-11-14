/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.BiomeOresLootTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.CrystalSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.RockAndOreSet;

public class MinersOfficePreset
extends Preset {
    public MinersOfficePreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, RockAndOreSet rocksAndWalls, CrystalSet crystal, FloorSet floor, ColumnSet columns, FurnitureSet furniture) {
        super("PRESET = {\n\twidth = 9,\n\theight = 10,\n\ttileIDs = [14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14, 14, 14, 14, 14, -1, -1, -1, 14, 14, 14, 14, 14, 14, -1, -1, -1, -1, 14, 14, 14, 14, 14, 14, -1, -1, -1, 14, 14, 14, 14, 14, 14, -1, -1, -1, 14, 14, 14, 14, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 1026, rock, 450, oakmodulartable, 451, oakchair, 1027, surfacerock, 1028, surfacerockr, 1029, surfacerocksmall, 390, sign, 1158, sapphirecluster, 263, woodcolumn, 1159, sapphireclusterr, 456, oakbed, 457, oakbed2, 1035, goldorerock, 236, woodfence, 237, woodfencegate, 468, oaktoilet, 312, candle, 446, oakchest],\n\tobjects = [1026, 1026, 1035, 1035, 1035, 1035, 1026, 1026, 1026, 1026, 1026, 1026, 1035, 1035, 1026, 1026, 1026, 1035, 1026, 1026, 263, 446, 390, 450, 263, 1035, 1035, 1026, 468, 0, 0, 0, 451, 0, 1035, 1026, 1026, 1026, 1158, 1159, 1027, 1028, 0, 457, 1026, 1026, 1026, 1158, 1159, 0, 0, 1029, 456, 1026, 1026, 1026, 263, 0, 0, 1029, 263, 1026, 1026, 1026, 236, 236, 236, 237, 236, 236, 236, 1026, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 2, 2, 0, 0, 2, 2, 2, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 2, 0, 2, 0, 0, 0, 2, 2, 2, 0, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 312, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        RockAndOreSet.forest.replaceWith(rocksAndWalls, this);
        CrystalSet.sapphire.replaceWith(crystal, this);
        FloorSet.wood.replaceWith(floor, this);
        ColumnSet.wood.replaceWith(columns, this);
        FurnitureSet.oak.replaceWith(furniture, this);
        String randomSignatureOreStringID = RockAndOreSet.getRandomSignatureOreStringID(biome, levelIdentifier, random);
        this.replaceObject(rocksAndWalls.goldOre, ObjectRegistry.getObjectID(randomSignatureOreStringID));
        this.iteratePreset((tileX, tileY) -> {
            int objectID = this.getObject((int)tileX, (int)tileY);
            if (objectID != -1 && ObjectRegistry.getObject(objectID).getStringID().contains("ore") && random.getEveryXthChance(3)) {
                this.setObject((int)tileX, (int)tileY, rocksAndWalls.rock);
            }
        });
        this.addSign("Notches:\n/|/|/ //|\\/  //\\/|  //|/", 4, 2);
        boolean validBiome = this.isValidCrystalBiome(biome, levelIdentifier);
        for (int y = 4; y < 6; ++y) {
            boolean coinflip;
            boolean bl = coinflip = random.getIntBetween(0, 1) == 1;
            if (y == 4 && coinflip && validBiome) continue;
            this.setObject(2, y, ObjectRegistry.getObjectID("air"));
            this.setObject(3, y, ObjectRegistry.getObjectID("air"));
            if (y == 4 && !coinflip && validBiome) break;
        }
        if (random.getIntBetween(0, 2) == 1) {
            this.setObject(4, 4, ObjectRegistry.getObjectID("air"));
            this.setObject(5, 4, ObjectRegistry.getObjectID("air"));
        }
        if (random.getIntBetween(0, 2) == 1) {
            this.setObject(5, 6, ObjectRegistry.getObjectID("air"));
        } else {
            this.setObject(6, 5, ObjectRegistry.getObjectID("air"));
        }
        this.addInventory(this.getLootBasedOnBiome(biome, levelIdentifier), random, 3, 2, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }

    protected LootTable getLootBasedOnBiome(Biome biome, LevelIdentifier levelIdentifier) {
        LootTable lootTable = BiomeOresLootTable.defaultOres;
        if (levelIdentifier == LevelIdentifier.CAVE_IDENTIFIER) {
            if (biome == BiomeRegistry.FOREST) {
                lootTable = new LootTable(new LootItem("ironpickaxe"), BiomeOresLootTable.forestOres);
            } else if (biome == BiomeRegistry.SNOW) {
                lootTable = new LootTable(new LootItem("frostpickaxe"), BiomeOresLootTable.snowOres);
            } else if (biome == BiomeRegistry.PLAINS) {
                lootTable = new LootTable(new LootItem("runicpickaxe"), BiomeOresLootTable.plainsOres);
            } else if (biome == BiomeRegistry.SWAMP) {
                lootTable = new LootTable(new LootItem("ivypickaxe"), BiomeOresLootTable.swampOres);
            } else if (biome == BiomeRegistry.DESERT) {
                lootTable = new LootTable(new LootItem("quartzpickaxe"), BiomeOresLootTable.desertOres);
            }
        } else if (levelIdentifier == LevelIdentifier.DEEP_CAVE_IDENTIFIER) {
            if (biome == BiomeRegistry.FOREST) {
                lootTable = new LootTable(new LootItem("tungstenpickaxe"), BiomeOresLootTable.deepForestOres);
            } else if (biome == BiomeRegistry.SNOW) {
                lootTable = new LootTable(new LootItem("glacialpickaxe"), BiomeOresLootTable.deepSnowOres);
            } else if (biome == BiomeRegistry.PLAINS) {
                lootTable = new LootTable(new LootItem("dryadpickaxe"), BiomeOresLootTable.deepPlainsOres);
            } else if (biome == BiomeRegistry.SWAMP) {
                lootTable = new LootTable(new LootItem("myceliumpickaxe"), BiomeOresLootTable.deepSwampOres);
            } else if (biome == BiomeRegistry.DESERT) {
                lootTable = new LootTable(new LootItem("ancientfossilpickaxe"), BiomeOresLootTable.deepDesertOres);
            }
        }
        return lootTable;
    }

    protected boolean isValidCrystalBiome(Biome biome, LevelIdentifier identifier) {
        return (biome.equals(BiomeRegistry.FOREST) || biome.equals(BiomeRegistry.DESERT)) && identifier.equals(LevelIdentifier.CAVE_IDENTIFIER) || (biome.equals(BiomeRegistry.FOREST) || biome.equals(BiomeRegistry.PLAINS) || biome.equals(BiomeRegistry.SWAMP)) && identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }
}

