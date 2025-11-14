/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.incursionPresets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.CrazedRavenMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;

public class RavenInvadersPreset2
extends Preset {
    public RavenInvadersPreset2(GameRandom random) {
        super("PRESET = {\n\twidth = 13,\n\theight = 14,\n\ttileIDs = [67, ravenfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, 67, -1, -1, -1, -1, -1, -1, 67, -1, -1, 67, 67, 67, -1, 67, 67, -1, -1, 67, -1, -1, -1, 67, 67, 67, 67, 67, 67, 67, -1, -1, 67, -1, 67, 67, 67, 67, 67, 67, 67, 67, -1, 67, -1, -1, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, 67, 67, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, -1, -1, 67, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, 67, -1, -1, 67, -1, -1, -1, -1, -1, 67, -1, 67, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 289, sack, 740, emptyravennest, 741, emptyravennest2, 325, basalttorch, 326, wallbasalttorch, 742, emptyravennest3, 743, emptyravennest4, 744, ravennestwithegg, 745, ravennestwithegg2, 362, ravenstatue, 746, ravennestwithegg3, 235, ravenwall, 747, ravennestwithegg4, 236, ravendoor, 748, ravenskull, 749, ravenskull2, 750, ravenskull3, 751, ravenskull4, 240, ravenwindow, 341, paintingparrot],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 235, 235, 235, 236, 235, -1, -1, -1, -1, -1, -1, -1, 235, 235, 740, 741, 0, 235, 235, -1, -1, -1, 0, -1, 235, 235, 326, 742, 743, 744, 745, 235, 235, -1, -1, 0, 0, 235, 362, 0, 0, 0, 746, 747, 289, 235, -1, 0, 0, 0, 240, 744, 745, 0, 0, 0, 744, 745, 240, 0, 0, 0, -1, 235, 746, 747, 0, 0, 0, 746, 747, 235, -1, 0, -1, -1, 235, 235, 325, 0, 740, 741, 325, 235, 235, -1, -1, -1, -1, -1, 235, 235, 0, 742, 743, 235, 235, -1, -1, -1, -1, -1, -1, -1, 235, 236, 235, 240, 235, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 748, 749, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 750, 751, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 1, 0, 3, 3, 0, 0, 0, 0, 1, 2, 2, 2, 2, 3, 0, 3, 3, 0, 0, 1, 2, 2, 2, 2, 0, 3, 3, 1, 0, 0, 0, 0, 2, 2, 2, 1, 1, 1, 3, 3, 2, 3, 3, 0, 0, 2, 2, 0, 1, 2, 2, 2, 2, 2, 3, 3, 0, 0, 3, 2, 2, 0, 2, 2, 2, 2, 3, 3, 3, 3, 0, 3, 3, 2, 2, 2, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 1, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 341, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 326, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 326, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        this.addInventory(new LootTable(LootTablePresets.ravenChest), random, 9, 5, new Object[0]);
        ArrayList<Point> spawnPositions = new ArrayList<Point>();
        spawnPositions.add(new Point(6, 4));
        spawnPositions.add(new Point(5, 6));
        spawnPositions.add(new Point(7, 8));
        spawnPositions.add(new Point(4, 12));
        for (Point spawnPosition : spawnPositions) {
            this.addCustomApply(spawnPosition.x, spawnPosition.y, 0, (level, levelX, levelY, dir, blackboard) -> {
                CrazedRavenMob crazedRavenMob = (CrazedRavenMob)MobRegistry.getMob("crazedraven", level);
                crazedRavenMob.canDespawn = false;
                Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, crazedRavenMob);
                crazedRavenMob.onSpawned(spawnLocation.x, spawnLocation.y);
                level.entityManager.addMob(crazedRavenMob, spawnLocation.x, spawnLocation.y);
                return (level1, presetX, presetY) -> crazedRavenMob.remove();
            });
        }
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

