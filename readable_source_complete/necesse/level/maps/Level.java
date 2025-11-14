/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package necesse.level.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import necesse.engine.AbstractMusicList;
import necesse.engine.DisposableExecutorService;
import necesse.engine.GameEvents;
import necesse.engine.GameState;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.WorldSettingsGetter;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketChangeObjects;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelLayerRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.RayLinkedList;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.ObjectDamageResult;
import necesse.entity.TileDamageResult;
import necesse.entity.manager.EntityComponent;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobDeathListenerEntityComponent;
import necesse.entity.manager.MobSpawnedListenerEntityComponent;
import necesse.entity.manager.ObjectDamagedListenerEntityComponent;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.manager.ObjectLootTableDroppedListenerEntityComponent;
import necesse.entity.manager.ObjectPlacedListenerEntityComponent;
import necesse.entity.manager.TileDamagedListenerEntityComponent;
import necesse.entity.manager.TileDestroyedListenerEntityComponent;
import necesse.entity.manager.TileLootTableDroppedListenerEntityComponent;
import necesse.entity.manager.TilePlacedListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.WallShadowVariables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CollisionPoint;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.LevelReturnedItemsManager;
import necesse.level.maps.LevelStats;
import necesse.level.maps.LevelTile;
import necesse.level.maps.LevelTilesSpliterator;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.layers.LogicGateLayerManager;
import necesse.level.maps.layers.WeatherLevelLayer;
import necesse.level.maps.levelBuffManager.LevelBuffManager;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.LevelDataManager;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.liquidManager.LiquidManager;
import necesse.level.maps.managers.BiomeBlendingManager;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.regionSystem.managers.BiomeLayerManager;
import necesse.level.maps.regionSystem.managers.JobsLayerManager;
import necesse.level.maps.regionSystem.managers.ObjectLayerManager;
import necesse.level.maps.regionSystem.managers.TileLayerManager;
import necesse.level.maps.wireManager.WireManager;
import org.lwjgl.glfw.GLFW;

