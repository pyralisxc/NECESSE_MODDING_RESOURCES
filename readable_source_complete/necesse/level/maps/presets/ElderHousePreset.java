/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.GameLog;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;

public class ElderHousePreset
extends Preset {
    public ElderHousePreset(GameRandom random) {
        super(17, 14);
        this.applyScript("PRESET = {\n\twidth = 17,\n\theight = 9,\n\ttileIDs = [9, farmland, 10, woodfloor, 14, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 14, 14, 14, 14, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 129, storagebox, 386, firemone, 35, woodwall, 36, wooddoor, 518, ladderdown, 176, sprucedesk, 183, sprucebed, 184, sprucebed2, 377, sunflowerseed, 26, workstation, 187, sprucecandelabra, 188, sprucedisplay],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 35, 35, 35, 35, 35, 35, 35, 35, 0, 0, 0, 0, 0, 0, 0, 0, 35, 187, 129, 26, 386, 188, 176, 183, 35, 0, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0, 0, 0, 0, 184, 35, 35, 35, 35, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 36, 0, 518, 0, 35, 377, 377, 0, 0, 35, 0, 0, 0, 0, 0, 0, 187, 35, 0, 0, 0, 35, 377, 377, 0, 0, 35, 35, 35, 35, 36, 35, 35, 35, 35, 35, 35, 35, 35, 377, 377, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 0, 0, 0, 0, 0, 3, 3, 3, 2, 1, 1, 1, 0, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.replaceTile(TileRegistry.grassID, -1);
        this.addInventory(LootTablePresets.startChest, random, 3, 2, new Object[0]);
        this.addInventory(LootTablePresets.startDisplayStand, random, 6, 2, new Object[0]);
        this.fillObject(14, 3, 2, 5, ObjectRegistry.getObjectID("sunflowerseed"));
        this.addCustomPreApplyRectEach(-1, -1, this.width + 2, this.height + 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomPreApplyRectEach(0, 0, this.width, this.height + 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            GameObject object = level.getObject(levelX, levelY);
            if (!object.isGrass) {
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(7, 9, 0, (level, levelX, levelY, dir, blackboard) -> {
            level.setObject(levelX, levelY, ObjectRegistry.getObjectID("roastingstation"), dir);
            return null;
        });
        this.addCustomPreApplyRectEach(6, 8, 3, 3, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(8, 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob mob = (HumanMob)MobRegistry.getMob("elderhuman", level);
            mob.setSettlerSeed(random.nextInt(), true);
            ElderHousePreset.createAndAddElder(mob, level, levelX, levelY);
            return (level1, presetX, presetY) -> mob.remove();
        });
    }

    public static void createAndAddElder(HumanMob mob, Level level, int tileX, int tileY) {
        mob.setHome(tileX, tileY);
        Point elderLocation = Waystone.findTeleportLocation(level, tileX, tileY, mob);
        level.entityManager.addMob(mob, elderLocation.x, elderLocation.y);
        if (level.isServer()) {
            ServerSettlementData serverData = SettlementsWorldData.getSettlementsData(level).getOrCreateServerData(level, tileX, tileY);
            serverData.networkData.markPreventDisbanding();
            SettlementBed bed = serverData.addOrValidateBed(tileX, tileY);
            LevelSettler settler = new LevelSettler(serverData, mob);
            serverData.moveSettler(settler, bed, null);
            if (bed == null) {
                GameLog.warn.println("Could not find bed for elder house at " + tileX + ", " + tileY);
            }
        }
    }
}

