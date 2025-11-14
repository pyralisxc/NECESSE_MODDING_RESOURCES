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

public class RavenInvadersPreset1
extends Preset {
    public RavenInvadersPreset1(GameRandom random) {
        super("PRESET = {\n\twidth = 17,\n\theight = 14,\n\ttileIDs = [67, ravenfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, -1, -1, 67, 67, 67, -1, -1, -1, -1, 67, -1, -1, -1, -1, -1, 67, 67, 67, -1, 67, -1, 67, -1, 67, 67, 67, 67, -1, -1, -1, -1, -1, -1, 67, -1, -1, 67, -1, -1, 67, 67, -1, 67, -1, 67, -1, -1, -1, -1, 67, 67, -1, -1, 67, 67, -1, 67, -1, -1, -1, 67, -1, -1, -1, 67, 67, -1, 67, 67, 67, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, -1, 67, -1, 67, -1, -1, -1, 67, 67, -1, 67, 67, 67, -1, 67, 67, -1, -1, -1, 67, -1, -1, -1, -1, 67, 67, -1, -1, 67, 67, 67, 67, 67, -1, -1, -1, -1, -1, 67, -1, -1, 67, 67, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, 67, 67, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 289, sack, 835, brokenplate, 740, emptyravennest, 741, emptyravennest2, 742, emptyravennest3, 743, emptyravennest4, 744, ravennestwithegg, 745, ravennestwithegg2, 746, ravennestwithegg3, 362, ravenstatue, 235, ravenwall, 747, ravennestwithegg4, 397, raveneffigy, 845, leftovertray, 398, raveneffigy2, 846, leftovertray2, 240, ravenwindow],\n\tobjects = [0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, 740, 741, 0, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, 742, 743, 235, 235, 0, -1, -1, -1, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 240, -1, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, 0, 235, 235, 235, 744, 745, 0, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0, 744, 745, 397, 398, 746, 747, 0, 235, 362, 845, 846, -1, -1, -1, -1, -1, 0, 746, 747, 0, 0, 0, 0, -1, 235, 235, 740, 741, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 742, 743, -1, -1, 0, 0, 0, 235, 235, -1, -1, 0, 235, 289, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 362, 235, -1, 0, 0, 235, 240, 235, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 740, 741, 235, -1, -1, -1, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 742, 743, 835, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 2, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 3, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 3, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 3, 3, 3, 3, 3, 0, 3, 3, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        this.addInventory(new LootTable(LootTablePresets.ravenChest), random, 10, 8, new Object[0]);
        ArrayList<Point> spawnPositions = new ArrayList<Point>();
        spawnPositions.add(new Point(4, 4));
        spawnPositions.add(new Point(7, 6));
        spawnPositions.add(new Point(4, 11));
        spawnPositions.add(new Point(14, 8));
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

