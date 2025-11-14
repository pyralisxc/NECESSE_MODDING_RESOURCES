/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.SimulatePriorityList;
import necesse.level.maps.regionSystem.layers.UnsignedShortRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.LevelJobsSubmitterRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickEffectRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickRegionLayer;

public class TileRegionLayer
extends UnsignedShortRegionLayer
implements TileTickRegionLayer,
TileTickEffectRegionLayer,
LevelJobsSubmitterRegionLayer {
    protected boolean[] isPlayerPlaced;

    public TileRegionLayer(Region region) {
        super(region);
        this.isPlayerPlaced = new boolean[region.tileWidth * region.tileHeight];
    }

    @Override
    public void init() {
    }

    @Override
    public void writeLayerPacket(PacketWriter writer) {
        super.writeLayerPacket(writer);
        for (boolean b : this.isPlayerPlaced) {
            writer.putNextBoolean(b);
        }
    }

    @Override
    public boolean applyLayerPacket(PacketReader reader) {
        if (!super.applyLayerPacket(reader)) {
            return false;
        }
        for (int i = 0; i < this.isPlayerPlaced.length; ++i) {
            this.isPlayerPlaced[i] = reader.getNextBoolean();
        }
        return true;
    }

    @Override
    public void simulateWorld(long worldTimeIncrease, boolean sendChanges) {
        super.simulateWorld(worldTimeIncrease, sendChanges);
        Performance.recordConstant(LevelSave.debugLoadingPerformance, "tiles", () -> {
            long seconds = worldTimeIncrease / 1000L;
            if (seconds > 0L) {
                SimulatePriorityList simulateList = new SimulatePriorityList();
                for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                    for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
                        this.getTileByRegion(regionTileX, regionTileY).addSimulateLogic(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, seconds, simulateList, sendChanges);
                    }
                }
                simulateList.run();
            }
        });
    }

    @Override
    public void tickTileByRegion(int regionTileX, int regionTileY) {
        this.getTileByRegion(regionTileX, regionTileY).tick(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
    }

    @Override
    public void tickTileEffectByRegion(GameCamera camera, PlayerMob perspective, int regionTileX, int regionTileY) {
        this.getTileByRegion(regionTileX, regionTileY).tickEffect(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
    }

    @Override
    public List<LevelJob> getLevelJobsByRegion(int regionTileX, int regionTileY) {
        return this.getTileByRegion(regionTileX, regionTileY).getLevelJobs(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
    }

    public void fillWithTilesIfEmpty(int tileID) {
        for (int i = 0; i < this.data.length; ++i) {
            if ((this.data[i] & 0xFFFF) != TileRegistry.emptyID) continue;
            this.data[i] = (short)tileID;
        }
    }

    public void fillWithEmptyTiles() {
        Arrays.fill(this.data, (short)TileRegistry.emptyID);
    }

    public void setTileByRegion(int regionTileX, int regionTileY, int tile) {
        this.setTileByRegion(regionTileX, regionTileY, tile, false);
    }

    public void setTileByRegion(int regionTileX, int regionTileY, int tile, boolean forceDontUpdate) {
        GameTile oldTile = this.getTileByRegion(regionTileX, regionTileY);
        this.set(regionTileX, regionTileY, (short)tile);
        GameTile newTile = this.getTileByRegion(regionTileX, regionTileY);
        int tileX = regionTileX + this.region.tileXOffset;
        int tileY = regionTileY + this.region.tileYOffset;
        if (this.region.isLoadingComplete() && this.level.isLoadingComplete() && !forceDontUpdate) {
            this.level.liquidManager.onTileUpdated(this.region, tileX, tileY, oldTile, newTile);
            if (oldTile.getLightLevel() != newTile.getLightLevel()) {
                this.level.lightManager.updateStaticLight(tileX, tileY);
            }
            if (oldTile != newTile) {
                ServerSettlementData settlement;
                this.manager.onSplattingChange(tileX, tileY);
                this.level.addDirtyRegion(this.region);
                if (this.level.isServer() && (settlement = SettlementsWorldData.getSettlementsData(this.level).getServerDataAtTile(this.level.getIdentifier(), tileX, tileY)) != null) {
                    settlement.rooms.recalculateStats(Collections.singletonList(new Point(regionTileX, regionTileY)));
                }
            }
        }
        if (this.level.isClient()) {
            this.level.getClient().levelManager.updateMapTile(tileX, tileY);
        }
    }

    public int getTileIDByRegion(int regionTileX, int regionTileY) {
        return this.get(regionTileX, regionTileY);
    }

    public GameTile getTileByRegion(int regionTileX, int regionTileY) {
        return TileRegistry.getTile(this.getTileIDByRegion(regionTileX, regionTileY));
    }

    public boolean isTileLiquidByRegion(int regionTileX, int regionTileY) {
        return this.getTileByRegion((int)regionTileX, (int)regionTileY).isLiquid;
    }

    public void setIsPlayerPlacedByRegion(int regionTileX, int regionTileY, boolean isPlayerPlaced) {
        this.isPlayerPlaced[this.getDataIndex((int)regionTileX, (int)regionTileY)] = isPlayerPlaced;
    }

    public boolean isPlayerPlacedByRegion(int regionTileX, int regionTileY) {
        return this.isPlayerPlaced[this.getDataIndex(regionTileX, regionTileY)];
    }

    @Override
    public void addSaveData(SaveData save) {
        HashSet<Integer> usedTileIDs = new HashSet<Integer>();
        for (short id : this.data) {
            usedTileIDs.add(id & 0xFFFF);
        }
        String[] tileIDs = VersionMigration.generateStringIDsArray(usedTileIDs, TileRegistry::getTileStringID);
        save.addStringArray("tileIDs", tileIDs);
        super.addSaveData(save);
        try {
            save.addCompressedBooleanArray("tileIsPlayerPlaced", this.isPlayerPlaced);
        }
        catch (IOException e) {
            save.addSmallBooleanArray("tileIsPlayerPlaced", this.isPlayerPlaced);
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        super.loadSaveData(save);
        int[] conversionArray = null;
        if (save.hasLoadDataByName("tileIDs")) {
            String[] tileIDs = save.getStringArray("tileIDs");
            conversionArray = VersionMigration.generateStringIDsArrayConversionArray(tileIDs, TileRegistry.getTileStringIDs(), TileRegistry.waterID, VersionMigration.oldTileStringIDs);
        }
        if (conversionArray != null) {
            int i;
            int[] intData = new int[this.data.length];
            for (i = 0; i < intData.length; ++i) {
                intData[i] = this.data[i];
            }
            if (VersionMigration.convertArray(intData, conversionArray)) {
                for (i = 0; i < this.data.length; ++i) {
                    this.data[i] = (short)intData[i];
                }
                Server server = this.level.getServer();
                if (server != null) {
                    server.migratedNames.add("tile data");
                    server.regionsMigrated.add(this.level.getIdentifier(), new Point(this.region.regionX, this.region.regionY));
                } else {
                    System.out.println("Migrated level " + this.level.getIdentifier() + " region " + this.region.regionX + "x" + this.region.regionY + " tile data");
                }
            } else {
                System.out.println("Failed to migrate level " + this.level.getIdentifier() + " region " + this.region.regionX + "x" + this.region.regionY + " tile data");
            }
        }
        try {
            if (save.isSmallBooleanArray("tileIsPlayerPlaced")) {
                throw new Exception("Handle small boolean array");
            }
            this.isPlayerPlaced = save.getCompressedBooleanArray("tileIsPlayerPlaced");
        }
        catch (Exception e) {
            this.isPlayerPlaced = save.getSmallBooleanArray("tileIsPlayerPlaced", this.isPlayerPlaced, false);
        }
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] != -1 && this.data[i] != TileRegistry.emptyID) continue;
            this.data[i] = (short)TileRegistry.waterID;
        }
    }

    @Override
    public void onLayerLoaded() {
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onLayerUnloaded() {
    }

    @Override
    protected void handleSaveNotFound() {
        throw new RuntimeException("Could not find level tile data");
    }

    @Override
    protected void handleLoadException(Exception e) {
        throw new RuntimeException("Failed to load level tiles", e);
    }

    @Override
    protected boolean isValidValue(int regionTileX, int regionTileY, int value) {
        return true;
    }
}

