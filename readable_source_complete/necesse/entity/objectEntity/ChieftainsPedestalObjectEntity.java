/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.levelEvent.ChieftainGauntletEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ChieftainMob;
import necesse.entity.mobs.hostile.theRunebound.CroneMob;
import necesse.entity.objectEntity.ChieftainBoneSpikeWallObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class ChieftainsPedestalObjectEntity
extends ObjectEntity {
    public ArrayList<Point> wallTiles = new ArrayList();
    public ArrayList<Point> enemyPortalTiles = new ArrayList();
    public ArrayList<Point> teleportPortalTiles = new ArrayList();
    public Point chieftainSpawnPos;
    public int chieftainMobID;
    public Point croneSpawnPos;
    public int croneMobID;
    protected boolean isRising = true;
    protected long animationStartTime;

    public ChieftainsPedestalObjectEntity(Level level, int x, int y) {
        super(level, "chieftainpedestal", x, y);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addPointCollection("wallTiles", this.wallTiles);
        save.addPointCollection("enemyPortalTiles", this.enemyPortalTiles);
        save.addPointCollection("teleportPortalTiles", this.teleportPortalTiles);
        if (this.chieftainSpawnPos != null) {
            save.addPoint("chieftainSpawnPos", this.chieftainSpawnPos);
        }
        save.addInt("chieftainMobID", this.chieftainMobID);
        if (this.croneSpawnPos != null) {
            save.addPoint("croneSpawnPos", this.croneSpawnPos);
        }
        save.addInt("croneMobID", this.croneMobID);
        save.addLong("animationStartTime", this.animationStartTime);
        save.addBoolean("isRising", this.isRising);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wallTiles = save.getPointCollection("wallTiles", new ArrayList<Point>());
        this.enemyPortalTiles = save.getPointCollection("enemyPortalTiles", new ArrayList<Point>());
        this.teleportPortalTiles = save.getPointCollection("teleportPortalTiles", new ArrayList<Point>());
        this.chieftainSpawnPos = save.getPoint("chieftainSpawnPos", this.chieftainSpawnPos);
        this.chieftainMobID = save.getInt("chieftainMobID", this.chieftainMobID);
        this.croneSpawnPos = save.getPoint("croneSpawnPos", this.croneSpawnPos);
        this.croneMobID = save.getInt("croneMobID", this.croneMobID);
        this.animationStartTime = save.getLong("animationStartTime", 0L);
        this.isRising = save.getBoolean("isRising", this.isRising);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.animationStartTime);
        writer.putNextBoolean(this.isRising);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.animationStartTime = reader.getNextLong();
        this.isRising = reader.getNextBoolean();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.isRising && this.animationStartTime + (long)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME <= this.getTime()) {
            this.getLevel().setObject(this.tileX, this.tileY, 0);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.isRising && this.animationStartTime + (long)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME <= this.getTime()) {
            this.getLevel().setObject(this.tileX, this.tileY, 0);
        }
    }

    public void startAnimation(boolean isRising) {
        this.isRising = isRising;
        this.animationStartTime = this.getTime();
    }

    public float getAnimationHeightProgress() {
        long progress = this.getTime() - this.animationStartTime;
        if (this.isRising) {
            if (progress < (long)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME) {
                return (float)progress / (float)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME;
            }
            return 1.0f;
        }
        if (progress < (long)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME) {
            return 1.0f - (float)progress / (float)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME;
        }
        return 0.0f;
    }

    public boolean canStartChieftainEvent() {
        if (this.chieftainSpawnPos == null) {
            return false;
        }
        return this.getLevel().entityManager.events.regionList.getInRegionTileByTile(this.tileX, this.tileY).stream().filter(event -> event instanceof ChieftainGauntletEvent).map(event -> (ChieftainGauntletEvent)event).noneMatch(event -> event.tileX == this.tileX && event.tileY == this.tileY);
    }

    public void startChieftainEvent() {
        Mob foundMob;
        Mob foundMob2;
        ChieftainMob chieftainMob = null;
        if (this.chieftainMobID != -1 && (foundMob2 = this.getLevel().entityManager.mobs.get(this.chieftainMobID, false)) instanceof ChieftainMob) {
            chieftainMob = (ChieftainMob)foundMob2;
        }
        if (chieftainMob == null) {
            chieftainMob = new ChieftainMob();
            chieftainMob.spawnPos = new Point(this.chieftainSpawnPos);
            this.getLevel().entityManager.addMob(chieftainMob, this.chieftainSpawnPos.x, this.chieftainSpawnPos.y);
            this.chieftainMobID = chieftainMob.getUniqueID();
        }
        CroneMob croneMob = null;
        if (this.croneMobID != -1 && (foundMob = this.getLevel().entityManager.mobs.get(this.croneMobID, false)) instanceof CroneMob) {
            croneMob = (CroneMob)foundMob;
        }
        if (croneMob == null && this.croneSpawnPos != null) {
            croneMob = new CroneMob();
            this.getLevel().entityManager.addMob(croneMob, this.croneSpawnPos.x, this.croneSpawnPos.y);
            this.croneMobID = croneMob.getUniqueID();
        }
        this.getLevel().entityManager.events.add(new ChieftainGauntletEvent(this.tileX, this.tileY, this.wallTiles, this.enemyPortalTiles, this.teleportPortalTiles, this.chieftainSpawnPos, chieftainMob, this.croneSpawnPos, croneMob));
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, tileOffset, positionOffset);
        ArrayList<Point> oldWallTiles = this.wallTiles;
        this.wallTiles = new ArrayList();
        for (Point point : oldWallTiles) {
            this.wallTiles.add(new Point(point.x + tileOffset.x, point.y + tileOffset.y));
        }
        ArrayList<Point> oldEnemyPortalTiles = this.enemyPortalTiles;
        this.enemyPortalTiles = new ArrayList();
        for (Point tile : oldEnemyPortalTiles) {
            this.enemyPortalTiles.add(new Point(tile.x + tileOffset.x, tile.y + tileOffset.y));
        }
        ArrayList<Point> arrayList = this.teleportPortalTiles;
        this.teleportPortalTiles = new ArrayList();
        for (Point tile : arrayList) {
            this.teleportPortalTiles.add(new Point(tile.x + tileOffset.x, tile.y + tileOffset.y));
        }
        if (this.chieftainSpawnPos != null) {
            this.chieftainSpawnPos.x += positionOffset.x;
            this.chieftainSpawnPos.y += positionOffset.y;
        }
    }
}

