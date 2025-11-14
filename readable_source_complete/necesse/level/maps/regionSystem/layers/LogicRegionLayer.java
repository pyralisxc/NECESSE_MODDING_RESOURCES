/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.util.HashMap;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ClientTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ServerTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.WireUpdateRegionLayer;

public class LogicRegionLayer
extends RegionLayer
implements ClientTickRegionLayer,
ServerTickRegionLayer,
WireUpdateRegionLayer,
RegionPacketHandlerRegionLayer,
SaveDataRegionLayer {
    private final HashMap<Integer, LogicGateEntity> entities;
    protected boolean[] hasGate;

    public LogicRegionLayer(Region region) {
        super(region);
        this.hasGate = new boolean[region.tileWidth * region.tileHeight];
        this.entities = new HashMap();
    }

    @Override
    public void init() {
    }

    @Override
    public void onLayerUnloaded() {
    }

    protected int getDataIndex(int regionTileX, int regionTileY) {
        return regionTileX + regionTileY * this.region.tileWidth;
    }

    public boolean hasLogicGateByRegion(int regionTileX, int regionTileY) {
        return this.hasGate[this.getDataIndex(regionTileX, regionTileY)];
    }

    @Override
    public void clientTick() {
        for (LogicGateEntity value : this.entities.values()) {
            value.tick();
        }
    }

    @Override
    public void serverTick() {
        for (LogicGateEntity value : this.entities.values()) {
            value.tick();
        }
    }

    @Override
    public void onLayerLoaded() {
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                if (!this.hasLogicGateByRegion(regionTileX, regionTileY)) continue;
                LogicGateEntity entity = this.getEntityByRegion(regionTileX, regionTileY);
                if (entity == null) {
                    entity = this.getLogicGateByRegion(regionTileX, regionTileY).getNewEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                    this.entities.put(this.getDataIndex(regionTileX, regionTileY), entity);
                }
                entity.init();
                for (int wireID = 0; wireID < 4; ++wireID) {
                    entity.onWireUpdate(wireID, this.level.wireManager.isWireActive(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, wireID));
                }
            }
        }
    }

    @Override
    public void onLoadingComplete() {
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                if (!this.hasLogicGateByRegion(regionTileX, regionTileY)) continue;
                LogicGateEntity entity = this.getEntityByRegion(regionTileX, regionTileY);
                if (entity == null) {
                    entity = this.getLogicGateByRegion(regionTileX, regionTileY).getNewEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                    this.entities.put(this.getDataIndex(regionTileX, regionTileY), entity);
                }
                entity.init();
                for (int wireID = 0; wireID < 4; ++wireID) {
                    entity.onWireUpdate(wireID, this.level.wireManager.isWireActive(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, wireID));
                }
            }
        }
    }

    public int getLogicGateIDByRegion(int regionTileX, int regionTileY) {
        if (this.hasLogicGateByRegion(regionTileX, regionTileY)) {
            return this.getEntityByRegion((int)regionTileX, (int)regionTileY).logicGateID;
        }
        return -1;
    }

    public GameLogicGate getLogicGateByRegion(int regionTileX, int regionTileY) {
        return LogicGateRegistry.getLogicGate(this.getLogicGateIDByRegion(regionTileX, regionTileY));
    }

    public LogicGateEntity getEntityByRegion(int regionTileX, int regionTileY) {
        return this.entities.get(this.getDataIndex(regionTileX, regionTileY));
    }

    public void clearLogicGateByRegion(int regionTileX, int regionTileY) {
        int dataIndex = this.getDataIndex(regionTileX, regionTileY);
        this.hasGate[dataIndex] = false;
        this.entities.computeIfPresent(dataIndex, (point, logicGateEntity) -> {
            logicGateEntity.remove();
            return null;
        });
        for (int i = 0; i < 4; ++i) {
            this.level.wireManager.updateWire(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, i, false);
        }
    }

    public void setLogicGateByRegion(int regionTileX, int regionTileY, int gateID, PacketReader spawnPacket) {
        if (gateID == -1) {
            this.clearLogicGateByRegion(regionTileX, regionTileY);
            return;
        }
        GameLogicGate logicGate = LogicGateRegistry.getLogicGate(gateID);
        int dataIndex = this.getDataIndex(regionTileX, regionTileY);
        if (!this.level.isLoadingComplete() || !this.region.isLoadingComplete()) {
            this.hasGate[dataIndex] = true;
            this.entities.put(dataIndex, logicGate.getNewEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset));
        } else {
            if (!this.hasLogicGateByRegion(regionTileX, regionTileY)) {
                boolean[] wireActive = new boolean[4];
                for (int i = 0; i < 4; ++i) {
                    wireActive[i] = this.isWireActiveByRegion(regionTileX, regionTileY, i);
                }
                this.hasGate[dataIndex] = true;
                LogicGateEntity newEntity = logicGate.getNewEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                this.entities.compute(dataIndex, (point, last) -> {
                    if (last != null) {
                        last.remove();
                    }
                    return newEntity;
                });
                newEntity.init();
                if (spawnPacket != null) {
                    newEntity.applyPacket(spawnPacket);
                    newEntity.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
                }
                for (int i = 0; i < 4; ++i) {
                    if (this.isWireActiveByRegion(regionTileX, regionTileY, i) == wireActive[i]) continue;
                    this.level.wireManager.updateWire(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, i, !wireActive[i]);
                }
            } else if (spawnPacket != null) {
                this.entities.compute(dataIndex, (point, entity) -> {
                    if (entity != null) {
                        entity.applyPacket(spawnPacket);
                        entity.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
                    }
                    return entity;
                });
            }
            this.level.addDirtyRegion(this.region);
        }
    }

    public boolean isWireActiveByRegion(int regionTileX, int regionTileY, int wire) {
        if (this.hasLogicGateByRegion(regionTileX, regionTileY)) {
            LogicGateEntity entity = this.entities.get(this.getDataIndex(regionTileX, regionTileY));
            return entity.getOutput(wire);
        }
        return false;
    }

    @Override
    public void onWireUpdateByRegion(int regionTileX, int regionTileY, int wireID, boolean active) {
        if (this.hasLogicGateByRegion(regionTileX, regionTileY)) {
            LogicGateEntity entity = this.entities.get(this.getDataIndex(regionTileX, regionTileY));
            entity.onWireUpdate(wireID, active);
        }
    }

    public PacketLogicGateUpdate getUpdatePacketByRegion(int regionTileX, int regionTileY) {
        if (this.hasLogicGateByRegion(regionTileX, regionTileY)) {
            LogicGateEntity entity = this.getEntityByRegion(regionTileX, regionTileY);
            return new PacketLogicGateUpdate(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, entity.logicGateID, entity);
        }
        return new PacketLogicGateUpdate(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, -1, null);
    }

    @Override
    public void addSaveData(SaveData save) {
        SaveData entitiesData = new SaveData("LOGICGATES");
        for (LogicGateEntity entity : this.entities.values()) {
            entitiesData.addSaveData(entity.getSaveData("LOGICGATE"));
        }
        if (!entitiesData.isEmpty()) {
            save.addSaveData(entitiesData);
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        LoadData entitiesData = save.getFirstLoadDataByName("LOGICGATES");
        if (entitiesData != null) {
            for (LoadData entityData : entitiesData.getLoadDataByName("LOGICGATE")) {
                try {
                    LogicGateEntity entity = LogicGateEntity.loadEntity(this.level, entityData, true);
                    int regionTileX = entity.tileX - this.region.tileXOffset;
                    int regionTileY = entity.tileY - this.region.tileYOffset;
                    if (regionTileX < 0 || regionTileX >= this.region.tileWidth || regionTileY < 0 || regionTileY >= this.region.tileHeight) {
                        throw new LogicGateEntity.LogicGateLoadException("Logic gate entity out of bounds: " + entityData.getName());
                    }
                    int dataIndex = this.getDataIndex(regionTileX, regionTileY);
                    this.entities.put(dataIndex, entity);
                    this.hasGate[dataIndex] = true;
                }
                catch (LogicGateEntity.LogicGateLoadException e) {
                    GameLog.warn.println(e.getMessage());
                    if (e.getCause() == null) continue;
                    e.getCause().printStackTrace();
                }
            }
        }
    }

    @Override
    public void writeLayerPacket(PacketWriter writer) {
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                if (this.hasLogicGateByRegion(regionTileX, regionTileY)) {
                    writer.putNextBoolean(true);
                    LogicGateEntity entity = this.entities.get(this.getDataIndex(regionTileX, regionTileY));
                    writer.putNextShortUnsigned(entity.logicGateID);
                    entity.writePacket(writer);
                    continue;
                }
                writer.putNextBoolean(false);
            }
        }
    }

    @Override
    public boolean applyLayerPacket(PacketReader reader) {
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                int dataIndex = this.getDataIndex(regionTileX, regionTileY);
                if (reader.getNextBoolean()) {
                    int gateID = reader.getNextShortUnsigned();
                    this.hasGate[dataIndex] = true;
                    GameLogicGate logicGate = LogicGateRegistry.getLogicGate(gateID);
                    if (logicGate == null) {
                        return false;
                    }
                    LogicGateEntity newEntity = logicGate.getNewEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                    this.entities.compute(dataIndex, (point, last) -> {
                        if (last != null) {
                            last.remove();
                        }
                        return newEntity;
                    });
                    newEntity.applyPacket(reader);
                    newEntity.applyPacketEvents.triggerEvent(new LogicGateEntity.ApplyPacketEvent());
                    newEntity.init();
                    continue;
                }
                this.hasGate[dataIndex] = false;
                this.entities.computeIfPresent(dataIndex, (point, logicGateEntity) -> {
                    logicGateEntity.remove();
                    return null;
                });
            }
        }
        return true;
    }
}

