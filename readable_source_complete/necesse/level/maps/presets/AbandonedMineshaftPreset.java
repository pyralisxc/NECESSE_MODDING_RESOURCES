/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.BiomeOresLootTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class AbandonedMineshaftPreset
extends Preset {
    public AbandonedMineshaftPreset(Biome biome, GameRandom random, RockAndOreSet rockAndOreSet, WallSet wallSet, FurnitureSet furnitureSet, TreeSet treeSet, String[] mobIDs) {
        super("PRESET = {\n\twidth = 19,\n\theight = 19,\n\ttileIDs = [11, rocktile, 14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, -1, -1, -1, -1, -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 14, 11, 11, 11, 14, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, 11, 11, 11, 14, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 14, 11, 11, 11, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 14, 11, 11, 11, -1, -1, -1, -1, -1, -1, 11, 11, 11, 14, 11, 11, 11, 11, 11, 14, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, -1, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 449, oakdesk, 1026, rock, 1027, surfacerock, 451, oakchair, 1028, surfacerockr, 1029, surfacerocksmall, 6, sprucetreestump, 390, sign, 7, sprucelogbench, 8, sprucelogbench2, 1033, ironorerock, 1034, copperorerock, 1035, goldorerock, 850, rockleveractive, 405, minecarttrack, 85, woodwall, 278, barrel, 86, wooddoor, 284, torch, 236, woodfence, 757, skull, 1334, crate, 56, forge, 1208, cobweb, 1211, ladderdown, 828, brownbearcarpet, 446, oakchest],\n\tobjects = [-1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1027, 1028, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, 1026, 1026, 1026, 0, 0, 0, 1029, 0, 0, 1026, 1026, 0, -1, -1, -1, -1, 0, 0, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 0, 0, 1026, 1026, 1026, 0, -1, -1, -1, -1, 0, 0, 1026, 1026, 1035, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 0, 0, -1, -1, -1, 0, 0, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1034, 1026, 1026, 1027, 1028, 0, -1, -1, 0, 0, 1026, 1026, 1026, 1026, 0, 446, 236, 1026, 1026, 1034, 1034, 1026, 1026, 1029, 0, -1, -1, 0, 0, 1026, 1026, 1026, 236, 0, 0, 0, 0, 0, 1334, 1034, 1026, 1026, 0, 0, -1, 0, 0, 1026, 1026, 1026, 757, 0, 0, 0, 0, 0, 1027, 1028, 56, 1026, 1026, 1026, 0, -1, 0, 1029, 1026, 1026, 1034, 0, 1211, 850, 1033, 1026, 1026, 0, 0, 0, 1026, 1026, 1026, 0, -1, -1, 0, 1026, 1034, 1034, 0, 0, 1033, 1033, 1026, 1026, 0, 0, 0, 1026, 1026, 1026, 0, -1, -1, 0, 1026, 1034, 1034, 1026, 1026, 1026, 1026, 236, 0, 0, 405, 1029, 236, 1026, 1026, 1026, 0, 0, 0, 1026, 1026, 1026, 1026, 449, 0, 1029, 0, 0, 0, 405, 405, 0, 1026, 1026, 1026, 0, 1026, 1026, 1026, 1026, 1026, 1026, 828, 0, 0, 451, 1334, 1026, 0, 0, 0, 1026, 1026, 1026, 0, 0, 0, 1026, 1026, 1026, 1026, 1026, 236, 0, 278, 1026, 1026, 1027, 1028, 1029, 1208, 1026, 1026, 0, 0, 0, 7, 8, 1026, 1026, 1026, 1026, 0, 1026, 1026, 0, 0, 405, 0, 0, 1026, 0, 0, -1, 0, 0, 0, 0, 1026, 1026, 1026, 1208, 1026, 1026, 85, 85, 86, 85, 85, 1026, 0, -1, -1, 0, 0, 6, 0, 0, 1026, 1026, 1026, 1026, 1026, 1208, 0, 0, 390, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 1026, 1026, 1026, 0, 0, 0, 0, 0, 0, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 0, 3, 3, 3, 3, 3, 3, 0, 1, 1, 3, 3, 3, 1, 3, 3, 1, 1, 3, 0, 3, 3, 3, 3, 3, 0, 0, 3, 2, 2, 2, 3, 3, 3, 1, 1, 2, 3, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 1, 1, 0, 3, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 3, 0, 3, 3, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 0, 0, 1, 1, 1, 3, 0, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 3, 3, 0, 3, 3, 2, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 1, 3, 0, 3, 1, 2, 0, 2, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 3, 2, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 3, 2, 2, 2, 0, 0, 0, 0, 3, 0, 0, 0, 1, 3, 0, 3, 2, 3, 3, 3, 2, 2, 0, 0, 2, 0, 2, 0, 0, 0, 3, 2, 0, 2, 2, 2, 3, 3, 3, 3, 2, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 2, 1, 2, 3, 3, 3, 3, 2, 2, 0, 0, 3, 0, 1, 0, 0, 2, 2, 2, 3, 1, 2, 3, 2, 3, 1, 1, 2, 2, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 1, 3, 3, 2, 2, 2, 2, 3, 3, 3, 3, 3, 0, 3, 0, 2, 3, 2, 0, 1, 3, 3, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 2, 2, 0, 3, 3, 0, 2, 2, 3, 2, 2, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 0, 284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 87, 87, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        RockAndOreSet.forest.replaceWith(rockAndOreSet, this);
        WallSet.wood.replaceWith(wallSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        TreeSet.spruce.replaceWith(treeSet, this);
        LootTable chestLoot = new LootTable(BiomeOresLootTable.defaultOres);
        if (biome == BiomeRegistry.FOREST) {
            chestLoot = new LootTable(BiomeOresLootTable.forestOres, new LootItem("goldpickaxe"));
        } else if (biome == BiomeRegistry.SNOW) {
            chestLoot = new LootTable(BiomeOresLootTable.snowOres, new LootItem("frostpickaxe"));
        } else if (biome == BiomeRegistry.PLAINS) {
            chestLoot = new LootTable(BiomeOresLootTable.plainsOres, new LootItem("demonicpickaxe"));
        } else if (biome == BiomeRegistry.SWAMP) {
            chestLoot = new LootTable(BiomeOresLootTable.swampOres, new LootItem("demonicpickaxe"));
        } else if (biome == BiomeRegistry.DESERT) {
            chestLoot = new LootTable(BiomeOresLootTable.desertOres, new LootItem("demonicpickaxe"));
        }
        this.addInventory(chestLoot, random, 8, 6, new Object[0]);
        this.addCustomApplyAreaEach(8, 8, 11, 8, 0, (level, levelX, levelY, dir, blackboard) -> {
            Mob mob = MobRegistry.getMob(random.getOneOf(mobIDs), level);
            mob.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, mob);
            level.entityManager.addMob(mob, spawnLocation.x, spawnLocation.y);
            return (level1, presetX, presetY) -> mob.remove();
        });
        this.addCustomApply(14, 17, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    ((SignObjectEntity)objEnt).setText("DANGER\n\nABANDONED MINESHAFT\n\nKEEP OUT!");
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return null;
        });
    }
}

