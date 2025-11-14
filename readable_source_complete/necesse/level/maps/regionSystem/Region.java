/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.RegionLayerRegistry;
import necesse.engine.registries.RegionLayersList;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.LevelEventSave;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.save.levelData.ObjectEntitySave;
import necesse.engine.save.levelData.PickupEntitySave;
import necesse.engine.util.GameMath;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.Entity;
import necesse.entity.LoadingCompleteInterface;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.EntityList;
import necesse.entity.manager.RegionLoadedListenerEntityComponent;
import necesse.entity.manager.RegionUnloadedListenerEntityComponent;
import necesse.entity.manager.WorldRegionLoadedEntityComponent;
import necesse.entity.manager.WorldRegionUnloadedEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.RegionUnloadBuffer;
import necesse.level.maps.regionSystem.SubRegionDataRegionLayer;
import necesse.level.maps.regionSystem.layers.BiomeBlendingRegionLayer;
import necesse.level.maps.regionSystem.layers.BiomeRegionLayer;
import necesse.level.maps.regionSystem.layers.JobsRegionLayer;
import necesse.level.maps.regionSystem.layers.LiquidDataRegionLayer;
import necesse.level.maps.regionSystem.layers.LogicRegionLayer;
import necesse.level.maps.regionSystem.layers.ObjectRegionLayer;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.SplattingRegionLayer;
import necesse.level.maps.regionSystem.layers.TileRegionLayer;
import necesse.level.maps.regionSystem.layers.TilesProtectedRegionLayer;
import necesse.level.maps.regionSystem.layers.WireDataRegionLayer;
import necesse.level.maps.regionSystem.layers.lighting.LightingRegionLayer;

public class Region {
    public final RegionManager manager;
    protected boolean isLoadingComplete = false;
    protected long lastWorldTime = 0L;
    public final int regionX;
    public final int regionY;
    public final int tileWidth;
    public final int tileHeight;
    public final int tileXOffset;
    public final int tileYOffset;
    public RegionUnloadBuffer unloadRegionBuffer;
    private final double tilesPerTick;
    private double tileTickBuffer;
    private int currentTileTickX;
    private int currentTileTickY;
    public boolean isPirateVillageRegion = false;
    public final RegionLayersList layers;
    public final BiomeRegionLayer biomeLayer;
    public final BiomeBlendingRegionLayer biomeBlendingLayer;
    public final TileRegionLayer tileLayer;
    public final SplattingRegionLayer splattingLayer;
    public final ObjectRegionLayer objectLayer;
    public final LogicRegionLayer logicLayer;
    public final WireDataRegionLayer wireLayer;
    public final TilesProtectedRegionLayer tilesProtectedLayer;
    public final LiquidDataRegionLayer liquidData;
    public final SubRegionDataRegionLayer subRegionData;
    public final LightingRegionLayer lightLayer;
    public final JobsRegionLayer jobsLayer;
    private LinkedList<LoadingCompleteInterface> loadedEntities = new LinkedList();

