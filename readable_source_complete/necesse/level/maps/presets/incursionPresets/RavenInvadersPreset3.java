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

public class RavenInvadersPreset3
extends Preset {
    public RavenInvadersPreset3(GameRandom random) {
        super("PRESET = {\n\twidth = 15,\n\theight = 12,\n\ttileIDs = [67, ravenfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 67, 67, -1, 67, -1, -1, -1, -1, -1, -1, -1, 67, -1, 67, 67, -1, 67, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, 67, -1, 67, -1, 67, 67, 67, -1, 67, -1, -1, -1, -1, 67, 67, -1, -1, -1, -1, -1, -1, 67, 67, -1, -1, -1, -1, 67, -1, 67, 67, -1, -1, 67, -1, 67, 67, -1, 67, 67, -1, -1, -1, -1, -1, -1, 67, 67, 67, 67, 67, -1, 67, 67, 67, -1, -1, 67, -1, 67, 67, 67, -1, 67, -1, 67, 67, -1, 67, -1, -1, -1, -1, -1, 67, -1, -1, 67, 67, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, 67, -1, 67, -1, 67, 67, -1, -1, -1, -1, -1, -1, -1, -1, 67, -1, 67, -1, -1, -1, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 289, sack, 1191, amethystclustersmall, 744, ravennestwithegg, 745, ravennestwithegg2, 362, ravenstatue, 746, ravennestwithegg3, 747, ravennestwithegg4, 748, ravenskull, 749, ravenskull2, 750, ravenskull3, 751, ravenskull4, 1177, amethystclusterpure, 1178, amethystclusterpurer],\n\tobjects = [0, 0, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, -1, -1, -1, 0, 0, -1, -1, -1, 0, 0, -1, 362, 0, 0, 0, 362, -1, 1191, 0, -1, -1, -1, -1, -1, 0, 1191, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, 362, 0, 0, 0, 744, 745, 0, 0, 0, 0, 362, -1, -1, 0, 0, 0, 0, 0, 0, 746, 747, 0, 744, 745, 0, 0, 0, 0, 0, 0, 0, 0, 0, 748, 749, 1177, 1178, 746, 747, 0, 0, 0, 0, 0, -1, 362, 0, 0, 750, 751, 289, 0, 0, 0, 0, 362, 0, 0, -1, -1, -1, 1191, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, 362, 0, 0, 0, 362, 0, 0, 0, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, -1, -1, -1, 1191, 0, -1, -1, 0, 0, 0, -1, -1, 1191, 0, 0, -1, -1, -1, 0, 0, 0, -1],\n\trotations = [0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 3, 0, 1, 1, 1, 1, 1, 0, 3, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 2, 2, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1, 0, 1, 2, 3, 0, 2, 2, 1, 0, 0, 0, 0, 2, 0, 1, 1, 1, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 3, 0, 1, 1, 0, 0, 0, 1, 1, 3, 0, 0, 1, 1, 1, 0, 0, 0, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        this.addInventory(new LootTable(LootTablePresets.ravenChest), random, 7, 7, new Object[0]);
        ArrayList<Point> spawnPositions = new ArrayList<Point>();
        spawnPositions.add(new Point(4, 4));
        spawnPositions.add(new Point(10, 3));
        spawnPositions.add(new Point(4, 7));
        spawnPositions.add(new Point(10, 8));
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

