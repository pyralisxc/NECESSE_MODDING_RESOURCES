/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.incursionPresets;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.theRunebound.BattleChefMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PathSet;
import necesse.level.maps.presets.set.WallSet;

public class ChefInvaderPreset2
extends Preset {
    public ChefInvaderPreset2(GameRandom random, Biome biome) {
        super("PRESET = {\n\twidth = 20,\n\theight = 17,\n\ttileIDs = [19, woodpathtile, 22, stonefloor, 14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, 22, -1, 22, -1, -1, 22, 22, 22, 22, -1, 22, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, -1, 22, -1, -1, 22, -1, 22, 22, -1, 22, -1, -1, -1, -1, -1, -1, -1, 22, 22, -1, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, -1, -1, -1, -1, 22, 22, -1, -1, -1, 22, 22, 22, 22, 22, 22, 22, -1, -1, 22, 22, -1, -1, -1, -1, -1, 22, 22, 22, 22, 22, 19, 14, 14, 14, 19, 22, 22, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, 22, -1, 22, 19, 14, 14, 14, 19, 22, -1, 22, 22, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, 22, 19, 19, 19, 19, 19, 22, -1, -1, 22, -1, -1, -1, -1, -1, -1, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, 22, 22, 22, -1, -1, -1, -1, -1, 22, 22, -1, 22, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, 22, -1, 22, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, -1, 22, -1, 22, 22, -1, -1, -1, 22, 22, -1, -1, -1, -1, -1, -1, 22, 22, -1, 22, -1, 22, 22, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, 22, 22, -1, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 323, walllantern, 843, stuffedpig, 844, stuffedpig2, 845, spilledgoldchalice, 782, pottedflower4, 465, oakdinnertable, 466, oakdinnertable2, 339, brazier, 851, brokenplate, 468, oakmodulartable, 469, oakchair, 85, woodwall, 341, paintingapple, 853, oldchalices, 470, oakbench, 86, wooddoor, 471, oakbench2, 857, halfeatenduck, 90, woodwindow, 858, halfeatenduck2, 796, bloodgoblet, 293, coolingbox, 808, reddiningset, 810, dirtydishes, 309, copperstreetlamp, 763, barshelf, 764, stool, 767, largekeg],\n\tobjects = [-1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 468, 469, 0, 0, 470, 471, 309, -1, -1, -1, -1, -1, 0, 309, 782, 0, 0, 0, 0, 0, 469, 339, 0, 0, 0, 0, 0, 782, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 763, 767, 782, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 469, 466, 0, 0, 0, 85, 85, 85, 90, 85, 782, 0, 0, 0, 0, 0, 0, -1, -1, 0, 469, 465, 469, 0, 0, 86, 341, 763, 468, 85, 767, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 85, 0, 0, 0, 90, 767, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 782, 85, 468, 468, 468, 85, 468, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 323, 0, 764, 764, 293, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 469, 0, 0, 0, -1, -1, -1, 0, 0, 469, 468, 468, 469, 0, 0, 0, 0, 0, 0, 0, 466, 465, 469, 0, -1, -1, -1, 0, 0, 0, 468, 468, 0, 0, 0, 0, 0, 0, 0, 0, 469, 469, 0, 0, -1, -1, 0, 0, 0, 469, 468, 468, 469, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 782, 309, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 0, 309, 782, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [2, 2, 3, 3, 3, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3, 3, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 0, 1, 1, 0, 0, 0, 2, 2, 2, 0, 0, 1, 0, 0, 0, 0, 0, 1, 3, 3, 0, 0, 0, 1, 1, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 1, 0, 0, 0, 0, 3, 3, 3, 3, 3, 1, 0, 0, 0, 0, 0, 0, 2, 2, 0, 1, 0, 3, 0, 0, 3, 2, 2, 2, 3, 1, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 2, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 3, 3, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 2, 0, 0, 1, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 2, 2, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 1, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 1, 3, 2, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 2, 3, 0, 0, 2, 2, 2, 2, 2, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 853, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 851, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 810, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 796, 0, 782, 0, 845, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 808, 808, 0, 0, 0, 0, 0, 0, 0, 0, 857, 858, 0, 0, 0, 0, 0, 0, 0, 0, 843, 844, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 808, 808, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        long seededRandom = random.nextLong();
        long seededRandom2 = random.nextLong();
        PresetUtils.applyRandomPot(this, 5, 3, new GameRandom(seededRandom));
        PresetUtils.applyRandomPot(this, 18, 3, new GameRandom(seededRandom));
        PresetUtils.applyRandomPot(this, 1, 15, new GameRandom(seededRandom));
        PresetUtils.applyRandomPot(this, 17, 15, new GameRandom(seededRandom));
        PresetUtils.applyRandomPot(this, 7, 9, new GameRandom(seededRandom2));
        PresetUtils.applyRandomPot(this, 10, 5, new GameRandom(seededRandom2));
        PresetUtils.applyRandomPot(this, 13, 6, new GameRandom(seededRandom2));
        PresetUtils.applyRandomPot(this, 11, 9, new GameRandom(seededRandom2));
        PresetUtils.applyRandomPainting(this, 9, 7, 2, random, PaintingSelectionTable.uncommonPaintings);
        WallSet wallSet = random.getOneOf(WallSet.willow, WallSet.dryad, WallSet.bamboo);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.willow, FurnitureSet.bamboo, FurnitureSet.dryad);
        FloorSet floorSet = random.getOneOf(FloorSet.dryad, FloorSet.bamboo);
        PathSet pathSet = random.getOneOf(PathSet.dryad);
        WallSet.wood.replaceWith(wallSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        FloorSet.wood.replaceWith(floorSet, this);
        PathSet.wood.replaceWith(pathSet, this);
        FloorSet.stone.replaceWith(FloorSet.dryad, this);
        this.addInventory(new LootTable(LootItem.offset("iceblossom", 30, 6)), random, 12, 10, new Object[0]);
        this.addInventory(new LootTable(LootTablePresets.chefCoolingBox), random, 12, 10, new Object[0]);
        this.addCustomApply(10, 8, 2, (level, levelX, levelY, dir, blackboard) -> {
            BattleChefMob battleChef = (BattleChefMob)MobRegistry.getMob("battlechef", level);
            battleChef.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, battleChef);
            battleChef.onSpawned(spawnLocation.x, spawnLocation.y);
            level.entityManager.addMob(battleChef, spawnLocation.x, spawnLocation.y);
            return (level1, presetX, presetY) -> battleChef.remove();
        });
        this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (!level.isLiquidTile(x, y)) continue;
                    return false;
                }
            }
            return true;
        });
    }
}