public class Level
implements WorldEntityGameClock,
GameState,
WorldSettingsGetter {
    public final IDData idData = new IDData();
    public PerformanceTimerManager debugLoadingPerformance;
    public int debugLoadingPrintCooldown = 0;
    public int remainingDebugLoadingPrints = 0;
    protected int debugLoadingPrintTimer;
    public static int EXECUTOR_POOL_SIZE = 5;
    public ArrayList<ModSaveInfo> lastMods = null;
    public final HashSet<LevelIdentifier> childLevels = new HashSet();
    protected final ArrayList<Runnable> glContextRunnables = new ArrayList();
    private LevelIdentifier identifier;
    public final boolean isIncursionLevel;
    public final boolean isTrialRoom;
    private boolean loadingComplete;
    public final int tileWidth;
    public final int tileHeight;
    public boolean isCave;
    public boolean isProtected = false;
    public boolean alwaysDrawWire = false;
    public LevelIdentifier fallbackIdentifier;
    public Point fallbackTilePos;
    public final LevelLayer[] layers;
    public final WeatherLevelLayer weatherLayer;
    public final RegionManager regionManager;
    public final BiomeLayerManager biomeLayer;
    public final TileLayerManager tileLayer;
    public final ObjectLayerManager objectLayer;
    public final LogicGateLayerManager logicLayer;
    public final JobsLayerManager jobsLayer;
    protected PointHashSet dirtyRegions = null;
    public final LevelReturnedItemsManager returnedItemsManager;
    public boolean keepTrackOfReturnedItems = false;
    public final EntityManager entityManager;
    public final WireManager wireManager;
    public final LevelDataManager levelDataManager;
    private final HashMap<Long, Long> grassWeave;
    public final LightManager lightManager;
    public final BiomeBlendingManager biomeBlendingManager;
    public final LevelBuffManager buffManager;
    public final GNDItemMap gndData;
    public final LiquidManager liquidManager;
    public final HudManager hudManager;
    public final LevelDrawUtils drawUtils;
    private boolean isDisposed;
    private DisposableExecutorService executor;
    private final Object executorLock = new Object();
    public Biome baseBiome = BiomeRegistry.UNKNOWN;
    public int unloadLevelBuffer;
    protected boolean lastPreventSleep;
    protected boolean preventSleep;
    public int presentPlayers;
    public int presentAdventurePartyMembers;
    public long lastWorldTime;
    private WorldEntity worldEntity;
    private Server server;
    private Client client;
    public final LevelStats levelStats = new LevelStats();
    public static final Point[] adjacentGetters = new Point[]{new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1)};
    public static final Point[] adjacentGettersNotDiagonal = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};
    public static final Point[] adjacentGettersWithCenter = new Point[]{new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1)};
    protected static WorldSettings defaultWorldSettings = new WorldSettings(null);

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Level(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        this.identifier = identifier;
        this.tileWidth = width;
        this.tileHeight = height;
        this.isIncursionLevel = this instanceof IncursionLevel;
        this.isTrialRoom = this instanceof TrialRoomLevel;
        this.setWorldEntity(worldEntity);
        LevelRegistry.instance.applyIDData(this.getClass(), this.idData);
        this.layers = LevelLayerRegistry.getNewLayersArray(this);
        this.weatherLayer = this.getLayer(LevelLayerRegistry.RAINING_LAYER, WeatherLevelLayer.class);
        this.regionManager = new RegionManager(this);
        this.biomeLayer = new BiomeLayerManager(this);
        this.tileLayer = new TileLayerManager(this);
        this.objectLayer = new ObjectLayerManager(this);
        this.logicLayer = new LogicGateLayerManager(this);
        this.jobsLayer = new JobsLayerManager(this);
        this.wireManager = this.constructWireManager();
        this.levelDataManager = this.constructLevelDataManager();
        this.lightManager = this.constructLightManager();
        this.biomeBlendingManager = new BiomeBlendingManager(this);
        this.liquidManager = this.constructLiquidManager();
        this.returnedItemsManager = new LevelReturnedItemsManager(this);
        this.entityManager = this.constructEntityManager();
        this.buffManager = this.constructLevelBuffManager();
        this.gndData = new GNDItemMap();
        this.hudManager = this.constructHudManager();
        this.drawUtils = this.constructLevelDrawUtils();
        this.grassWeave = new HashMap();
        for (LevelLayer layer : this.layers) {
            layer.init();
        }
    }

    public RegionManager constructRegionManager() {
        return new RegionManager(this);
    }

    public WireManager constructWireManager() {
        return new WireManager(this);
    }

    public LevelDataManager constructLevelDataManager() {
        return new LevelDataManager(this);
    }

    public LightManager constructLightManager() {
        return new LightManager(this);
    }

    public LiquidManager constructLiquidManager() {
        return new LiquidManager(this);
    }

    public EntityManager constructEntityManager() {
        return new EntityManager(this);
    }

    public LevelBuffManager constructLevelBuffManager() {
        return new LevelBuffManager(this);
    }

    public HudManager constructHudManager() {
        return new HudManager(this);
    }

    public LevelDrawUtils constructLevelDrawUtils() {
        return new LevelDrawUtils(this);
    }

    public void writeLevelDataPacket(PacketWriter writer) {
        writer.putNextInt(this.baseBiome.getID());
        writer.putNextBoolean(this.isCave);
        writer.putNextBoolean(this.isProtected);
        for (LevelLayer layer : this.layers) {
            layer.writeLevelDataPacket(writer);
        }
        this.gndData.writePacket(writer);
    }

    public void readLevelDataPacket(PacketReader reader) {
        this.baseBiome = BiomeRegistry.getBiome(reader.getNextInt());
        this.isCave = reader.getNextBoolean();
        this.isProtected = reader.getNextBoolean();
        for (LevelLayer layer : this.layers) {
            layer.readLevelDataPacket(reader);
        }
        this.gndData.readPacket(reader);
    }

    public boolean shouldSave() {
        return true;
    }

    public void addSaveData(SaveData save) {
        LevelSave.addLevelBasics(this, save);
    }

    public void applyLoadData(LoadData save) {
        LevelSave.applyLevelBasics(this, save);
    }

    public void onLoadingComplete() {
        this.loadingComplete = true;
        for (LevelLayer layer : this.layers) {
            layer.onLoadingComplete();
        }
        this.regionManager.onLoadingComplete();
        this.buffManager.forceUpdateBuffs();
        this.entityManager.onLoadingComplete();
        this.levelDataManager.onLoadingComplete();
    }

    public boolean isLoadingComplete() {
        return this.loadingComplete;
    }

    public void onUnloading() {
        if (this.isServer()) {
            this.getServer().streamClients().forEach(c -> c.removeLoadedRegions(this));
        }
        this.entityManager.onUnloading();
    }

    public void simulateSinceLastWorldTime(boolean sendChanges) {
        if (this.worldEntity != null && this.lastWorldTime != 0L) {
            long currentTime = this.worldEntity.getWorldTime();
            long timeIncrease = currentTime - this.lastWorldTime;
            for (LevelLayer layer : this.layers) {
                layer.simulateWorld(timeIncrease, sendChanges);
            }
        }
    }

    public void simulateWorldTime(long timeIncrease, boolean sendChanges) {
        if (timeIncrease <= 0L) {
            return;
        }
        for (LevelLayer layer : this.layers) {
            layer.simulateWorld(timeIncrease, sendChanges);
        }
        this.regionManager.simulateWorldTime(timeIncrease, sendChanges);
    }

    public int limitTileXToBounds(int tileX) {
        if (this.tileWidth > 0) {
            return GameMath.limit(tileX, 0, this.tileWidth - 1);
        }
        return tileX;
    }

    public int limitTileXToBounds(int tileX, int width, int tilePadding) {
        if (this.tileWidth > 0) {
            return GameMath.limit(tileX, tilePadding, this.tileWidth - 1 - width - tilePadding);
        }
        return tileX;
    }

    public int limitTileYToBounds(int tileY) {
        if (this.tileHeight > 0) {
            return GameMath.limit(tileY, 0, this.tileHeight - 1);
        }
        return tileY;
    }

    public int limitTileYToBounds(int tileY, int height, int tilePadding) {
        if (this.tileHeight > 0) {
            return GameMath.limit(tileY, tilePadding, this.tileHeight - 1 - height - tilePadding);
        }
        return tileY;
    }

    public boolean isTileXWithinBounds(int tileX) {
        if (this.tileWidth > 0) {
            return tileX >= 0 && tileX < this.tileWidth;
        }
        return true;
    }

    public boolean isTileYWithinBounds(int tileY) {
        if (this.tileHeight > 0) {
            return tileY >= 0 && tileY < this.tileHeight;
        }
        return true;
    }

    public boolean isTileXWithinBounds(int tileX, int padding) {
        if (this.tileWidth > 0) {
            return tileX >= padding && tileX < this.tileWidth - padding;
        }
        return true;
    }

    public boolean isTileYWithinBounds(int tileY, int padding) {
        if (this.tileHeight > 0) {
            return tileY >= padding && tileY < this.tileHeight - padding;
        }
        return true;
    }

    public boolean isTileWithinBounds(int tileX, int tileY) {
        return this.isTileXWithinBounds(tileX) && this.isTileYWithinBounds(tileY);
    }

    public boolean isTileWithinBounds(int tileX, int tileY, int padding) {
        return this.isTileXWithinBounds(tileX, padding) && this.isTileYWithinBounds(tileY, padding);
    }

    public double limitLevelXToBounds(double levelX) {
        if (this.tileWidth > 0) {
            return GameMath.limit(levelX, 0.0, (double)(GameMath.getLevelCoordinate(this.tileWidth) - 1));
        }
        return levelX;
    }

    public double limitLevelXToBounds(double levelX, double width, double padding) {
        if (this.tileWidth > 0) {
            return GameMath.limit(levelX, padding, (double)(GameMath.getLevelCoordinate(this.tileWidth) - 1) - width - padding);
        }
        return levelX;
    }

    public double limitLevelYToBounds(double levelY) {
        if (this.tileHeight > 0) {
            return GameMath.limit(levelY, 0.0, (double)(GameMath.getLevelCoordinate(this.tileHeight) - 1));
        }
        return levelY;
    }

    public double limitLevelYToBounds(double levelY, double height, double padding) {
        if (this.tileHeight > 0) {
            return GameMath.limit(levelY, padding, (double)(GameMath.getLevelCoordinate(this.tileHeight) - 1) - height - padding);
        }
        return levelY;
    }

    public int limitLevelXToBounds(int levelX) {
        if (this.tileWidth > 0) {
            return GameMath.limit(levelX, 0, GameMath.getLevelCoordinate(this.tileWidth) - 1);
        }
        return levelX;
    }

    public int limitLevelXToBounds(int levelX, int width, int padding) {
        if (this.tileWidth > 0) {
            return GameMath.limit(levelX, padding, GameMath.getLevelCoordinate(this.tileWidth) - 1 - width - padding);
        }
        return levelX;
    }

    public int limitLevelYToBounds(int levelY) {
        if (this.tileHeight > 0) {
            return GameMath.limit(levelY, 0, GameMath.getLevelCoordinate(this.tileHeight) - 1);
        }
        return levelY;
    }

    public int limitLevelYToBounds(int levelY, int height, int padding) {
        if (this.tileHeight > 0) {
            return GameMath.limit(levelY, padding, GameMath.getLevelCoordinate(this.tileHeight) - 1 - height - padding);
        }
        return levelY;
    }

    public boolean isLevelXWithinBounds(double levelX) {
        if (this.tileWidth > 0) {
            return levelX >= 0.0 && levelX < (double)GameMath.getLevelCoordinate(this.tileWidth);
        }
        return true;
    }

    public boolean isLevelYWithinBounds(double levelY) {
        if (this.tileHeight > 0) {
            return levelY >= 0.0 && levelY < (double)GameMath.getLevelCoordinate(this.tileHeight);
        }
        return true;
    }

    public boolean isLevelPosWithinBounds(double levelX, double levelY) {
        return this.isLevelXWithinBounds(levelX) && this.isLevelYWithinBounds(levelY);
    }

    public boolean isOneWorldLevel() {
        return false;
    }

    public void generateRegion(Region region) {
    }

    public void onRegionGenerated(Region region, boolean skipGenerateForced) {
    }

    public void onGenerateRegionSkipped(Region region) {
    }

    public int getRegionID(int tileX, int tileY) {
        return this.regionManager.getRegionIDByTile(tileX, tileY);
    }

    public int getRoomID(int tileX, int tileY) {
        return this.regionManager.getRoomIDByTile(tileX, tileY);
    }

    public boolean isOutside(int tileX, int tileY) {
        return this.regionManager.isOutsideByTile(tileX, tileY);
    }

    public int getRoomSize(int roomID) {
        return this.regionManager.getRoomSize(roomID);
    }

    public int getRoomSize(int tileX, int tileY) {
        return this.getRoomSize(this.getRoomID(tileX, tileY));
    }

    public long getSeed() {
        return WorldGenerator.getSeed(this.identifier);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addGLContextRunnable(Runnable runnable) {
        ArrayList<Runnable> arrayList = this.glContextRunnables;
        synchronized (arrayList) {
            long currentContext = GLFW.glfwGetCurrentContext();
            if (currentContext != 0L) {
                runnable.run();
            } else {
                this.glContextRunnables.add(runnable);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runGLContextRunnables() {
        ArrayList<Runnable> arrayList = this.glContextRunnables;
        synchronized (arrayList) {
            for (Runnable runnable : this.glContextRunnables) {
                runnable.run();
            }
            this.glContextRunnables.clear();
        }
    }

    public void draw(GameCamera camera, PlayerMob perspective, TickManager tickManager, boolean singleDraw) {
        if (this.lightManager == null || this.lightManager.isDisposed()) {
            return;
        }
        if (!this.isServer()) {
            this.lightManager.ensureSetting(Settings.lights);
        }
        this.drawUtils.draw(camera, perspective, tickManager, singleDraw);
    }

    public void drawHud(GameCamera camera, PlayerMob perspective, TickManager tickManager) {
        this.drawUtils.drawLastHudDrawables(camera, perspective, tickManager);
    }

    public void tickEffect(GameCamera camera, PlayerMob perspective) {
        LevelDrawUtils.DrawArea area = new LevelDrawUtils.DrawArea(this, camera);
        for (LevelLayer layer : this.layers) {
            layer.tickTileEffects(camera, perspective, area);
        }
        float rainAlpha = this.weatherLayer.getRainAlpha();
        double minDistance = -1.0;
        GameSound minDistanceRainSound = null;
        for (int tileX = area.startTileX; tileX < area.endTileX; ++tileX) {
            for (int tileY = area.startTileY; tileY < area.endTileY; ++tileY) {
                GameSound rainSound;
                Biome biome;
                this.regionManager.tickTileEffect(camera, perspective, tileX, tileY);
                if (!(rainAlpha > 0.0f) || !this.isOutside(tileX, tileY) || !(biome = this.getBiome(tileX, tileY)).canRain(this)) continue;
                if (perspective != null && (rainSound = biome.getRainSound(this)) != null) {
                    double distance = GameMath.diagonalMoveDistance(perspective.getX(), perspective.getY(), tileX * 32 + 16, tileY * 32 + 16);
                    if (minDistance == -1.0 || distance < minDistance) {
                        minDistance = distance;
                        minDistanceRainSound = rainSound;
                    }
                }
                biome.tickRainEffect(camera, this, tileX, tileY, rainAlpha);
            }
        }
        if (minDistanceRainSound != null) {
            SoundManager.setWeatherSound(minDistanceRainSound, 0.0f, 0.0f, (float)minDistance, rainAlpha);
        }
    }

    public void clientTick() {
        if (this.worldEntity != null) {
            this.lastWorldTime = this.worldEntity.getWorldTime();
        }
        this.lastPreventSleep = this.preventSleep;
        this.preventSleep = false;
        this.lightManager.ensureSetting(Settings.lights);
        this.lightManager.updateAmbientLight();
        this.liquidManager.clientTick();
        this.wireManager.clientTick();
        this.buffManager.clientTick();
        Performance.record((PerformanceTimerManager)this.tickManager(), "entities", this.entityManager::clientTick);
        this.tickGrassWeave();
        this.levelDataManager.tick();
        this.hudManager.tick();
        Performance.record((PerformanceTimerManager)this.tickManager(), "regions", () -> {
            this.regionManager.clientTick();
            this.regionManager.tickTiles();
        });
        Performance.record((PerformanceTimerManager)this.tickManager(), "levelLayers", () -> {
            for (LevelLayer layer : this.layers) {
                layer.clientTick();
            }
        });
        this.presentPlayers = 0;
        if (this.getClient() != null) {
            for (int i = 0; i < this.getClient().getSlots(); ++i) {
                ClientClient client = this.getClient().getClient(i);
                if (client == null || !client.isSamePlace(this) || !client.hasSpawned() || client.isDead()) continue;
                ++this.presentPlayers;
            }
        }
        this.tickDebugLoadingTimer();
    }

    public void serverTick() {
        if (this.worldEntity != null) {
            this.lastWorldTime = this.worldEntity.getWorldTime();
        }
        this.lastPreventSleep = this.preventSleep;
        this.preventSleep = false;
        this.lightManager.updateAmbientLight();
        this.regionManager.resetFinalLights();
        this.wireManager.serverTick();
        this.buffManager.serverTick();
        Performance.record((PerformanceTimerManager)this.tickManager(), "entities", this.entityManager::serverTick);
        ++this.unloadLevelBuffer;
        this.levelDataManager.tick();
        Performance.record((PerformanceTimerManager)this.tickManager(), "levelLayers", () -> {
            this.regionManager.serverTick();
            this.regionManager.tickTiles();
            for (LevelLayer layer : this.layers) {
                layer.serverTick();
            }
        });
        this.presentPlayers = 0;
        this.presentAdventurePartyMembers = 0;
        if (this.isServer()) {
            for (int i = 0; i < this.getServer().getSlots(); ++i) {
                ServerClient client = this.getServer().getClient(i);
                if (client == null || !client.isSamePlace(this)) continue;
                this.unloadLevelBuffer = 0;
                if (!client.hasSpawned() || client.isDead()) continue;
                ++this.presentPlayers;
                this.presentAdventurePartyMembers += client.adventureParty.getSize();
            }
            this.runDirtyRegionSync();
        }
        this.tickDebugLoadingTimer();
    }

    public void frameTick(TickManager tickManager) {
        for (LevelLayer layer : this.layers) {
            layer.frameTick(tickManager);
        }
        this.regionManager.frameTick(tickManager);
        this.entityManager.frameTick(tickManager);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startDirtyRegionTracking() {
        Level level = this;
        synchronized (level) {
            if (this.dirtyRegions == null) {
                this.dirtyRegions = new PointHashSet();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDirtyRegion(int regionX, int regionY) {
        Level level = this;
        synchronized (level) {
            if (this.dirtyRegions != null) {
                this.dirtyRegions.add(regionX, regionY);
            }
        }
    }

    public void addDirtyRegion(Region region) {
        this.addDirtyRegion(region.regionX, region.regionY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeDirtyRegion(int regionX, int regionY) {
        Level level = this;
        synchronized (level) {
            if (this.dirtyRegions != null) {
                this.dirtyRegions.remove(regionX, regionY);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runDirtyRegionSync() {
        Level level = this;
        synchronized (level) {
            if (this.dirtyRegions != null && this.isServer()) {
                for (Point regionPos : this.dirtyRegions) {
                    Region region = this.regionManager.getRegion(regionPos.x, regionPos.y, false);
                    if (region == null) continue;
                    PacketRegionData packet = new PacketRegionData(region);
                    this.getServer().network.sendToClientsWithRegion((Packet)packet, this, region.regionX, region.regionY);
                }
            }
            this.dirtyRegions = null;
        }
    }

    public void startDebugLoadingTimer(int printCooldown, int maxPrints) {
        this.debugLoadingPerformance = new PerformanceTimerManager();
        this.debugLoadingPrintCooldown = printCooldown;
        this.remainingDebugLoadingPrints = maxPrints;
        this.debugLoadingPrintTimer = 0;
    }

    public void endDebugLoadingTimer(boolean printFirst) {
        if (printFirst) {
            this.printDebugLoadingTimer();
        }
        this.debugLoadingPerformance = null;
        this.debugLoadingPrintCooldown = 0;
    }

    protected void tickDebugLoadingTimer() {
        if (this.debugLoadingPerformance != null && this.debugLoadingPrintCooldown > 0) {
            this.debugLoadingPrintTimer += 50;
            if (this.debugLoadingPrintTimer >= this.debugLoadingPrintCooldown) {
                this.debugLoadingPrintTimer = 0;
                this.printDebugLoadingTimer();
                this.debugLoadingPerformance = new PerformanceTimerManager();
                if (this.remainingDebugLoadingPrints > 0) {
                    --this.remainingDebugLoadingPrints;
                    if (this.remainingDebugLoadingPrints == 0) {
                        System.out.println("That was the last print");
                        this.debugLoadingPerformance = null;
                    } else {
                        System.out.println(this.remainingDebugLoadingPrints + " prints remaining");
                    }
                }
            }
        }
    }

    public void printDebugLoadingTimer() {
        if (this.debugLoadingPerformance != null) {
            PerformanceTimer rootTimer = this.debugLoadingPerformance.getCurrentRootPerformanceTimer();
            if (rootTimer != null) {
                System.out.println("Debug performance print for " + this.getIdentifier() + " (" + this.getHostString() + "):");
                PerformanceTimerUtils.printPerformanceTimer(rootTimer);
            } else {
                System.out.println("Could not print debug performance for " + this.getIdentifier() + " (" + this.getHostString() + ") because it had no root timer");
            }
        } else {
            System.out.println("Could not print debug performance for " + this.getIdentifier() + " (" + this.getHostString() + ") because it is not started");
        }
    }

    public GameMessage getSetSpawnError(int x, int y, ServerClient client) {
        return null;
    }

    public void preventSleep() {
        this.preventSleep = true;
    }

    public boolean isSleepPrevented() {
        return this.lastPreventSleep || this.preventSleep;
    }

    public GameLight getLightLevel(int tileX, int tileY) {
        return this.lightManager.getLightLevel(tileX, tileY);
    }

    public GameLight getLightLevelWall(int x, int y) {
        return this.lightManager.getLightLevelWall(x, y);
    }

    public GameLight getLightLevel(Entity entity) {
        return this.getLightLevel(entity.getTileX(), entity.getTileY());
    }

    public float getStaticLightLevelFloat(int tileX, int tileY) {
        return this.lightManager.getAmbientAndStaticLightLevelFloat(tileX, tileY);
    }

    public float getStaticLightLevelFloat(Entity entity) {
        return this.getStaticLightLevelFloat(entity.getTileX(), entity.getTileY());
    }

    public Stream<WallShadowVariables> getWallShadows() {
        float lightLevel = this.getWorldEntity().getAmbientLightFloat();
        if (lightLevel <= 0.0f) {
            return Stream.empty();
        }
        float sunProgress = this.getWorldEntity().getSunProgress();
        if (sunProgress < 0.0f) {
            return Stream.empty();
        }
        return Stream.of(WallShadowVariables.fromProgress(lightLevel, sunProgress, 32.0f, 320.0f));
    }

    public <T extends LevelLayer> T getLayer(int layerID, Class<T> expectedClass) {
        return (T)((LevelLayer)expectedClass.cast(this.layers[layerID]));
    }

    public int getBiomeID(int tileX, int tileY) {
        int biomeID = this.biomeLayer.getBiomeID(tileX, tileY);
        if (biomeID == BiomeRegistry.UNKNOWN.getID()) {
            return this.baseBiome.getID();
        }
        return biomeID;
    }

    public Biome getBiome(int tileX, int tileY) {
        Biome biome = this.biomeLayer.getBiome(tileX, tileY);
        if (biome == BiomeRegistry.UNKNOWN) {
            return this.baseBiome;
        }
        return biome;
    }

    public AbstractMusicList getLevelMusic(int tileX, int tileY, PlayerMob perspective) {
        Region region = this.regionManager.getRegionByTile(tileX, tileY, false);
        if (region == null) {
            return this.baseBiome.getLevelMusic(this, perspective);
        }
        return region.getLevelMusic(tileX, tileY, perspective);
    }

    public boolean canRain() {
        return !this.isCave;
    }

    public int getRainTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(240, 420);
    }

    public int getDryTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(1200, 1800);
    }

    public GameTile getTile(int tileX, int tileY) {
        return this.tileLayer.getTile(tileX, tileY);
    }

    public LevelTile getLevelTile(int tileX, int tileY) {
        return new LevelTile(this, tileX, tileY);
    }

    public int getTileID(int tileX, int tileY) {
        return this.tileLayer.getTileID(tileX, tileY);
    }

    public GameMessage getTileName(int tileX, int tileY) {
        return this.getTile(tileX, tileY).getLocalization();
    }

    public GameMessage getObjectName(int tileX, int tileY) {
        return this.getObject(tileX, tileY).getLocalization();
    }

    public void setTile(int tileX, int tileY, int tile) {
        this.tileLayer.setTile(tileX, tileY, tile);
    }

    public LevelObject getLevelObject(int tileX, int tileY) {
        return new LevelObject(this, tileX, tileY);
    }

    public LevelObject getLevelObject(int layerID, int tileX, int tileY) {
        return new LevelObject(this, layerID, tileX, tileY);
    }

    public LevelObject getMasterLevelObject(int tileX, int tileY) {
        return this.getMasterLevelObject(0, tileX, tileY);
    }

    public LevelObject getMasterLevelObject(int layerID, int tileX, int tileY) {
        LevelObject levelObject = new LevelObject(this, layerID, tileX, tileY);
        if (levelObject.object.isMultiTile()) {
            levelObject = levelObject.getMasterLevelObject().orElse(null);
        }
        return levelObject;
    }

    public GameObject getObject(int layerID, int tileX, int tileY) {
        return this.objectLayer.getObject(layerID, tileX, tileY);
    }

    public int getObjectID(int layerID, int tileX, int tileY) {
        return this.objectLayer.getObjectID(layerID, tileX, tileY);
    }

    public byte getObjectRotation(int layerID, int tileX, int tileY) {
        return this.objectLayer.getObjectRotation(layerID, tileX, tileY);
    }

    public GameObject getObject(int tileX, int tileY) {
        return this.getObject(0, tileX, tileY);
    }

    public int getObjectID(int tileX, int tileY) {
        return this.getObjectID(0, tileX, tileY);
    }

    public byte getObjectRotation(int tileX, int tileY) {
        return this.getObjectRotation(0, tileX, tileY);
    }

    public void setObjectRotation(int tileX, int tileY, int rotation) {
        this.objectLayer.setObjectRotation(0, tileX, tileY, rotation);
    }

    public void setObject(int tileX, int tileY, int object) {
        this.objectLayer.setObject(0, tileX, tileY, object);
    }

    public void setObject(int tileX, int tileY, int object, int objectRotation) {
        this.setObject(tileX, tileY, object);
        this.setObjectRotation(tileX, tileY, objectRotation);
    }

    public void replaceObjectEntity(int tileX, int tileY) {
        GameObject obj = ObjectRegistry.getObject(this.getObjectID(tileX, tileY));
        ObjectEntity objectEntity = obj.getNewObjectEntity(this, tileX, tileY);
        if (objectEntity != null) {
            this.entityManager.objectEntities.add(objectEntity);
            if (this.isClient() && objectEntity.shouldRequestPacket()) {
                this.getClient().loading.objectEntities.addObjectEntityRequest(tileX, tileY);
            }
        } else {
            this.entityManager.removeObjectEntity(tileX, tileY);
        }
    }

    public int getTileAndObjectsHash(int tileX, int tileY) {
        int hash = 1;
        hash = 31 * hash + this.getTileID(tileX, tileY);
        hash = 31 * hash + Boolean.hashCode(this.tileLayer.isPlayerPlaced(tileX, tileY));
        hash = 31 * hash + this.objectLayer.getTileObjectsHash(tileX, tileY);
        return hash;
    }

    public boolean checkExpectedTileAndObjectsHash(ServerClient client, int tileX, int tileY, int expectedHash) {
        if (!this.isTileWithinBounds(tileX, tileY)) {
            return false;
        }
        int current = this.getTileAndObjectsHash(tileX, tileY);
        if (current != expectedHash) {
            client.sendPacket(new PacketChangeTile(this, tileX, tileY));
            client.sendPacket(new PacketChangeObjects(this, tileX, tileY));
            return true;
        }
        return false;
    }

    public int getAdjacentTileAndObjectsHash(int tileX, int tileY) {
        int hash = 1;
        for (Point offset : adjacentGetters) {
            hash = 31 * hash + this.getTileAndObjectsHash(tileX + offset.x, tileY + offset.y);
        }
        return hash;
    }

    public boolean checkExpectedAdjacentTileAndObjectsHash(ServerClient client, int tileX, int tileY, int expectedHash) {
        int current = this.getAdjacentTileAndObjectsHash(tileX, tileY);
        if (current != expectedHash) {
            boolean result = false;
            for (Point offset : adjacentGetters) {
                int offsetTileX = tileX + offset.x;
                int offsetTileY = tileY + offset.y;
                if (!this.isTileWithinBounds(offsetTileX, offsetTileY)) continue;
                result = true;
                client.sendPacket(new PacketChangeTile(this, offsetTileX, offsetTileY));
                client.sendPacket(new PacketChangeObjects(this, offsetTileX, offsetTileY));
            }
            return result;
        }
        return false;
    }

    public void setupTileAndObjectsHashGNDMap(GNDItemMap map, int tileX, int tileY, boolean includeAdjacent) {
        map.setInt("tileAndObjectsHash", this.getTileAndObjectsHash(tileX, tileY));
        if (includeAdjacent) {
            map.setInt("adjacentTileAndObjectsHash", this.getAdjacentTileAndObjectsHash(tileX, tileY));
        }
    }

    public boolean checkTileAndObjectsHashGNDMap(ServerClient client, GNDItemMap map, int tileX, int tileY, boolean includeAdjacent) {
        int expectedTileAndObjectsHash = map.getInt("tileAndObjectsHash");
        int expectedAdjacentTileAndObjectsHash = map.getInt("adjacentTileAndObjectsHash");
        boolean result = this.checkExpectedTileAndObjectsHash(client, tileX, tileY, expectedTileAndObjectsHash);
        result = this.checkExpectedAdjacentTileAndObjectsHash(client, tileX, tileY, expectedAdjacentTileAndObjectsHash) || result;
        return result;
    }

    public <T> T[] getRelative(int x, int y, Point[] points, BiFunction<Integer, Integer, T> provider, IntFunction<T[]> arrayProvider) {
        T[] out = arrayProvider.apply(points.length);
        for (int i = 0; i < points.length; ++i) {
            out[i] = provider.apply(x + points[i].x, y + points[i].y);
        }
        return out;
    }

    public boolean getRelativeAnd(int x, int y, Point[] points, BiPredicate<Integer, Integer> predicate) {
        for (Point point : points) {
            if (predicate.test(x + point.x, y + point.y)) continue;
            return false;
        }
        return true;
    }

    public GameTile[] getAdjacentUnderLiquidTiles(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getUnderLiquidTile, GameTile[]::new);
    }

    public Integer[] getAdjacentTilesInt(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getTileID, Integer[]::new);
    }

    public GameTile[] getAdjacentTiles(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getTile, GameTile[]::new);
    }

    public LevelTile[] getAdjacentLevelTiles(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getLevelTile, LevelTile[]::new);
    }

    public GameObject[] getAdjacentObjects(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getObject, GameObject[]::new);
    }

    public LevelObject[] getAdjacentLevelObjects(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getLevelObject, LevelObject[]::new);
    }

    public Integer[] getAdjacentObjectsInt(int x, int y) {
        return this.getRelative(x, y, adjacentGetters, this::getObjectID, Integer[]::new);
    }

    public boolean isShore(int tileX, int tileY) {
        return this.liquidManager.isShore(tileX, tileY);
    }

    public boolean isProtected(int tileX, int tileY) {
        return this.isProtected || this.regionManager.isTileProtected(tileX, tileY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long grassWeave(int tileX, int tileY) {
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            return this.grassWeave.getOrDefault(GameMath.getUniqueLongKey(tileX, tileY), 0L) - this.getLocalTime();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long grassWeaveStart(int tileX, int tileY) {
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            return this.grassWeave.getOrDefault(GameMath.getUniqueLongKey(tileX, tileY), 0L);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isGrassWeaving(int tileX, int tileY) {
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            return this.grassWeave.containsKey(GameMath.getUniqueLongKey(tileX, tileY));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void makeGrassWeave(int tileX, int tileY, int weaveTime, boolean forceStart) {
        if (this.isServer()) {
            return;
        }
        long time = this.getLocalTime() + (long)weaveTime;
        long i = GameMath.getUniqueLongKey(tileX, tileY);
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            if (forceStart || !this.grassWeave.containsKey(i)) {
                this.grassWeave.put(i, time);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceGrassWeave(int tileX, int tileY, int weaveTime) {
        if (this.isServer()) {
            return;
        }
        long time = this.getLocalTime() + (long)weaveTime;
        long i = GameMath.getUniqueLongKey(tileX, tileY);
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            this.grassWeave.put(i, time);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickGrassWeave() {
        long time = this.getLocalTime();
        ArrayList<Long> removes = new ArrayList<Long>();
        this.grassWeave.forEach((key, weave) -> {
            if (weave - time < 0L) {
                removes.add((Long)key);
            }
        });
        HashMap<Long, Long> hashMap = this.grassWeave;
        synchronized (hashMap) {
            removes.forEach(this.grassWeave::remove);
        }
    }

    public ArrayList<LevelObjectHit> getCollisions(Shape shape, CollisionFilter filter) {
        ArrayList<LevelObjectHit> hits = new ArrayList<LevelObjectHit>();
        if (filter == null) {
            return hits;
        }
        this.streamShapeBounds(shape).forEach(lo -> filter.addCollisions(hits, shape, (TilePosition)lo));
        if (this.tileWidth > 0 || this.tileHeight > 0) {
            Rectangle shapeBounds = shape.getBounds();
            Rectangle edgeBounds = new Rectangle(GameMath.getLevelCoordinate(this.tileWidth), GameMath.getLevelCoordinate(this.tileHeight));
            if (this.tileWidth > 0 && (shapeBounds.x < 0 || shapeBounds.x + shapeBounds.width > edgeBounds.width)) {
                hits.add(new LevelObjectHit(edgeBounds, this));
            } else if (this.tileHeight > 0 && (shapeBounds.y < 0 || shapeBounds.y + shapeBounds.height > edgeBounds.height)) {
                hits.add(new LevelObjectHit(edgeBounds, this));
            }
        }
        return hits;
    }

    public boolean collides(Shape shape, CollisionFilter filter) {
        if (filter == null) {
            return false;
        }
        return this.collides(shape, (TilePosition tp) -> filter.check(shape, (TilePosition)tp));
    }

    public boolean collides(Line2D line, CollisionFilter filter) {
        if (filter == null) {
            return false;
        }
        double dist = line.getP1().distance(line.getP2());
        RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this, line.getX1(), line.getY1(), line.getX2() - line.getX1(), line.getY2() - line.getY1(), dist, 0, filter);
        return rays.totalDist < dist && dist > 0.0;
    }

    public boolean collides(Line2D line, float width, float resolution, CollisionFilter filter) {
        if (filter == null) {
            return false;
        }
        if (this.collides(line, filter)) {
            return true;
        }
        double dist = line.getP1().distance(line.getP2());
        Point2D.Double dir = GameMath.normalize(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        boolean checkLast = false;
        float halfWidth = width / 2.0f;
        for (float i = resolution; i <= halfWidth; i += resolution) {
            Point2D.Double p1 = GameMath.getPerpendicularPoint(line.getP1(), i, dir);
            RayLinkedList<LevelObjectHit> rays1 = GameUtils.castRay(this, p1.x, p1.y, dir.x, dir.y, dist, 0, filter);
            if (rays1.totalDist < dist && dist > 0.0) {
                return true;
            }
            Point2D.Double p2 = GameMath.getPerpendicularPoint(line.getP1(), -i, dir);
            RayLinkedList<LevelObjectHit> rays2 = GameUtils.castRay(this, p2.x, p2.y, dir.x, dir.y, dist, 0, filter);
            if (rays2.totalDist < dist && dist > 0.0) {
                return true;
            }
            if (i != halfWidth) continue;
            checkLast = true;
        }
        if (checkLast) {
            Point2D.Double p1 = GameMath.getPerpendicularPoint(line.getP1(), halfWidth, dir);
            RayLinkedList<LevelObjectHit> rays1 = GameUtils.castRay(this, p1.x, p1.y, dir.x, dir.y, dist, 0, filter);
            if (rays1.totalDist < dist && dist > 0.0) {
                return true;
            }
            Point2D.Double p2 = GameMath.getPerpendicularPoint(line.getP1(), -halfWidth, dir);
            RayLinkedList<LevelObjectHit> rays2 = GameUtils.castRay(this, p2.x, p2.y, dir.x, dir.y, dist, 0, filter);
            return rays2.totalDist < dist && dist > 0.0;
        }
        return false;
    }

    public boolean collides(Shape shape, Function<TilePosition, Boolean> check) {
        if (this.streamShapeBounds(shape).anyMatch(check::apply)) {
            return true;
        }
        if (this.tileWidth > 0 || this.tileHeight > 0) {
            Rectangle shapeBounds = shape.getBounds();
            if (this.tileWidth > 0 && (shapeBounds.x < 0 || shapeBounds.x + shapeBounds.width > GameMath.getLevelCoordinate(this.tileWidth))) {
                return true;
            }
            return this.tileHeight > 0 && (shapeBounds.y < 0 || shapeBounds.y + shapeBounds.height > GameMath.getLevelCoordinate(this.tileHeight));
        }
        return false;
    }

    protected Stream<TilePosition> streamShapeBounds(Shape shape) {
        return new LevelTilesSpliterator(this, shape, 0).stream();
    }

    public IntersectionPoint<LevelObjectHit> getCollisionPoint(List<LevelObjectHit> col, Line2D l, boolean checkInsideRect) {
        return CollisionPoint.getClosestCollision(col, l, checkInsideRect);
    }

    public boolean isSolidTile(int x, int y) {
        return this.getObject(x, y).isSolid(this, x, y);
    }

    public boolean clientsCollides(Rectangle collision) {
        return GameUtils.streamNetworkClients(this).filter(c -> c.playerMob != null && c.hasSpawned() && !c.playerMob.isFlying()).anyMatch(c -> collision.intersects(c.playerMob.getCollision()));
    }

    public boolean entityCollides(Rectangle collision, boolean checkClients) {
        boolean foundCollision = false;
        if (checkClients) {
            foundCollision = this.clientsCollides(collision);
        }
        foundCollision = foundCollision || this.entityManager.mobs.streamInRegionsShape(collision, 1).filter(m -> !m.removed() && m.canLevelInteract() && !m.isFlying()).anyMatch(m -> collision.intersects(m.getCollision()));
        return foundCollision;
    }

    public GameTile getUnderLiquidTile(int tileX, int tileY) {
        return this.getBiome(tileX, tileY).getUnderLiquidTile(this, tileX, tileY);
    }

    public boolean isLiquidTile(int x, int y) {
        return this.getTile((int)x, (int)y).isLiquid;
    }

    public boolean inLiquid(int levelX, int levelY) {
        int tileY;
        int tileX = GameMath.getTileCoordinate(levelX);
        return this.getTile(tileX, tileY = GameMath.getTileCoordinate(levelY)).inLiquid(this, tileX, tileY, levelX, levelY) && !this.getObject(tileX, tileY).overridesInLiquid(this, tileX, tileY, levelX, levelY);
    }

    public boolean inLiquid(Point him) {
        return this.inLiquid(him.x, him.y);
    }

    public float getLiquidSaltWaterSinkRate() {
        return 1.0f;
    }

    public float getLiquidFreshWaterSinkRate() {
        return 3.0f;
    }

    public float getLiquidMobSinkRate() {
        return 2.0f;
    }

    public void sendObjectChangePacket(Server server, int layerID, int x, int y, int object, int objectRotation) {
        this.setObject(x, y, object, objectRotation);
        if (server != null) {
            server.network.sendToClientsWithTile(new PacketChangeObject(this, layerID, x, y, object, objectRotation), this, x, y);
        }
    }

    public void sendObjectChangePacket(Server server, int x, int y, int object, int objectRotation) {
        this.sendObjectChangePacket(server, 0, x, y, object, objectRotation);
    }

    public void sendObjectChangePacket(Server server, int x, int y, int object) {
        this.sendObjectChangePacket(server, x, y, object, this.getObjectRotation(x, y));
    }

    public void sendTileChangePacket(Server server, int x, int y, int tile) {
        this.setTile(x, y, tile);
        if (server != null) {
            server.network.sendToClientsWithTile(new PacketChangeTile(this, x, y, tile), this, x, y);
        }
    }

    public void sendObjectUpdatePacket(int layerID, int x, int y) {
        if (this.isServer()) {
            this.getServer().network.sendToClientsWithTile(new PacketChangeObject(this, layerID, x, y, this.getObjectID(x, y), this.getObjectRotation(x, y)), this, x, y);
        }
    }

    public void sendObjectUpdatePacket(int x, int y) {
        this.sendObjectUpdatePacket(0, x, y);
    }

    public void sendTileUpdatePacket(int x, int y) {
        if (this.isServer()) {
            this.getServer().network.sendToClientsWithTile(new PacketChangeTile(this, x, y, this.getTileID(x, y)), this, x, y);
        }
    }

    public void sendWireChangePacket(Server server, int x, int y, byte wireData) {
        this.wireManager.setWireData(x, y, wireData, true);
        server.network.sendToClientsWithTile(new PacketChangeWire(this, x, y, wireData), this, x, y);
    }

    public void sendWireUpdatePacket(int x, int y) {
        if (this.isServer()) {
            this.getServer().network.sendToClientsWithTile(new PacketChangeWire(this, x, y, this.wireManager.getWireData(x, y)), this, x, y);
        }
    }

    public LevelData getLevelData(String key) {
        return this.levelDataManager.getLevelData(key);
    }

    public void addLevelData(String key, LevelData data) {
        this.levelDataManager.addLevelData(key, data);
    }

    public boolean shouldLimitCameraWithinBounds(PlayerMob perspective) {
        return Settings.limitCameraToLevelBounds;
    }

    public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
        return this.streamAll(LevelBuffsEntityComponent.class).flatMap(LevelBuffsEntityComponent::getLevelModifiers);
    }

    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        return this.streamAll(MobBuffsEntityComponent.class).flatMap(e -> e.getLevelModifiers(mob));
    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.basicCrate;
    }

    public <R extends EntityComponent, C extends Class<? extends R>> Stream<R> streamAll(C componentClass) {
        return Stream.concat(this.entityManager.componentManager.streamAll(componentClass), Stream.concat(this.entityManager.objectEntityComponentManager.streamAll(componentClass), this.levelDataManager.componentManager.streamAll(componentClass)));
    }

    public <R extends EntityComponent, C extends Class<? extends R>> Stream<R> streamAllWorld(C componentClass) {
        WorldEntity worldEntity = this.getWorldEntity();
        if (worldEntity != null) {
            return worldEntity.dataComponentManager.streamAll(componentClass);
        }
        return Stream.empty();
    }

    public void onMobSpawned(Mob mob) {
        this.streamAll(MobSpawnedListenerEntityComponent.class).forEach(e -> e.onLevelMobSpawned(mob));
    }

    public void onMobDied(Mob mob, Attacker attacker, HashSet<Attacker> attackers) {
        this.streamAll(MobDeathListenerEntityComponent.class).forEach(e -> e.onLevelMobDied(mob, attacker, attackers));
    }

    public LootTable getExtraMobDrops(Mob mob) {
        return this.getBiome(mob.getTileX(), mob.getTileY()).getExtraMobDrops(mob);
    }

    public LootTable getExtraPrivateMobDrops(Mob mob, ServerClient client) {
        return this.getBiome(mob.getTileX(), mob.getTileY()).getExtraPrivateMobDrops(mob, client);
    }

    public void onTilePlaced(GameTile tile, int tileX, int tileY, ServerClient client) {
        this.streamAll(TilePlacedListenerEntityComponent.class).forEach(e -> e.onTilePlaced(tile, tileX, tileY, client));
    }

    public void onObjectPlaced(GameObject object, int objectLayerID, int tileX, int tileY, ServerClient client) {
        this.streamAll(ObjectPlacedListenerEntityComponent.class).forEach(e -> e.onObjectPlaced(object, objectLayerID, tileX, tileY, client));
    }

    public void onTileDamaged(GameTile tile, int tileX, int tileY, Attacker attacker, ServerClient client, TileDamageResult result) {
        this.streamAll(TileDamagedListenerEntityComponent.class).forEach(e -> e.onTileDamaged(tile, tileX, tileY, client, result));
    }

    public void onObjectDamaged(GameObject object, int objectLayerID, int tileX, int tileY, Attacker attacker, ServerClient client, ObjectDamageResult result) {
        this.streamAll(ObjectDamagedListenerEntityComponent.class).forEach(e -> e.onObjectDamaged(object, objectLayerID, tileX, tileY, client, result));
    }

    public void onTileDestroyed(GameTile tile, int tileX, int tileY, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        this.streamAll(TileDestroyedListenerEntityComponent.class).forEach(e -> e.onTileDestroyed(tile, tileX, tileY, client, itemsDropped));
    }

    public void onTileLootTableDropped(TileLootTableDropsEvent event) {
        this.streamAll(TileLootTableDroppedListenerEntityComponent.class).forEach(listener -> listener.onTileLootTableDropped(event));
        GameEvents.triggerEvent(event);
    }

    public void onObjectDestroyed(GameObject object, int layerID, int tileX, int tileY, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        this.streamAll(ObjectDestroyedListenerEntityComponent.class).forEach(e -> e.onObjectDestroyed(object, layerID, tileX, tileY, client, itemsDropped));
    }

    public void onObjectLootTableDropped(ObjectLootTableDropsEvent event) {
        this.streamAll(ObjectLootTableDroppedListenerEntityComponent.class).forEach(listener -> listener.onObjectLootTableDropped(event));
        GameEvents.triggerEvent(event);
    }

    public int getAppearedObjectDamage(GameObject object, int layerID, int tileX, int tileY) {
        return 0;
    }

    public GameMessage preventsLadderPlacement(int tileX, int tileY) {
        this.regionManager.ensureTilesAreLoaded(tileX - 1, tileY - 1, tileX + 1, tileY + 1);
        if (this.isProtected(tileX, tileY)) {
            return new LocalMessage("misc", "blockingexit");
        }
        GameMessage tileError = this.getTile(tileX, tileY).preventsLadderPlacement(this, tileX, tileY);
        if (tileError != null) {
            return tileError;
        }
        return this.getObject(tileX, tileY).preventsLadderPlacement(this, tileX, tileY);
    }

    public boolean isSamePlace(Level other) {
        return this.identifier.equals(other.identifier);
    }

    public boolean isIslandPosition() {
        return this.identifier.isIslandPosition();
    }

    public int getIslandX() {
        return this.identifier.getIslandX();
    }

    public int getIslandY() {
        return this.identifier.getIslandY();
    }

    public int getIslandDimension() {
        return this.identifier.getIslandDimension();
    }

    public LevelIdentifier getIdentifier() {
        return this.identifier;
    }

    public int getIdentifierHashCode() {
        return this.identifier.hashCode();
    }

    public void overwriteIdentifier(LevelIdentifier identifier) {
        this.identifier = identifier;
    }

    public void setFallbackLevel(Level level, int tileX, int tileY) {
        if (level.getIdentifier().equals(this.getIdentifier())) {
            return;
        }
        this.keepTrackOfReturnedItems = this.keepTrackOfReturnedItems || level.keepTrackOfReturnedItems;
        this.fallbackIdentifier = level.getIdentifier();
        this.fallbackTilePos = new Point(tileX, tileY);
    }

    public void makeClientLevel(Client client) {
        this.server = null;
        this.client = client;
    }

    public void makeServerLevel(Server server) {
        this.client = null;
        this.server = server;
        this.regionManager.makeServerLevel();
        this.lightManager.setting = Settings.LightSetting.White;
    }

    @Deprecated
    public boolean isClientLevel() {
        return this.isClient();
    }

    @Override
    public boolean isClient() {
        return this.client != null;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Deprecated
    public boolean isServerLevel() {
        return this.isServer();
    }

    @Override
    public boolean isServer() {
        return this.server != null;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    public void setWorldEntity(WorldEntity worldEntity) {
        this.worldEntity = worldEntity;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.worldEntity;
    }

    public TickManager tickManager() {
        if (this.getClient() != null) {
            return this.getClient().tickManager();
        }
        if (this.getServer() != null) {
            return this.getServer().tickManager();
        }
        return GlobalData.getCurrentGameLoop();
    }

    @Override
    public WorldSettings getWorldSettings() {
        if (this.getClient() != null) {
            return this.getClient().worldSettings;
        }
        if (this.getServer() != null) {
            return this.getServer().world.settings;
        }
        return defaultWorldSettings;
    }

    public GameMessage getLocationMessage(int tileX, int tileY) {
        return this.getBiome(tileX, tileY).getLocalization();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DisposableExecutorService executor() {
        Object object = this.executorLock;
        synchronized (object) {
            if (this.executor == null) {
                AtomicInteger counter = new AtomicInteger();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(0, EXECUTOR_POOL_SIZE, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(), r -> new Thread(null, r, "level-" + this.getHostString() + "-" + this.getIdentifier() + "-executor-" + counter.addAndGet(1)));
                executor.allowCoreThreadTimeOut(true);
                executor.setCorePoolSize(EXECUTOR_POOL_SIZE);
                this.executor = new DisposableExecutorService(executor);
            }
            return this.executor;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        this.isDisposed = true;
        if (this.isServer() && !this.shouldSave()) {
            this.regionManager.deleteLevelFiles();
        }
        this.regionManager.dispose();
        this.entityManager.dispose();
        this.lightManager.dispose();
        this.drawUtils.dispose();
        Object object = this.executorLock;
        synchronized (object) {
            if (this.executor != null) {
                this.executor.dispose();
            }
        }
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "{" + this.getHostString() + ", " + this.identifier.toString() + "}";
    }

    public void migrateToOldLevel(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier) {
    }
}

