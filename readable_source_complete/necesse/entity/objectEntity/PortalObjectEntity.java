/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PortalObjectEntity
extends ObjectEntity {
    public LevelIdentifier destinationIdentifier;
    public int destinationTileX;
    public int destinationTileY;
    public int clearHostileMobsRadius = 160;
    public int clearLevelGeneratedHostileMobsRadius = 480;

    public PortalObjectEntity(Level level, String type, int x, int y, LevelIdentifier destinationIdentifier, int destinationTileX, int destinationTileY) {
        super(level, "portal." + type, x, y);
        this.destinationIdentifier = destinationIdentifier;
        this.destinationTileX = destinationTileX;
        this.destinationTileY = destinationTileY;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addUnsafeString("destination", this.destinationIdentifier.stringID);
        save.addInt("destinationTileX", this.destinationTileX);
        save.addInt("destinationTileY", this.destinationTileY);
        if (this.destinationIdentifier.isIslandPosition()) {
            save.addInt("dIslandX", this.destinationIdentifier.getIslandX());
            save.addInt("dIslandY", this.destinationIdentifier.getIslandY());
            save.addInt("dDimension", this.destinationIdentifier.getIslandDimension());
            save.addInt("dX", this.destinationTileX);
            save.addInt("dY", this.destinationTileY);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        try {
            this.destinationIdentifier = new LevelIdentifier(save.getUnsafeString("destination", null, false));
        }
        catch (InvalidLevelIdentifierException e) {
            int dIslandX = save.getInt("dIslandX");
            int dIslandY = save.getInt("dIslandY");
            int dDimension = save.getInt("dDimension");
            this.destinationIdentifier = new LevelIdentifier(dIslandX, dIslandY, dDimension);
        }
        try {
            this.destinationTileX = save.getInt("destinationTileX");
        }
        catch (Exception e) {
            this.destinationTileX = save.getInt("dX");
        }
        try {
            this.destinationTileY = save.getInt("destinationTileY");
        }
        catch (Exception e) {
            this.destinationTileY = save.getInt("dY");
        }
    }

    @Override
    public boolean shouldRequestPacket() {
        return false;
    }

    public void use(Server server, ServerClient client) {
        client.changeLevel(this.getDestinationIdentifier(), level -> new Point(this.getDestinationX(), this.getDestinationY()), true);
    }

    public void runClearMobs(Level level, int tileX, int tileY) {
        if (this.clearHostileMobsRadius > 0 || this.clearLevelGeneratedHostileMobsRadius > 0) {
            int tileRadius = Math.max(this.clearHostileMobsRadius, this.clearLevelGeneratedHostileMobsRadius) / 32 + 1;
            level.regionManager.ensureTilesAreLoaded(tileX - tileRadius, tileY - tileRadius, tileX + tileRadius, tileY + tileRadius);
            for (Mob mob : level.entityManager.mobs.getInRegionByTileRange(tileX, tileY, tileRadius)) {
                float distance;
                if (mob.isBoss() || !mob.isHostile || !((distance = mob.getDistance(tileX * 32 + 16, tileY * 32 + 16)) < (float)this.clearHostileMobsRadius) && (mob.canDespawn || !(distance < (float)this.clearLevelGeneratedHostileMobsRadius)) || !mob.estimateCanMoveTo(tileX, tileY, true)) continue;
                mob.remove();
            }
        }
    }

    public LevelIdentifier getDestinationIdentifier() {
        return this.destinationIdentifier;
    }

    public int getDestinationX() {
        return this.destinationTileX * 32 + 16;
    }

    public int getDestinationY() {
        return this.destinationTileY * 32 + 16;
    }

    protected void teleportClientToAroundDestination(ServerClient client, Predicate<Level> validCheck, boolean mountFollow) {
        this.teleportClientToAroundDestination(client, null, validCheck, mountFollow);
    }

    protected void teleportClientToAroundDestination(ServerClient client, Function<LevelIdentifier, Level> generator, Predicate<Level> validCheck, boolean mountFollow) {
        client.changeLevelCheck(this.getDestinationIdentifier(), generator, level -> {
            boolean isValid;
            boolean bl = isValid = validCheck == null || validCheck.test((Level)level);
            if (!isValid) {
                return new TeleportResult(false, null);
            }
            Point teleportPos = PortalObjectEntity.getTeleportDestinationAroundObject(level, client.playerMob, this.destinationTileX, this.destinationTileY, true);
            if (teleportPos == null) {
                teleportPos = new Point(this.destinationTileX * 32 + 16, this.destinationTileY * 32 + 16);
            }
            return new TeleportResult(true, teleportPos);
        }, mountFollow);
    }

    public static Point getTeleportDestinationAroundObject(Level level, Mob mob, int tileX, int tileY, boolean allowDiagonal) {
        return GameUtils.getValidMobLocationAroundObject(GameRandom.globalRandom, level, mob, tileX, tileY, allowDiagonal);
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, tileOffset, positionOffset);
        LevelIdentifier oldDestination = this.destinationIdentifier;
        this.destinationIdentifier = migrationData.getNewLevelIdentifier(oldDestination);
        if (this.destinationIdentifier == null) {
            this.destinationIdentifier = oldLevelIdentifier;
        } else {
            Point offset = migrationData.getTilePositionOffset(oldDestination);
            this.destinationTileX += offset.x;
            this.destinationTileY += offset.y;
        }
    }
}

