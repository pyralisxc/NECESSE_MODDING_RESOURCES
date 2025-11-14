/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.critters.FrogMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.HedgeSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class ForgottenShrinePreset
extends Preset {
    public ForgottenShrinePreset(GameRandom random, FurnitureSet furnitureSet, WallSet wallSet, TreeSet treeSet, HedgeSet hedgeSet) {
        super("PRESET = {\n\twidth = 14,\n\theight = 11,\n\ttileIDs = [2, watertile, 23, stonebrickfloor, 42, graniterocktile, 46, graveltile],\n\ttiles = [-1, -1, -1, -1, -1, 46, -1, 46, 46, -1, 46, 46, 46, -1, -1, -1, -1, -1, -1, 46, 46, 46, 46, 46, 46, 46, 46, -1, -1, -1, -1, -1, 46, 46, 42, 42, 42, 42, 42, 46, 46, -1, -1, -1, -1, 46, 46, 42, 42, 23, 23, 23, 42, 42, 46, 46, -1, 2, -1, -1, 46, 42, 23, 42, 23, 23, 23, 42, 46, 46, -1, 2, 2, -1, 46, 42, 23, 42, 42, 23, 23, 42, 46, 46, -1, -1, 2, -1, 46, 42, 23, 23, 23, 42, 23, 42, 46, 46, -1, -1, -1, 46, 46, 42, 42, 23, 23, 23, 42, 42, 46, -1, -1, -1, -1, 46, 46, 46, 42, 42, 42, 42, 42, 46, 46, 46, -1, -1, -1, -1, -1, 46, 46, 46, 46, 46, 46, 46, -1, -1, -1, -1, -1, -1, -1, 46, -1, -1, -1, -1, 46, 46, -1, -1],\n\tobjectIDs = [0, air, 266, granitecolumn, 1072, granitecaverocksmall, 145, granitewall, 401, gravestone1, 817, forgottenblade, 146, granitedoor, 756, bonsaitree2, 21, birchtree, 1334, crate, 1208, cobweb, 1338, vase, 314, stonecandlepedestal, 442, steppingstones, 603, birchdisplay, 443, largesteppingstone, 444, mossysteppingstones, 445, largemossysteppingstone, 254, plainshedge],\n\tobjects = [-1, -1, -1, -1, 254, 254, 254, 254, 254, 0, 0, 0, 254, -1, -1, -1, 0, 254, 254, 0, 0, 0, 0, 0, 0, 0, 254, 254, -1, 0, 21, 254, 0, 0, 0, 145, 0, 145, 145, 0, 21, 254, 0, 0, 0, 1072, 0, 145, 0, 266, 603, 266, 145, 145, 0, 254, 0, 0, 0, 0, 0, 145, 756, 1338, 0, 1208, 314, 145, 401, 0, 0, 0, 0, 445, 444, 0, 0, 0, 0, 0, 0, 0, 817, 0, 0, 0, 0, 0, 0, 145, 1208, 0, 0, 0, 1208, 145, 0, 254, -1, 0, 0, 0, 0, 145, 145, 1208, 0, 1334, 145, 145, 0, 254, -1, -1, 0, 254, 0, 0, 145, 145, 146, 145, 145, 0, 0, 254, -1, -1, -1, 254, 254, 0, 254, 0, 442, 0, 254, 0, 254, 254, -1, -1, -1, -1, 254, 254, 254, 0, 443, 0, 0, 254, 254, -1],\n\trotations = [3, 3, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 0, 2, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 2, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 3, 3, 2, 0, 2, 3, 3, 2, 0, 2, 2, 0, 3, 0, 0, 1, 1, 0, 0, 1, 2, 1, 3, 2, 2, 2, 3, 3, 0, 3, 0, 0, 0, 2, 0, 3, 3, 2, 2, 2, 2, 3, 3, 3, 0, 2, 0, 2, 0, 0, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        if (furnitureSet != null) {
            FurnitureSet.birch.replaceWith(furnitureSet, this);
        }
        if (wallSet != null) {
            WallSet.granite.replaceWith(wallSet, this);
        }
        if (treeSet != null) {
            TreeSet.birch.replaceWith(treeSet, this);
        }
        if (hedgeSet != null) {
            HedgeSet.plains.replaceWith(hedgeSet, this);
        }
        this.addInventory(new LootTable(LootItem.between("revivalpotion", 1, 3)), random, 8, 3, new Object[0]);
        ArrayList<Point> frogSpawnPositions = new ArrayList<Point>();
        frogSpawnPositions.add(new Point(3, 6));
        frogSpawnPositions.add(new Point(7, 5));
        frogSpawnPositions.add(new Point(9, 4));
        frogSpawnPositions.add(new Point(9, 6));
        frogSpawnPositions.add(new Point(9, 9));
        frogSpawnPositions.add(new Point(5, 1));
        for (Point frogSpawnPosition : frogSpawnPositions) {
            this.addCustomApply(frogSpawnPosition.x, frogSpawnPosition.y, 0, (level, levelX, levelY, dir, blackboard) -> {
                FrogMob frog = (FrogMob)MobRegistry.getMob("frog", level);
                frog.canDespawn = false;
                Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, frog);
                level.entityManager.addMob(frog, spawnLocation.x, spawnLocation.y);
                return (level1, presetX, presetY) -> frog.remove();
            });
        }
    }
}

