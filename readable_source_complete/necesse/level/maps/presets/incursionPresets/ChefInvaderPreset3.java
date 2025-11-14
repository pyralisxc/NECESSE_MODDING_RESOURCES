/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.incursionPresets;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.theRunebound.BattleChefMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
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

public class ChefInvaderPreset3
extends Preset {
    public ChefInvaderPreset3(GameRandom random, Biome biome) {
        super("PRESET = {\n\twidth = 19,\n\theight = 20,\n\ttileIDs = [19, woodpathtile, 22, stonefloor, 23, stonebrickfloor, 14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, -1, -1, -1, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, 19, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, 19, -1, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, -1, -1, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, -1, 23, 23, 23, -1, -1, -1, -1, 19, 14, 14, 14, 14, 19, 14, 14, 14, 14, 19, -1, 23, 23, 23, -1, -1, -1, -1, 19, 14, 19, 19, 19, 19, 19, 19, 19, 14, 19, -1, 23, 23, 23, -1, -1, -1, -1, 19, 14, 19, 14, 14, 19, 14, 14, 19, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 19, 14, 19, 19, 19, 14, 19, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 19, 14, 14, 14, 14, 14, 19, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 19, 14, 14, 14, 14, 14, 19, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 19, 19, 19, 19, 19, 19, 19, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 14, 14, 14, 14, 14, 14, 14, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, -1, -1, -1, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, 14, 14, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 323, walllantern, 325, candle, 326, wallcandle, 847, oldplate, 465, oakdinnertable, 849, rottenfishstew, 337, swampcandlestand, 466, oakdinnertable2, 850, oldsoup, 467, oakdesk, 468, oakmodulartable, 85, woodwall, 469, oakchair, 341, paintingapple, 86, wooddoor, 1048, cookingpot, 408, sign, 793, bonsaitree2, 90, woodwindow, 1050, cookingstation, 1051, cookingstation2, 859, roastedduck, 1052, compostbin, 860, roastedduck2, 481, oakclock, 865, brownbearcarpet, 482, oakcandelabra, 291, barrel, 804, mug, 293, coolingbox, 805, teapot, 807, bluediningset, 809, dirtyplate, 810, dirtydishes, 811, stewpot, 812, cuttingboard, 815, papertowel, 816, stackofpaper, 824, quillandparchment, 825, observantmask, 826, unamusedmask, 763, barshelf, 764, stool, 765, sink, 767, largekeg],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, 0, -1, -1, 0, 0, -1, -1, 0, 0, -1, -1, -1, -1, -1, 0, 0, 291, 85, 85, 90, 85, 85, 90, 85, 85, 85, 85, 90, 85, 85, -1, 0, 0, 323, 85, 90, 85, 481, 466, 465, 482, 466, 465, 763, 763, 468, 765, 482, 85, -1, 0, 0, 0, 86, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 466, 90, 0, -1, 0, 1052, 85, 467, 469, 0, 0, 0, 468, 1048, 1051, 1050, 0, 0, 0, 465, 85, -1, -1, 85, 85, 85, 85, 85, 85, 86, 85, 85, 85, 85, 85, 85, 85, 86, 85, 85, -1, -1, 85, 469, 468, 469, 341, 326, 0, 326, 341, 469, 468, 469, 85, 293, 0, 767, 85, -1, 0, 85, 0, 865, 865, 865, 865, 0, 865, 865, 865, 865, 0, 85, 323, 0, 767, 85, -1, 0, 90, 0, 865, 0, 0, 0, 0, 0, 0, 0, 865, 0, 85, 291, 0, 767, 85, -1, -1, 85, 341, 865, 0, 468, 468, 0, 468, 468, 0, 865, 341, 85, 85, 90, 85, 85, -1, -1, 85, 469, 865, 764, 468, 0, 0, 0, 468, 764, 865, 469, 85, 0, 0, -1, -1, -1, 0, 90, 468, 865, 764, 468, 468, 468, 468, 468, 764, 865, 468, 90, 0, -1, -1, -1, -1, 0, 85, 469, 865, 0, 468, 468, 468, 468, 468, 0, 865, 469, 85, -1, -1, -1, -1, -1, -1, 85, 341, 865, 0, 0, 764, 764, 764, 0, 0, 865, 341, 85, -1, -1, -1, -1, -1, 0, 90, 0, 865, 865, 865, 865, 865, 865, 865, 865, 865, 0, 90, 0, -1, -1, -1, -1, -1, 85, 469, 468, 469, 0, 469, 468, 469, 0, 469, 468, 469, 85, 0, -1, -1, -1, -1, -1, 85, 85, 90, 85, 86, 85, 90, 85, 86, 85, 90, 85, 85, -1, -1, -1, -1, -1, -1, -1, 0, 0, 326, 0, 468, 408, 468, 0, 326, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 2, 2, 2, 2, 0, 2, 2, 0, 0, 0, 3, 2, 0, 2, 2, 3, 3, 2, 3, 3, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 1, 3, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 0, 0, 0, 2, 0, 0, 3, 1, 0, 3, 2, 2, 0, 2, 2, 1, 0, 3, 3, 2, 0, 3, 2, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 0, 3, 2, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 3, 2, 2, 3, 2, 0, 1, 3, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 3, 3, 0, 2, 0, 0, 0, 1, 3, 2, 0, 1, 1, 0, 0, 0, 1, 3, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1, 2, 1, 1, 1, 2, 1, 0, 0, 3, 0, 0, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 3, 3, 0, 0, 0, 0, 1, 3, 3, 0, 3, 2, 3, 0, 3, 3, 3, 0, 3, 3, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 859, 860, 0, 815, 812, 0, 0, 849, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 809, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 816, 0, 0, 0, 0, 0, 0, 810, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 805, 0, 0, 0, 0, 0, 0, 0, 850, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 859, 860, 0, 815, 793, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 807, 0, 0, 0, 807, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 325, 0, 0, 807, 811, 847, 812, 807, 0, 0, 325, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 337, 807, 807, 807, 337, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 804, 0, 0, 0, 824, 0, 0, 0, 809, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 825, 0, 826, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 3, 3, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        long seededRandom = random.nextLong();
        PresetUtils.applyRandomCarpetToSelection(this, 3, 7, 4, 1, 0, new GameRandom(seededRandom));
        PresetUtils.applyRandomCarpetToSelection(this, 8, 7, 4, 1, 0, new GameRandom(seededRandom));
        PresetUtils.applyRandomCarpetToSelection(this, 3, 8, 1, 7, 0, new GameRandom(seededRandom));
        PresetUtils.applyRandomCarpetToSelection(this, 11, 8, 1, 7, 0, new GameRandom(seededRandom));
        PresetUtils.applyRandomCarpetToSelection(this, 4, 14, 7, 1, 0, new GameRandom(seededRandom));
        PresetUtils.applyRandomPainting(this, 5, 6, 2, new GameRandom(seededRandom), PaintingSelectionTable.uncommonPaintings);
        PresetUtils.applyRandomPainting(this, 9, 6, 2, new GameRandom(seededRandom), PaintingSelectionTable.uncommonPaintings);
        WallSet wallSet = random.getOneOf(WallSet.willow, WallSet.dryad);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.willow, FurnitureSet.bamboo, FurnitureSet.dryad);
        FloorSet floorSet = random.getOneOf(FloorSet.dryad, FloorSet.bamboo);
        PathSet pathSet = random.getOneOf(PathSet.dryad);
        WallSet.wood.replaceWith(wallSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        FloorSet.wood.replaceWith(floorSet, this);
        PathSet.wood.replaceWith(pathSet, this);
        this.addInventory(new LootTable(LootItem.offset("iceblossom", 30, 6)), random, 14, 6, new Object[0]);
        this.addInventory(new LootTable(LootTablePresets.chefCoolingBox), random, 14, 6, new Object[0]);
        AtomicReference battleChefRef = new AtomicReference();
        this.addCustomApply(10, 7, 10, (level, levelX, levelY, dir, blackboard) -> {
            BattleChefMob battleChef = (BattleChefMob)MobRegistry.getMob("battlechef", level);
            battleChef.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, battleChef);
            battleChef.onSpawned(spawnLocation.x, spawnLocation.y);
            level.entityManager.addMob(battleChef, spawnLocation.x, spawnLocation.y);
            battleChefRef.set(battleChef);
            return (level1, presetX, presetY) -> battleChef.remove();
        });
        this.addCustomApply(7, 17, 2, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    BattleChefMob battleChefMob = (BattleChefMob)battleChefRef.get();
                    if (battleChefMob != null) {
                        ((SignObjectEntity)objEnt).setText(battleChefMob.getDisplayName() + "'s Diner");
                    }
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            battleChefRef.set(null);
            return null;
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

