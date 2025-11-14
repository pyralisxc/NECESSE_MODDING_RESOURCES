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

public class ChefInvaderPreset1
extends Preset {
    public ChefInvaderPreset1(GameRandom random, Biome biome) {
        super("PRESET = {\n\twidth = 21,\n\theight = 13,\n\ttileIDs = [19, woodpathtile, 22, stonefloor, 14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, 14, 19, 19, 19, 19, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 22, 22, 14, 19, 14, 14, 14, 14, 14, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, 22, 22, 22, -1, 14, 19, 14, 14, 14, 14, 14, 14, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, 19, -1, 19, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 19, -1, 19, -1, -1, -1, -1, 19, 19, 19, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 19, 19, 19, -1, -1, -1, -1, 19, -1, 19, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 19, -1, 19, -1, -1, -1, -1, -1, -1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, 19, 19, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 326, wallcandle, 781, pottedflower3, 845, spilledgoldchalice, 846, goldchalice, 847, oldplate, 465, oakdinnertable, 337, swampcandlestand, 466, oakdinnertable2, 850, oldsoup, 468, oakmodulartable, 85, woodwall, 341, paintingapple, 469, oakchair, 853, oldchalices, 86, wooddoor, 854, forgottenblade, 90, woodwindow, 1050, cookingstation, 1051, cookingstation2, 865, brownbearcarpet, 482, oakcandelabra, 291, barrel, 803, plate, 293, coolingbox, 486, oaktoilet, 359, paintinglargeship, 360, paintinglargeship2, 809, dirtyplate, 811, stewpot, 812, cuttingboard, 815, papertowel, 763, barshelf, 764, stool, 765, sink, 766, tungstensink, 767, largekeg],\n\tobjects = [-1, -1, -1, 0, -1, -1, -1, 0, 0, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 85, 85, 90, 85, 85, 85, 90, 85, 85, 85, 85, 90, 85, 85, 85, -1, -1, -1, -1, -1, 0, 85, 468, 765, 482, 85, 767, 0, 468, 1050, 1051, 293, 766, 763, 763, 85, 291, 0, -1, -1, -1, 0, 90, 486, 865, 865, 86, 0, 764, 468, 0, 0, 0, 0, 0, 0, 90, 0, 0, 0, -1, -1, -1, 85, 781, 865, 865, 85, 0, 0, 468, 0, 468, 468, 468, 468, 468, 85, 85, 85, 0, -1, -1, -1, 85, 85, 85, 85, 85, 0, 0, 0, 0, 764, 0, 764, 0, 764, 482, 767, 85, 0, 0, -1, 0, 0, 326, 85, 482, 341, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 85, 326, 0, 0, 0, 0, 0, 86, 865, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 865, 86, 0, 0, 0, -1, 0, 0, 85, 469, 466, 469, 0, 469, 465, 469, 865, 865, 469, 465, 469, 0, 85, 0, 0, 0, -1, 0, 0, 90, 469, 465, 469, 0, 469, 466, 469, 865, 865, 469, 466, 469, 781, 90, 0, 0, -1, -1, -1, -1, 85, 85, 90, 85, 90, 85, 90, 85, 86, 86, 85, 90, 85, 90, 85, 0, -1, -1, -1, -1, -1, -1, 0, 0, -1, 0, 0, 0, 326, 0, 0, 326, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 2, 3, 2, 2, 3, 2, 0, 0, 1, 1, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 1, 0, 1, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 2, 0, 0, 2, 2, 3, 3, 2, 2, 2, 2, 2, 2, 0, 0, 2, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 2, 0, 0, 3, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 3, 0, 0, 1, 0, 3, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 0, 3, 1, 1, 2, 3, 0, 0, 1, 2, 3, 0, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 3, 1, 1, 2, 3, 0, 0, 1, 2, 3, 1, 2, 0, 0, 2, 0, 0, 0, 2, 2, 0, 2, 0, 3, 0, 2, 0, 2, 2, 0, 2, 2, 2, 0, 2, 2, 0, 0, 2, 2, 2, 0, 2, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 360, 359, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 815, 0, 0, 0, 0, 0, 337, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 845, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 811, 0, 803, 0, 850, 812, 809, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 846, 0, 0, 0, 0, 854, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 847, 0, 0, 0, 853, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        long seededRandom = random.nextLong();
        PresetUtils.applyRandomCarpetToSelection(this, 3, 3, 2, 2, 0, random);
        PresetUtils.applyRandomPot(this, 2, 4, new GameRandom(seededRandom));
        PresetUtils.applyRandomPot(this, 16, 9, new GameRandom(seededRandom));
        PresetUtils.applyRandomPainting(this, 5, 6, 2, random, PaintingSelectionTable.uncommonPaintings);
        PresetUtils.applyRandomPainting(this, 10, 2, 2, random, PaintingSelectionTable.largeRarePaintings);
        WallSet wallSet = random.getOneOf(WallSet.willow, WallSet.dryad, WallSet.bamboo);
        FurnitureSet furnitureSet = random.getOneOf(FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.willow, FurnitureSet.bamboo, FurnitureSet.dryad);
        FloorSet floorSet = random.getOneOf(FloorSet.dryad, FloorSet.bamboo);
        PathSet pathSet = random.getOneOf(PathSet.dryad);
        WallSet.wood.replaceWith(wallSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        FloorSet.wood.replaceWith(floorSet, this);
        PathSet.wood.replaceWith(pathSet, this);
        this.addInventory(new LootTable(LootItem.offset("iceblossom", 30, 6)), random, 11, 2, new Object[0]);
        this.addInventory(new LootTable(LootTablePresets.chefCoolingBox), random, 11, 2, new Object[0]);
        this.addCustomApply(13, 3, 2, (level, levelX, levelY, dir, blackboard) -> {
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

