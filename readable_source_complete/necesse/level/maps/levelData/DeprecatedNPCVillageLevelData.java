/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import necesse.engine.GameLog;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.OneWorldNPCVillageData;

@Deprecated
public class DeprecatedNPCVillageLevelData
extends LevelData {
    protected long nextWorldTimeVillagerSpawn;
    public HashSet<Point> villageTiles = new HashSet();
    protected HashMap<Point, DeprecatedNPCVillager> villagers = new HashMap();

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addPointCollection("villageTiles", this.villageTiles);
        save.addLong("nextWorldTimeVillagerSpawn", this.nextWorldTimeVillagerSpawn);
        SaveData villagersSave = new SaveData("VILLAGERS");
        for (DeprecatedNPCVillager villager : this.villagers.values()) {
            villagersSave.addSaveData(villager.getSaveData(""));
        }
        if (!villagersSave.isEmpty()) {
            save.addSaveData(villagersSave);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.villageTiles = new HashSet<Point>(save.getPointCollection("villageTiles", new ArrayList<Point>()));
        this.nextWorldTimeVillagerSpawn = save.getLong("nextWorldTimeVillagerSpawn", this.nextWorldTimeVillagerSpawn, false);
        LoadData villagersSave = save.getFirstLoadDataByName("VILLAGERS");
        if (villagersSave != null) {
            for (LoadData villagerSave : villagersSave.getLoadData()) {
                try {
                    DeprecatedNPCVillager villager = new DeprecatedNPCVillager(villagerSave);
                    this.villagers.put(villager.tile, villager);
                }
                catch (LoadDataException e) {
                    GameLog.warn.println("Failed to load villager data: " + e.getMessage() + " at " + this.level);
                }
            }
        }
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Level newLevel, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, newLevel, tileOffset, positionOffset);
        OneWorldNPCVillageData newVillageData = OneWorldNPCVillageData.getVillageData(newLevel, true);
        if (newVillageData == null) {
            return;
        }
        for (Point villageTile : this.villageTiles) {
            newVillageData.addVillageTile(villageTile.x + tileOffset.x, villageTile.y + tileOffset.y);
        }
        for (DeprecatedNPCVillager value : this.villagers.values()) {
            Mob mob;
            int newTileX = value.tile.x + tileOffset.x;
            int newTileY = value.tile.y + tileOffset.y;
            boolean foundMob = false;
            if (value.currentMobUniqueID != -1 && (mob = this.getLevel().entityManager.mobs.get(value.currentMobUniqueID, false)) instanceof HumanMob) {
                ((HumanMob)mob).villagerData = new OneWorldNPCVillageData.NPCVillagerData(newTileX, newTileY, value.mobStringIDs);
                foundMob = true;
            }
            if (foundMob) continue;
            newVillageData.addNPCVillager(newTileX, newTileY, value.mobStringIDs.toArray(new String[0]));
        }
    }

    protected static class DeprecatedNPCVillager {
        public final Point tile;
        public ArrayList<String> mobStringIDs;
        public int currentMobUniqueID;

        public DeprecatedNPCVillager(Point tile, int currentMobUniqueID, ArrayList<String> mobStringIDs) {
            this.tile = tile;
            this.currentMobUniqueID = currentMobUniqueID;
            this.mobStringIDs = mobStringIDs;
        }

        public DeprecatedNPCVillager(LoadData save) {
            this.tile = save.getPoint("tile", null);
            if (this.tile == null) {
                throw new LoadDataException("NPCVillager tile is missing");
            }
            this.currentMobUniqueID = save.getInt("currentMobUniqueID", -1);
            this.mobStringIDs = new ArrayList<String>(save.getSafeStringCollection("mobStringIDs", new ArrayList<String>()));
        }

        public SaveData getSaveData(String name) {
            SaveData save = new SaveData(name);
            save.addPoint("tile", this.tile);
            save.addInt("currentMobUniqueID", this.currentMobUniqueID);
            save.addSafeStringCollection("mobStringIDs", this.mobStringIDs);
            ListIterator<String> li = this.mobStringIDs.listIterator();
            while (li.hasNext()) {
                String newStringID;
                String oldStringID = li.next();
                if (oldStringID.equals(newStringID = VersionMigration.tryFixStringID(oldStringID, VersionMigration.oldMobStringIDs))) continue;
                li.set(newStringID);
            }
            return save;
        }

        public boolean tickRespawn(Level level, boolean forceSpawn) {
            Mob currentMob;
            Mob mob = currentMob = this.currentMobUniqueID == -1 ? null : level.entityManager.mobs.get(this.currentMobUniqueID, false);
            if (!(currentMob instanceof HumanMob)) {
                int levelPosY;
                int levelPosX;
                boolean playersFound;
                this.currentMobUniqueID = -1;
                if (!forceSpawn && (playersFound = level.entityManager.players.streamArea(levelPosX = this.tile.x * 32 + 16, levelPosY = this.tile.y * 32 + 16, OneWorldNPCVillageData.MIN_PLAYER_PREVENTED_DISTANCE).anyMatch(p -> p.getDistance(levelPosX, levelPosY) < (float)OneWorldNPCVillageData.MIN_PLAYER_PREVENTED_DISTANCE))) {
                    return false;
                }
                while (!this.mobStringIDs.isEmpty()) {
                    int nextIndex = GameRandom.globalRandom.nextInt(this.mobStringIDs.size());
                    String mobStringID = this.mobStringIDs.get(nextIndex);
                    Mob newMob = MobRegistry.getMob(mobStringID, level);
                    if (newMob instanceof HumanMob) {
                        HumanMob humanMob = (HumanMob)newMob;
                        humanMob.setSettlerSeed(GameRandom.globalRandom.nextInt(), true);
                        humanMob.setHome(this.tile.x, this.tile.y);
                        level.entityManager.addMob(humanMob, this.tile.x * 32 + 16, this.tile.y * 32 + 16);
                        this.currentMobUniqueID = newMob.getUniqueID();
                        return true;
                    }
                    this.mobStringIDs.remove(nextIndex);
                }
            }
            return false;
        }

        public boolean shouldRemove() {
            return this.currentMobUniqueID == -1 && this.mobStringIDs.isEmpty();
        }
    }
}

