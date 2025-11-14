/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.util.List;
import necesse.engine.GameState;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.SingletonPointSet;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.entity.Entity;
import necesse.entity.LoadingCompleteInterface;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.levelEvent.actions.LevelEventActionRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.LevelEventRegionPositionsTracker;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPositionGetter;
import necesse.level.maps.regionSystem.RegionTracker;
import necesse.level.maps.regionSystem.RegionTrackerGetter;

public class LevelEvent
implements RegionPositionGetter,
RegionTrackerGetter<LevelEvent>,
GameState,
WorldEntityGameClock,
LoadingCompleteInterface {
    public final IDData idData = new IDData();
    protected boolean shouldSave;
    private int uniqueID;
    private boolean isOver;
    public Level level;
    private final LevelEventActionRegistry actions = new LevelEventActionRegistry(this);
    private final RegionTracker<LevelEvent> regionTracker = new LevelEventRegionPositionsTracker(this);

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    protected LevelEvent(boolean networkCapable) {
        if (networkCapable) {
            LevelEventRegistry.instance.applyIDData(this.getClass(), this.idData);
        }
    }

    protected LevelEvent() {
        this(true);
    }

    public boolean isNetworkImportant() {
        return false;
    }

    public boolean shouldSendOverPacket() {
        return this.isNetworkImportant();
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.getUniqueID());
    }

    public void applyLoadData(LoadData save) {
        this.setUniqueID(save.getInt("uniqueID", this.getRealUniqueID()));
    }

    public void applySpawnPacket(PacketReader reader) {
        this.setUniqueID(reader.getNextInt());
    }

    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.getUniqueID());
    }

    public void init() {
        this.actions.closeRegistry();
    }

    public void tickMovement(float delta) {
    }

    public void clientTick() {
    }

    public void serverTick() {
    }

    public void addDrawables(List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables, LevelDrawUtils.DrawArea drawArea, Level level, TickManager tickManager, GameCamera camera) {
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        Point regionPos = this.getSaveToRegionPos();
        if (regionPos != null) {
            return new SingletonPointSet(regionPos.x, regionPos.y);
        }
        return new PointHashSet();
    }

    public Point getSaveToRegionPos() {
        return null;
    }

    @Override
    public RegionTracker<LevelEvent> getRegionTracker() {
        return this.regionTracker;
    }

    public boolean shouldSave() {
        return this.shouldSave;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public int getUniqueID(GameRandom random) {
        if (this.uniqueID == 0) {
            this.uniqueID = Entity.getNewUniqueID(this.level, random);
        }
        return this.uniqueID;
    }

    public int getUniqueID() {
        return this.getUniqueID(null);
    }

    public int getRealUniqueID() {
        return this.uniqueID;
    }

    public int resetUniqueID(GameRandom random) {
        this.uniqueID = 0;
        return this.getUniqueID(random);
    }

    public final void runAction(int id, PacketReader reader) {
        this.actions.runAction(id, reader);
    }

    public <T extends LevelEventAction> T registerAction(T action) {
        return this.actions.registerAction(action);
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.level == null ? null : this.level.getWorldEntity();
    }

    @Override
    public boolean isClient() {
        return this.level != null && this.level.isClient();
    }

    @Override
    public Client getClient() {
        return this.level == null ? null : this.level.getClient();
    }

    @Override
    public boolean isServer() {
        return this.level != null && this.level.isServer();
    }

    @Override
    public Server getServer() {
        return this.level == null ? null : this.level.getServer();
    }

    public void over() {
        if (!this.isOver) {
            this.regionTracker.clearElements();
        }
        this.isOver = true;
    }

    public final boolean isOver() {
        return this.isOver;
    }

    @Override
    public void onLoadingComplete() {
    }

    @Override
    public void onUnloading(Region region) {
    }

    public void onDispose() {
    }

    public static int getTileCoordinate(int levelCoordinate) {
        return GameMath.getTileCoordinate(levelCoordinate);
    }

    public static int getTileCoordinate(float levelCoordinate) {
        return GameMath.getTileCoordinate(levelCoordinate);
    }

    public static int getTileCoordinate(double levelCoordinate) {
        return GameMath.getTileCoordinate(levelCoordinate);
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
    }
}

