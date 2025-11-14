/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.awt.Point;
import java.util.Objects;
import necesse.engine.GameState;
import necesse.engine.GlobalData;
import necesse.engine.WorldSettingsGetter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.SingletonPointSet;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.DrawOnMapEntity;
import necesse.entity.LoadingCompleteInterface;
import necesse.level.maps.Level;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPositionGetter;
import necesse.level.maps.regionSystem.RegionTracker;
import necesse.level.maps.regionSystem.RegionTrackerGetter;
import necesse.level.maps.regionSystem.TileEntityRegionPositionTracker;

public abstract class TileEntity
implements SoundEmitter,
RegionPositionGetter,
RegionTrackerGetter<TileEntity>,
GameState,
WorldSettingsGetter,
WorldEntityGameClock,
DrawOnMapEntity,
LoadingCompleteInterface {
    private boolean isInitialized;
    private boolean removed;
    private boolean disposed;
    private Level level;
    private WorldEntity worldEntity;
    private WorldSettings worldSettings;
    public final int tileX;
    public final int tileY;
    private boolean isDirty;
    private final RegionTracker<TileEntity> regionTracker = new TileEntityRegionPositionTracker<TileEntity>(this){

        @Override
        public boolean isDisposed() {
            return TileEntity.this.removed();
        }
    };

    @Override
    public RegionTracker<TileEntity> getRegionTracker() {
        return this.regionTracker;
    }

    public TileEntity(Level level, int tileX, int tileY) {
        this.setLevel(level);
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void init() {
        this.isInitialized = true;
    }

    public void postInit() {
    }

    public void onLevelChanged() {
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public abstract void clientTick();

    public abstract void serverTick();

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return GlobalData.debugCheatActive() || map.isTileKnown(this.tileX, this.tileY);
    }

    @Override
    public Point getMapPos() {
        return new Point(this.tileX * 32 + 16, this.tileY * 32 + 16);
    }

    @Deprecated
    public boolean isClientLevel() {
        return this.isClient();
    }

    @Override
    public boolean isClient() {
        return this.level != null && this.level.isClient();
    }

    @Override
    public Client getClient() {
        return this.level == null ? null : this.level.getClient();
    }

    @Deprecated
    public boolean isServerLevel() {
        return this.isServer();
    }

    @Override
    public boolean isServer() {
        return this.level != null && this.level.isServer();
    }

    @Override
    public Server getServer() {
        return this.level == null ? null : this.level.getServer();
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        Objects.requireNonNull(level);
        this.level = level;
        this.worldEntity = level.getWorldEntity();
        this.worldSettings = level.getWorldSettings();
    }

    @Override
    public WorldSettings getWorldSettings() {
        return this.worldSettings;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.worldEntity;
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        return new SingletonPointSet(this.getLevel().regionManager.getRegionCoordByTile(this.tileX), this.getLevel().regionManager.getRegionCoordByTile(this.tileY));
    }

    @Override
    public float getSoundPositionX() {
        return this.tileX * 32 + 16;
    }

    @Override
    public float getSoundPositionY() {
        return this.tileY * 32 + 16;
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public void markClean() {
        this.isDirty = false;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onUnloading(Region region) {
    }

    public void remove() {
        if (!this.removed) {
            this.regionTracker.clearElements();
        }
        this.removed = true;
    }

    public void onRemovedFromManager() {
    }

    public boolean removed() {
        return this.removed;
    }

    public void dispose() {
        this.disposed = true;
    }

    public boolean isDisposed() {
        return this.disposed;
    }
}

