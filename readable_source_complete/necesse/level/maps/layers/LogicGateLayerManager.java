/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.layers;

import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LogicGateLayerManager {
    protected final Level level;

    public LogicGateLayerManager(Level level) {
        this.level = level;
    }

    public boolean hasGate(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return false;
        }
        return region.logicLayer.hasLogicGateByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public int getLogicGateID(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return 0;
        }
        return region.logicLayer.getLogicGateIDByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public GameLogicGate getLogicGate(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return null;
        }
        return region.logicLayer.getLogicGateByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public LogicGateEntity getEntity(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return null;
        }
        return region.logicLayer.getEntityByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void clearLogicGate(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return;
        }
        region.logicLayer.clearLogicGateByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void setLogicGate(int tileX, int tileY, int gateID, PacketReader spawnPacket) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, true);
        if (region == null) {
            return;
        }
        region.logicLayer.setLogicGateByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, gateID, spawnPacket);
    }

    public boolean isWireActive(int tileX, int tileY, int wire) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return false;
        }
        return region.logicLayer.isWireActiveByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset, wire);
    }

    public PacketLogicGateUpdate getUpdatePacket(int tileX, int tileY) {
        Region region = this.level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return null;
        }
        return region.logicLayer.getUpdatePacketByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }
}