    public Region(RegionManager manager, int regionX, int regionY, int tileXOffset, int tileYOffset, int tileWidth, int tileHeight) {
        this.manager = manager;
        this.regionX = regionX;
        this.regionY = regionY;
        this.tileXOffset = tileXOffset;
        this.tileYOffset = tileYOffset;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.unloadRegionBuffer = new RegionUnloadBuffer(regionX, regionY);
        this.tilesPerTick = (double)(tileWidth * tileHeight) / 20.0;
        this.layers = RegionLayerRegistry.getNewLayersArray(this);
        this.biomeLayer = this.layers.getLayer(RegionLayerRegistry.BIOME_LAYER, BiomeRegionLayer.class);
        this.biomeBlendingLayer = this.layers.getLayer(RegionLayerRegistry.BIOME_BLENDING_LAYER, BiomeBlendingRegionLayer.class);
        this.tileLayer = this.layers.getLayer(RegionLayerRegistry.TILE_LAYER, TileRegionLayer.class);
        this.splattingLayer = this.layers.getLayer(RegionLayerRegistry.SPLATTING_LAYER, SplattingRegionLayer.class);
        this.objectLayer = this.layers.getLayer(RegionLayerRegistry.OBJECT_LAYER, ObjectRegionLayer.class);
        this.logicLayer = this.layers.getLayer(RegionLayerRegistry.LOGIC_LAYER, LogicRegionLayer.class);
        this.wireLayer = this.layers.getLayer(RegionLayerRegistry.WIRE_LAYER, WireDataRegionLayer.class);
        this.tilesProtectedLayer = this.layers.getLayer(RegionLayerRegistry.TILE_PROTECTED_LAYER, TilesProtectedRegionLayer.class);
        this.jobsLayer = this.layers.getLayer(RegionLayerRegistry.JOBS_LAYER, JobsRegionLayer.class);
        this.liquidData = this.layers.getLayer(RegionLayerRegistry.LIQUID_DATA_LAYER, LiquidDataRegionLayer.class);
        this.subRegionData = this.layers.getLayer(RegionLayerRegistry.SUB_REGION_DATA_LAYER, SubRegionDataRegionLayer.class);
        this.lightLayer = this.layers.getLayer(RegionLayerRegistry.LIGHT_LAYER, LightingRegionLayer.class);
        for (RegionLayer layer : this.layers) {
            layer.init();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addSaveData(SaveData save) {
        this.layers.addSaveData(save);
        save.addLong("lastWorldTime", this.lastWorldTime);
        save.addInt("tileTickX", this.currentTileTickX);
        save.addInt("tileTickY", this.currentTileTickY);
        SaveData objectEntitiesSave = new SaveData("OBJECTENTITIES");
        Object object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (ObjectEntity objectEntity : this.manager.level.entityManager.objectEntities.getInRegion(this.regionX, this.regionY)) {
                if (objectEntity.removed() || !objectEntity.shouldSave()) continue;
                objectEntitiesSave.addSaveData(ObjectEntitySave.getSave(objectEntity));
            }
        }
        if (!objectEntitiesSave.isEmpty()) {
            save.addSaveData(objectEntitiesSave);
        }
        this.saveEntitiesList(save, this.manager.level.entityManager.mobs, "MOBS", mob -> !mob.removed() && mob.shouldSave(), mob -> MobSave.getSave("MOB", mob));
        this.saveEntitiesList(save, this.manager.level.entityManager.pickups, "PICKUPENTITIES", pickup -> !pickup.removed() && pickup.shouldSave(), PickupEntitySave::getSave);
        SaveData eventsSave = new SaveData("EVENTS");
        Object object2 = this.manager.level.entityManager.lock;
        synchronized (object2) {
            for (LevelEvent event : this.manager.level.entityManager.events.regionList.getSaveToRegion(this.regionX, this.regionY)) {
                if (event.isOver() || !event.shouldSave()) continue;
                eventsSave.addSaveData(LevelEventSave.getSave(event));
            }
        }
        if (!eventsSave.isEmpty()) {
            save.addSaveData(eventsSave);
        }
        SaveData levelDataSave = new SaveData("LEVELDATA");
        this.manager.level.levelDataManager.addRegionSaveData(this, levelDataSave);
        if (!levelDataSave.isEmpty()) {
            save.addSaveData(levelDataSave);
        }
        save.addBoolean("isPirateVillageRegion", this.isPirateVillageRegion);
    }

    public void loadSaveData(LoadData save, PerformanceTimerManager tickManager, boolean recordConstant) {
        this.loadedEntities = new LinkedList();
        Performance.record(tickManager, "layers", recordConstant, () -> this.layers.loadSaveData(save, tickManager));
        this.lastWorldTime = save.getLong("lastWorldTime", 0L, false);
        Performance.record(tickManager, "objectEntities", recordConstant, () -> {
            LoadData objectEntitiesSave = save.getFirstLoadDataByName("OBJECTENTITIES");
            if (objectEntitiesSave != null) {
                for (LoadData objectEntitySave : objectEntitiesSave.getLoadData()) {
                    try {
                        ObjectEntity loadedObjectEntity = ObjectEntitySave.loadSave(objectEntitySave, this.manager.level);
                        if (loadedObjectEntity == null) continue;
                        this.manager.level.entityManager.objectEntities.addHidden(loadedObjectEntity);
                        this.loadedEntities.add(loadedObjectEntity);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        this.loadEntities(save, this.manager.level.entityManager.mobs, "MOBS", mobSave -> MobSave.loadSave(mobSave, this.manager.level), tickManager, recordConstant);
        this.loadEntities(save, this.manager.level.entityManager.pickups, "PICKUPENTITIES", pickupSave -> PickupEntitySave.loadSave(pickupSave, this.manager.level), tickManager, recordConstant);
        Performance.record(tickManager, "levelEvents", recordConstant, () -> {
            LoadData eventsSave = save.getFirstLoadDataByName("EVENTS");
            if (eventsSave != null) {
                for (LoadData eventSave : eventsSave.getLoadData()) {
                    try {
                        LevelEvent loadedLevelEvent = LevelEventSave.loadSaveData(eventSave, this.manager.level);
                        if (loadedLevelEvent == null) continue;
                        this.manager.level.entityManager.events.addHidden(loadedLevelEvent);
                        this.loadedEntities.add(loadedLevelEvent);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        LoadData levelDataSave = save.getFirstLoadDataByName("LEVELDATA");
        if (levelDataSave != null) {
            this.manager.level.levelDataManager.applyRegionSaveData(this, levelDataSave);
        }
        this.isPirateVillageRegion = save.getBoolean("isPirateVillageRegion", false, false);
        WorldEntity worldEntity = this.manager.level.getWorldEntity();
        if (worldEntity != null && this.lastWorldTime != 0L) {
            long currentTime = worldEntity.getWorldTime();
            long timeIncrease = currentTime - this.lastWorldTime;
            this.simulateWorldTime(timeIncrease, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T extends Entity> void saveEntitiesList(SaveData save, EntityList<T> list, String saveDataName, Predicate<T> shouldSave, Function<T, SaveData> saveDataGetter) {
        SaveData newData = new SaveData(saveDataName);
        Object object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (Entity entity : list.getSaveToRegion(this.regionX, this.regionY)) {
                SaveData saveData;
                if (!shouldSave.test(entity) || (saveData = saveDataGetter.apply(entity)) == null) continue;
                newData.addSaveData(saveData);
            }
        }
        if (!newData.isEmpty()) {
            save.addSaveData(newData);
        }
    }

    protected <T extends Entity> void loadEntities(LoadData save, EntityList<T> list, String saveDataName, Function<LoadData, T> loader, PerformanceTimerManager tickManager, boolean recordConstant) {
        Performance.record(tickManager, list.entityName + "Loading", recordConstant, () -> {
            LoadData saveData = save.getFirstLoadDataByName(saveDataName);
            if (saveData != null) {
                for (LoadData objectEntitySave : saveData.getLoadData()) {
                    try {
                        Entity entity = (Entity)loader.apply(objectEntitySave);
                        if (entity == null) continue;
                        list.addHidden(entity);
                        this.loadedEntities.add(entity);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void onLayerLoaded() {
        this.isLoadingComplete = true;
        for (RegionLayer layer : this.layers) {
            layer.onLayerLoaded();
        }
        if (this.loadedEntities != null) {
            for (LoadingCompleteInterface entity : this.loadedEntities) {
                entity.onLoadingComplete();
            }
            this.loadedEntities = null;
        }
        this.manager.level.returnedItemsManager.onRegionLoaded(this);
        this.manager.level.streamAll(RegionLoadedListenerEntityComponent.class).forEach(listener -> listener.onRegionLoaded(this));
        this.manager.level.streamAllWorld(WorldRegionLoadedEntityComponent.class).forEach(listener -> listener.onLevelRegionLoaded(this.manager.level, this));
    }

    public void onLoadingComplete() {
        this.isLoadingComplete = true;
        for (RegionLayer layer : this.layers) {
            layer.onLoadingComplete();
        }
        this.loadedEntities = null;
        this.manager.level.returnedItemsManager.onRegionLoaded(this);
        this.manager.level.streamAll(RegionLoadedListenerEntityComponent.class).forEach(listener -> listener.onRegionLoaded(this));
        this.manager.level.streamAllWorld(WorldRegionLoadedEntityComponent.class).forEach(listener -> listener.onLevelRegionLoaded(this.manager.level, this));
    }

    public boolean isLoadingComplete() {
        return this.isLoadingComplete;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUnloading() {
        this.manager.level.returnedItemsManager.onRegionUnloading(this);
        Object object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (ObjectEntity objectEntity : this.manager.level.entityManager.objectEntities.getInRegion(this.regionX, this.regionY)) {
                objectEntity.onUnloading(this);
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (DamagedObjectEntity damaged : this.manager.level.entityManager.damagedObjects.getInRegion(this.regionX, this.regionY)) {
                damaged.onUnloading(this);
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (Mob mob : this.manager.level.entityManager.mobs.getSaveToRegion(this.regionX, this.regionY)) {
                if (!mob.shouldRemoveOnRegionUnload()) continue;
                mob.onUnloading(this);
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (PickupEntity pickup : this.manager.level.entityManager.pickups.getSaveToRegion(this.regionX, this.regionY)) {
                if (!pickup.shouldRemoveOnRegionUnload()) continue;
                pickup.onUnloading(this);
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (LevelEvent event : this.manager.level.entityManager.events.regionList.getSaveToRegion(this.regionX, this.regionY)) {
                event.onUnloading(this);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUnloaded() {
        for (RegionLayer layer : this.layers) {
            layer.onLayerUnloaded();
        }
        Object object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (ObjectEntity objectEntity : this.manager.level.entityManager.objectEntities.getInRegion(this.regionX, this.regionY)) {
                objectEntity.remove();
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (DamagedObjectEntity damaged : this.manager.level.entityManager.damagedObjects.getInRegion(this.regionX, this.regionY)) {
                damaged.remove();
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (Mob mob : this.manager.level.entityManager.mobs.getSaveToRegion(this.regionX, this.regionY)) {
                if (!mob.shouldRemoveOnRegionUnload()) continue;
                mob.limitWithinRegionBounds(GameMath.getLevelCoordinate(this.tileXOffset) + 1, GameMath.getLevelCoordinate(this.tileYOffset) + 1, GameMath.getLevelCoordinate(this.tileXOffset + this.tileWidth) - 1, GameMath.getLevelCoordinate(this.tileYOffset + this.tileHeight) - 1);
                mob.remove();
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (PickupEntity pickup : this.manager.level.entityManager.pickups.getSaveToRegion(this.regionX, this.regionY)) {
                if (!pickup.shouldRemoveOnRegionUnload()) continue;
                pickup.limitWithinRegionBounds(GameMath.getLevelCoordinate(this.tileXOffset) + 1, GameMath.getLevelCoordinate(this.tileYOffset) + 1, GameMath.getLevelCoordinate(this.tileXOffset + this.tileWidth) - 1, GameMath.getLevelCoordinate(this.tileYOffset + this.tileHeight) - 1);
                pickup.remove();
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            for (LevelEvent event : this.manager.level.entityManager.events.regionList.getSaveToRegion(this.regionX, this.regionY)) {
                event.over();
            }
        }
        object = this.manager.level.entityManager.lock;
        synchronized (object) {
            this.manager.level.levelDataManager.onUnloadedRegion(this);
        }
        this.subRegionData.invalidate();
        this.updateLight();
        this.manager.level.streamAll(RegionUnloadedListenerEntityComponent.class).forEach(listener -> listener.onRegionUnloaded(this));
        this.manager.level.streamAllWorld(WorldRegionUnloadedEntityComponent.class).forEach(listener -> listener.onLevelRegionUnloaded(this.manager.level, this));
    }

    public void dispose() {
        for (RegionLayer layer : this.layers) {
            layer.onDispose();
        }
    }

    public void writeRegionDataPacket(PacketWriter writer) {
        this.layers.writeRegionLayerPacket(writer);
        writer.putNextBoolean(this.isPirateVillageRegion);
        int settlementUniqueID = SettlementsWorldData.getSettlementsData(this.manager.level).getSettlementUniqueIDAtRegion(this.manager.level.getIdentifier(), this.regionX, this.regionY);
        writer.putNextInt(settlementUniqueID);
    }

    public boolean applyRegionDataPacket(PacketReader reader) {
        boolean recordConstant = this.manager.level.debugLoadingPerformance != null;
        PerformanceTimerManager tickManager = this.manager.level.debugLoadingPerformance != null ? this.manager.level.debugLoadingPerformance : this.manager.level.tickManager();
        boolean valid = Performance.record(tickManager, "applyRegion", recordConstant, () -> this.layers.applyRegionLayerPacket(reader));
        this.isPirateVillageRegion = reader.getNextBoolean();
        int settlementUniqueID = reader.getNextInt();
        if (!valid) {
            return false;
        }
        if (this.manager.level.isClient()) {
            SettlementsWorldData.getSettlementsData(this.manager.level).ensureClientRequestedSettlement(settlementUniqueID);
        }
        Performance.record(tickManager, "objectEntities", recordConstant, this::replaceObjectEntities);
        return true;
    }

    public void frameTick(TickManager tickManager) {
        this.layers.frameTick(tickManager);
    }

    public void clientTick() {
        this.lastWorldTime = this.manager.level.getWorldTime();
        this.layers.clientTick();
        this.unloadRegionBuffer.gameTick(this);
    }

    public void serverTick() {
        this.lastWorldTime = this.manager.level.getWorldTime();
        this.layers.serverTick();
        this.unloadRegionBuffer.gameTick(this);
    }

    public void tickTiles() {
        this.tileTickBuffer += this.tilesPerTick;
        if (this.tileTickBuffer >= 1.0) {
            int ticks = (int)this.tileTickBuffer;
            this.tileTickBuffer -= (double)ticks;
            for (int i = 0; i < ticks; ++i) {
                this.layers.tickTileByRegion(this.currentTileTickX, this.currentTileTickY);
                ++this.currentTileTickX;
                if (this.currentTileTickX < this.tileWidth) continue;
                this.currentTileTickX = 0;
                ++this.currentTileTickY;
                if (this.currentTileTickY < this.tileHeight) continue;
                this.currentTileTickY = 0;
            }
        }
    }

    public void tickTileEffect(GameCamera camera, PlayerMob perspective, int tileX, int tileY) {
        int regionTileX = tileX - this.tileXOffset;
        int regionTileY = tileY - this.tileYOffset;
        this.layers.tickTileEffectByRegion(camera, perspective, regionTileX, regionTileY);
    }

    public void onWireUpdate(int tileX, int tileY, int wireID, boolean active) {
        int regionTileX = tileX - this.tileXOffset;
        int regionTileY = tileY - this.tileYOffset;
        this.layers.onWireUpdateByRegion(regionTileX, regionTileY, wireID, active);
    }

    public void simulateWorldTime(long timeIncrease, boolean sendChanges) {
        if (timeIncrease <= 0L) {
            return;
        }
        for (RegionLayer layer : this.layers) {
            layer.simulateWorld(timeIncrease, sendChanges);
        }
    }

    public void updateSubRegions() {
        this.subRegionData.update();
    }

    public void updateLiquidManager() {
        this.manager.level.liquidManager.updateLevel(this, this.tileXOffset - 1, this.tileYOffset - 1, this.tileXOffset + this.tileWidth + 1, this.tileYOffset + this.tileHeight + 1, true, true);
    }

    public void updateSplattingManager() {
        if (this.manager.level.isServer()) {
            return;
        }
        new RegionBoundsExecutor(this.manager, this.tileXOffset - 1, this.tileYOffset - 1, this.tileXOffset + this.tileWidth + 1, this.tileYOffset + this.tileHeight + 1, false).runCoordinates((region, regionTileX, regionTileY) -> region.splattingLayer.updateSplattingByRegion(regionTileX, regionTileY));
    }

    public void replaceObjectEntities() {
        for (int x = 0; x < this.tileWidth; ++x) {
            for (int y = 0; y < this.tileHeight; ++y) {
                this.manager.level.replaceObjectEntity(this.tileXOffset + x, this.tileYOffset + y);
            }
        }
    }

    public void updateLight() {
        this.manager.level.lightManager.updateStaticLight(this.tileXOffset - 1, this.tileYOffset - 1, this.tileXOffset + this.tileWidth + 1, this.tileYOffset + this.tileHeight + 1, true);
    }

    public void updateBiomeBlending() {
        this.manager.level.biomeBlendingManager.updateBlends(this.tileXOffset - 1, this.tileYOffset - 1, this.tileXOffset + this.tileWidth + 1, this.tileYOffset + this.tileHeight + 1);
    }

    public Region getRegion(int regionX, int regionY, boolean loadIfNotLoaded) {
        if (regionX == this.regionX && regionY == this.regionY) {
            return this;
        }
        return this.manager.getRegion(regionX, regionY, loadIfNotLoaded);
    }

    public Region getRegionByTile(int tileX, int tileY, boolean loadIfNotLoaded) {
        if (this.manager.level.tileWidth > 0 && (tileX < 0 || tileX >= this.manager.level.tileWidth)) {
            return null;
        }
        if (this.manager.level.tileHeight > 0 && (tileY < 0 || tileY >= this.manager.level.tileHeight)) {
            return null;
        }
        int regionX = this.manager.getRegionCoordByTile(tileX);
        int regionY = this.manager.getRegionCoordByTile(tileY);
        if (regionX == this.regionX && regionY == this.regionY) {
            return this;
        }
        return this.manager.getRegion(regionX, regionY, loadIfNotLoaded);
    }

    public AbstractMusicList getLevelMusic(int tileX, int tileY, PlayerMob perspective) {
        if (this.isPirateVillageRegion) {
            return new MusicList(MusicRegistry.PiratesHorizon);
        }
        int regionTileX = tileX - this.tileXOffset;
        int regionTileY = tileY - this.tileYOffset;
        Biome biome = this.biomeLayer.getBiomeByRegion(regionTileX, regionTileY);
        if (biome == BiomeRegistry.UNKNOWN) {
            return this.manager.level.baseBiome.getLevelMusic(this.manager.level, perspective);
        }
        return biome.getLevelMusic(this.manager.level, perspective);
    }

    public void checkGenerationValid() {
        int regionTileY;
        int regionTileX;
        for (regionTileX = 0; regionTileX < this.tileWidth; ++regionTileX) {
            for (regionTileY = 0; regionTileY < this.tileHeight; ++regionTileY) {
                if (this.tileLayer.getTileIDByRegion(regionTileX, regionTileY) == 0) continue;
                GameTile tile = this.tileLayer.getTileByRegion(regionTileX, regionTileY);
                tile.tickValid(this.manager.level, this.tileXOffset + regionTileX, this.tileYOffset + regionTileY, true);
            }
        }
        for (regionTileX = 0; regionTileX < this.tileWidth; ++regionTileX) {
            for (regionTileY = 0; regionTileY < this.tileHeight; ++regionTileY) {
                for (int layerID : ObjectLayerRegistry.getLayerIDs()) {
                    if (this.objectLayer.getObjectIDByRegion(layerID, regionTileX, regionTileY) == 0) continue;
                    GameObject object = this.objectLayer.getObjectByRegion(layerID, regionTileX, regionTileY);
                    object.tickValid(this.manager.level, layerID, this.tileXOffset + regionTileX, this.tileYOffset + regionTileY, true, true);
                }
            }
        }
        for (Mob mob : this.manager.level.entityManager.mobs.getInRegion(this.regionX, this.regionY)) {
            if (!mob.collidesWith(this.manager.level)) continue;
            mob.remove();
        }
        this.tickGenerationEdgeValid(true);
        this.tickGenerationEdgeValid(false);
    }

    private void tickGenerationEdgeValid(boolean isTiles) {
        for (int tileX = this.tileXOffset - 1; tileX < this.tileXOffset + this.tileWidth + 1; ++tileX) {
            int startTileY = this.tileYOffset - 1;
            Region.tickGenerationValidTile(this.manager.level, tileX, startTileY, isTiles);
            int endTileY = this.tileYOffset + this.tileHeight + 1;
            Region.tickGenerationValidTile(this.manager.level, tileX, endTileY, isTiles);
        }
        for (int tileY = this.tileYOffset - 1; tileY < this.tileYOffset + this.tileHeight + 1; ++tileY) {
            int startTileX = this.tileXOffset - 1;
            Region.tickGenerationValidTile(this.manager.level, startTileX, tileY, isTiles);
            int endTileX = this.tileXOffset + this.tileWidth + 1;
            Region.tickGenerationValidTile(this.manager.level, endTileX, tileY, isTiles);
        }
    }

    private static void tickGenerationValidTile(Level level, int tileX, int tileY, boolean isTiles) {
        Region region = level.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return;
        }
        int regionTileX = tileX - region.tileXOffset;
        int regionTileY = tileY - region.tileYOffset;
        if (isTiles) {
            GameTile tile = region.tileLayer.getTileByRegion(regionTileX, regionTileY);
            tile.tickValid(region.manager.level, region.tileXOffset + regionTileX, region.tileYOffset + regionTileY, true);
        } else {
            for (int layerID : ObjectLayerRegistry.getLayerIDs()) {
                if (region.objectLayer.getObjectIDByRegion(layerID, regionTileX, regionTileY) == 0) continue;
                GameObject object = region.objectLayer.getObjectByRegion(layerID, regionTileX, regionTileY);
                object.tickValid(region.manager.level, layerID, region.tileXOffset + regionTileX, region.tileYOffset + regionTileY, true, true);
            }
        }
    }

    public static void checkTilesGenerationValid(Level level, Rectangle tileRectangle) {
        Object tile;
        int tileY;
        int tileX;
        for (tileX = tileRectangle.x; tileX < tileRectangle.x + tileRectangle.width; ++tileX) {
            for (tileY = tileRectangle.y; tileY < tileRectangle.y + tileRectangle.height; ++tileY) {
                tile = level.getTile(tileX, tileY);
                if (((GameTile)tile).getID() == 0) continue;
                ((GameTile)tile).tickValid(level, tileX, tileY, true);
            }
        }
        for (tileX = tileRectangle.x; tileX < tileRectangle.x + tileRectangle.width; ++tileX) {
            for (tileY = tileRectangle.y; tileY < tileRectangle.y + tileRectangle.height; ++tileY) {
                tile = ObjectLayerRegistry.getLayerIDs().iterator();
                while (tile.hasNext()) {
                    int layerID = (Integer)tile.next();
                    GameObject object = level.getObject(layerID, tileX, tileY);
                    if (object.getID() == 0) continue;
                    object.tickValid(level, layerID, tileX, tileY, true, true);
                }
            }
        }
        int startRegionX = level.regionManager.getRegionXByTileLimited(tileRectangle.x);
        int startRegionY = level.regionManager.getRegionYByTileLimited(tileRectangle.y);
        int endRegionX = level.regionManager.getRegionXByTileLimited(tileRectangle.x + tileRectangle.width - 1);
        int endRegionY = level.regionManager.getRegionYByTileLimited(tileRectangle.y + tileRectangle.height - 1);
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                for (Mob mob : level.entityManager.mobs.getInRegion(regionX, regionY)) {
                    if (!mob.collidesWith(level)) continue;
                    mob.remove();
                }
            }
        }
    }
}

