/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.regionSystem.layers.JobsRegionLayer;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ClientTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.FrameTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.LevelJobsSubmitterRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.ServerTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickEffectRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.WireUpdateRegionLayer;

public class RegionLayersList
implements Iterable<RegionLayer> {
    private final RegionLayer[] layers;
    private final ArrayList<FrameTickRegionLayer> frameTickLayers;
    private final ArrayList<ClientTickRegionLayer> clientTickLayers;
    private final ArrayList<ServerTickRegionLayer> serverTickLayers;
    private final ArrayList<TileTickRegionLayer> tileTickLayers;
    private final ArrayList<TileTickEffectRegionLayer> tileTickEffectLayers;
    private final ArrayList<WireUpdateRegionLayer> wireUpdateLayers;
    private final ArrayList<LevelJobsSubmitterRegionLayer> levelJobsSubmitterLayers;
    private final ArrayList<RegionPacketHandlerRegionLayer> regionPacketHandlerLayers;
    private final ArrayList<SaveDataRegionLayer> saveDataLayers;

    public RegionLayersList(RegionLayer[] allLayers, ArrayList<FrameTickRegionLayer> frameTickLayers, ArrayList<ClientTickRegionLayer> clientTickLayers, ArrayList<ServerTickRegionLayer> serverTickLayers, ArrayList<TileTickRegionLayer> tileTickLayers, ArrayList<TileTickEffectRegionLayer> tileTickEffectLayers, ArrayList<WireUpdateRegionLayer> wireUpdateLayers, ArrayList<LevelJobsSubmitterRegionLayer> levelJobsSubmitterLayers, ArrayList<RegionPacketHandlerRegionLayer> regionPacketHandlerLayers, ArrayList<SaveDataRegionLayer> saveDataLayers) {
        this.layers = allLayers;
        this.frameTickLayers = frameTickLayers;
        this.clientTickLayers = clientTickLayers;
        this.serverTickLayers = serverTickLayers;
        this.tileTickLayers = tileTickLayers;
        this.tileTickEffectLayers = tileTickEffectLayers;
        this.wireUpdateLayers = wireUpdateLayers;
        this.levelJobsSubmitterLayers = levelJobsSubmitterLayers;
        this.regionPacketHandlerLayers = regionPacketHandlerLayers;
        this.saveDataLayers = saveDataLayers;
    }

    public <T extends RegionLayer> T getLayer(int layerID, Class<T> expectedClass) {
        return (T)((RegionLayer)expectedClass.cast(this.layers[layerID]));
    }

    public void frameTick(TickManager tickManager) {
        for (FrameTickRegionLayer layer : this.frameTickLayers) {
            layer.frameTick(tickManager);
        }
    }

    public void clientTick() {
        for (ClientTickRegionLayer layer : this.clientTickLayers) {
            layer.clientTick();
        }
    }

    public void serverTick() {
        for (ServerTickRegionLayer layer : this.serverTickLayers) {
            layer.serverTick();
        }
    }

    public void tickTileByRegion(int regionTileX, int regionTileY) {
        for (TileTickRegionLayer layer : this.tileTickLayers) {
            layer.tickTileByRegion(regionTileX, regionTileY);
        }
    }

    public void tickTileEffectByRegion(GameCamera camera, PlayerMob perspective, int regionTileX, int regionTileY) {
        for (TileTickEffectRegionLayer layer : this.tileTickEffectLayers) {
            layer.tickTileEffectByRegion(camera, perspective, regionTileX, regionTileY);
        }
    }

    public void onWireUpdateByRegion(int regionTileX, int regionTileY, int wireID, boolean active) {
        for (WireUpdateRegionLayer layer : this.wireUpdateLayers) {
            layer.onWireUpdateByRegion(regionTileX, regionTileY, wireID, active);
        }
    }

    public void addLevelJobs(JobsRegionLayer jobsLayer, int regionTileX, int regionTileY) {
        for (LevelJobsSubmitterRegionLayer layer : this.levelJobsSubmitterLayers) {
            List<LevelJob> newJobs = layer.getLevelJobsByRegion(regionTileX, regionTileY);
            if (newJobs == null) continue;
            for (LevelJob job : newJobs) {
                jobsLayer.addJob(job, false, false);
            }
        }
    }

    public void writeRegionLayerPacket(PacketWriter writer) {
        for (RegionPacketHandlerRegionLayer layer : this.regionPacketHandlerLayers) {
            layer.writeLayerPacket(writer);
        }
    }

    public boolean applyRegionLayerPacket(PacketReader reader) {
        for (RegionPacketHandlerRegionLayer layer : this.regionPacketHandlerLayers) {
            if (layer.applyLayerPacket(reader)) continue;
            System.err.println("Received invalid region data in " + ((RegionLayer)((Object)layer)).getStringID() + " layer");
            return false;
        }
        return true;
    }

    public void addSaveData(SaveData save) {
        for (SaveDataRegionLayer layer : this.saveDataLayers) {
            layer.addSaveData(save);
        }
    }

    public void loadSaveData(LoadData save, PerformanceTimerManager debugLoadingPerformance) {
        for (SaveDataRegionLayer layer : this.saveDataLayers) {
            Performance.recordConstant(debugLoadingPerformance, ((RegionLayer)((Object)layer)).getStringID(), () -> layer.loadSaveData(save));
        }
    }

    @Override
    public Iterator<RegionLayer> iterator() {
        return GameUtils.arrayIterator(this.layers);
    }
}

