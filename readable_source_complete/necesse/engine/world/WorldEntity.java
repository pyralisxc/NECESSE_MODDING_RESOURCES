/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import necesse.engine.DisposableExecutorService;
import necesse.engine.GameLog;
import necesse.engine.GameState;
import necesse.engine.GameVersion;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.packet.PacketQuestUpdate;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.packet.PacketWorldEvent;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.QuestSave;
import necesse.engine.save.levelData.WorldEventSave;
import necesse.engine.team.TeamManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.HashMapPointEntry;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashMap;
import necesse.engine.world.GameClock;
import necesse.engine.world.SpawnTileFinder;
import necesse.engine.world.World;
import necesse.engine.world.WorldSettings;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldData.WorldData;
import necesse.engine.world.worldEvent.WorldEvent;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPresetsRegion;
import necesse.entity.manager.EntityComponentManager;
import necesse.entity.mobs.Mob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.ElderHousePreset1;
import necesse.level.maps.presets.ElderHousePreset2;
import necesse.level.maps.presets.ElderHousePreset3;
import necesse.level.maps.presets.ElderHousePreset4;
import necesse.level.maps.presets.ElderHousePreset5;
import necesse.level.maps.presets.ElderHousePreset6;
import necesse.level.maps.presets.ElderHousePreset7;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.regionSystem.Region;

public class WorldEntity
implements GameClock,
GameState {
    public static float sleepingModifier = 20.0f;
    public final Object lock = new Object();
    private long uniqueID;
    private long worldTime;
    private float worldTimeMod;
    private long time;
    private long localTime;
    private float timeMod;
    private float timeBuffer;
    private float worldTimeBuffer;
    private boolean lastPreventSleep;
    private boolean preventSleep;
    private boolean lastIsSleeping;
    private boolean isSleeping;
    private float addedSleepingTime;
    private long gameTicks;
    private long frameTicks;
    public final PlayerStats worldStats = new PlayerStats(false, EmptyStats.Mode.READ_AND_WRITE);
    public final QuestManager quests;
    public final TeamManager teams;
    public String loadedGameVersion = "1.0.1";
    public boolean shouldSaveLoadedGameVersion = false;
    public String worldSeed = ServerCreationSettings.getNewRandomSpawnSeed();
    public LevelIdentifier spawnLevelIdentifier;
    public Point defaultSpawnTile;
    public Point spawnTile;
    protected boolean hasFoundSpawn = false;
    private DisposableExecutorService executor;
    public static int EXECUTOR_POOL_SIZE = 5;
    private final Object executorLock = new Object();
    protected BiomeGeneratorStack generatorStack;
    protected PointHashMap<WorldPresetsRegion> worldPresetsCache = new PointHashMap();
    public boolean keepPresetGeneratedRegionsLoaded = false;
    public ArrayList<ModSaveInfo> lastMods = null;
    private final ArrayList<WorldEvent> events = new ArrayList();
    private final HashMap<String, WorldData> data = new HashMap();
    public final EntityComponentManager<String> dataComponentManager = new EntityComponentManager();
    public Client client;
    public World serverWorld;
    private float[] dayMods;
    private int[] dayShares;
    private int dayTotal;
    public static int DEFAULT_DAY_TOTAL = 960;
    public static float[] DEFAULT_DAY_MODS = new float[]{0.4f, 0.1f, 0.4f, 0.1f};

    private WorldEntity(Server server) {
        this.quests = new QuestManager(server);
        this.teams = new TeamManager(server);
        this.worldTime = 0L;
        this.worldTimeMod = 1.0f;
        this.time = 0L;
        this.localTime = 0L;
        this.timeMod = 1.0f;
        this.resetUniqueID();
        int halfWorldPresetTileSize = 512;
        this.defaultSpawnTile = this.spawnTile = new Point(halfWorldPresetTileSize, halfWorldPresetTileSize);
        this.calculateAmbientLightValues();
    }

    private WorldEntity(World serverWorld, String worldSeed) {
        this(serverWorld.server);
        this.serverWorld = serverWorld;
        this.worldSeed = worldSeed;
        this.spawnLevelIdentifier = LevelIdentifier.SURFACE_IDENTIFIER;
        int halfWorldPresetTileSize = 512;
        this.defaultSpawnTile = this.spawnTile = new Point(halfWorldPresetTileSize, halfWorldPresetTileSize);
        this.calculateAmbientLightValues();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initServer(boolean guideSpawn) {
        Point spawnTile = this.spawnTile;
        try {
            Level spawnLevel = this.serverWorld.getLevel(this.spawnLevelIdentifier);
            if (spawnLevel != null) {
                GameMessageBuilder builder = new GameMessageBuilder();
                builder.append("loading", "findingspawn");
                builder.append("\n");
                builder.append("loading", "takeamoment");
                this.serverWorld.server.setStartingMessage(builder, false);
                spawnTile = SpawnTileFinder.findSpawnTile(this);
                this.hasFoundSpawn = true;
                this.defaultSpawnTile = spawnTile;
                this.spawnTile = spawnTile;
                this.removePresetsNearbySpawn();
                int generateRegionsWithinTileRange = 64;
                int startRegionX = GameMath.getRegionCoordByTile(spawnTile.x - generateRegionsWithinTileRange);
                int startRegionY = GameMath.getRegionCoordByTile(spawnTile.y - generateRegionsWithinTileRange);
                int endRegionX = GameMath.getRegionCoordByTile(spawnTile.x + generateRegionsWithinTileRange);
                int endRegionY = GameMath.getRegionCoordByTile(spawnTile.y + generateRegionsWithinTileRange);
                for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
                    for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                        spawnLevel.regionManager.ensureRegionIsLoaded(regionX, regionY);
                    }
                }
                spawnLevel.getObject(spawnTile.x, spawnTile.y).getMultiTile(spawnLevel, ObjectLayerRegistry.BASE_LAYER, spawnTile.x, spawnTile.y).streamObjects(spawnTile.x, spawnTile.y).filter(o -> ((GameObject)o.value).getID() == spawnLevel.getObjectID(o.tileX, o.tileY)).forEach(x -> spawnLevel.setObject(x.tileX, x.tileY, 0));
                GameRandom random = new GameRandom(spawnLevel.getSeed());
                if (guideSpawn) {
                    Preset preset = random.getOneOf(new ElderHousePreset1(random), new ElderHousePreset2(random), new ElderHousePreset3(random), new ElderHousePreset4(random), new ElderHousePreset5(random), new ElderHousePreset6(random), new ElderHousePreset7(random));
                    if (random.nextBoolean()) {
                        preset = preset.tryMirrorX();
                    }
                    ArrayList<Point> spawnPoints = new ArrayList<Point>();
                    Rectangle spawnRec = new Rectangle(-4, -4, 8, 8);
                    for (int x2 = -preset.width - 4; x2 < preset.width / 2 + 4; ++x2) {
                        for (int y = -preset.height - 4; y < preset.height / 2 + 4; ++y) {
                            Rectangle presetRec = new Rectangle(x2, y, preset.width, preset.height);
                            if (spawnRec.intersects(presetRec)) continue;
                            spawnPoints.add(new Point(x2, y));
                        }
                    }
                    Point spawnPoint = (Point)spawnPoints.get(random.nextInt(spawnPoints.size()));
                    int presetX = spawnTile.x + spawnPoint.x;
                    int presetY = spawnTile.y + spawnPoint.y;
                    PresetUtils.clearMobsInPreset(preset, spawnLevel, presetX, presetY);
                    preset.applyToLevel(spawnLevel, presetX, presetY);
                } else {
                    Mob elder = MobRegistry.getMob("elderhuman", spawnLevel);
                    ArrayList<Point> spawnPoints = new ArrayList<Point>();
                    int area = 8;
                    for (int x3 = spawnTile.x - area; x3 < spawnTile.x + area; ++x3) {
                        for (int y = spawnTile.y - area; y < spawnTile.y + area; ++y) {
                            int elderX = x3 * 32 + 16;
                            int elderY = y * 32 + 16;
                            if (elder.collidesWith(spawnLevel, elderX, elderY)) continue;
                            spawnPoints.add(new Point(elderX, elderY));
                        }
                    }
                    Point elderSpawn = new Point(spawnTile.x * 32 + 16, spawnTile.y * 32 + 16);
                    if (spawnPoints.size() > 0) {
                        elderSpawn = (Point)spawnPoints.get(random.nextInt(spawnPoints.size()));
                    }
                    spawnLevel.entityManager.addMob(elder, elderSpawn.x, elderSpawn.y);
                }
            }
        }
        finally {
            this.spawnTile = spawnTile;
            this.defaultSpawnTile = spawnTile;
            this.hasFoundSpawn = true;
        }
    }

    public void removePresetsNearbySpawn() {
        int spawnRegionX = GameMath.getRegionCoordByTile(this.defaultSpawnTile.x);
        int spawnRegionY = GameMath.getRegionCoordByTile(this.defaultSpawnTile.y);
        int startRegionX = spawnRegionX - SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE;
        int startRegionY = spawnRegionY - SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE;
        int endRegionX = spawnRegionX + SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE;
        int endRegionY = spawnRegionY + SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE;
        int startPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionX);
        int startPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionY);
        int endPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionX);
        int endPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionY);
        for (int presetRegionX = startPresetRegionX; presetRegionX <= endPresetRegionX; ++presetRegionX) {
            for (int presetRegionY = startPresetRegionY; presetRegionY <= endPresetRegionY; ++presetRegionY) {
                WorldPresetsRegion cache = this.worldPresetsCache.get(presetRegionX, presetRegionY);
                if (cache == null || !cache.isLevelRegionLoadingOrLoaded(LevelIdentifier.SURFACE_IDENTIFIER)) continue;
                this.removePresetsNearbySpawn(cache.getLevelRegions(LevelIdentifier.SURFACE_IDENTIFIER, 0));
            }
        }
    }

    public void removePresetsNearbySpawn(LevelPresetsRegion presetsRegion) {
        if (!this.hasFoundSpawn) {
            return;
        }
        int spawnRegionX = GameMath.getRegionCoordByTile(this.defaultSpawnTile.x);
        int spawnRegionY = GameMath.getRegionCoordByTile(this.defaultSpawnTile.y);
        presetsRegion.markPresetsToNotGenerate(preset -> {
            int removeRange = Math.min(preset.getRemoveIfWithinSpawnRegionRange(), SpawnTileFinder.CLEAR_SPAWN_REGION_RANGE);
            if (removeRange < 0) {
                return false;
            }
            for (Rectangle tileRectangle : preset.getOccupiedTileRectangles()) {
                if (!tileRectangle.contains(this.defaultSpawnTile)) continue;
                return true;
            }
            for (Point occupiedRegion : preset.occupiedRegions) {
                if (!(GameMath.squareDistance(occupiedRegion.x, occupiedRegion.y, spawnRegionX, spawnRegionY) <= (float)removeRange)) continue;
                return true;
            }
            return false;
        });
    }

    private WorldEntity(Client client) {
        this((Server)null);
        this.client = client;
        this.calculateAmbientLightValues();
    }

    public SaveData getSave() {
        SaveData save = new SaveData("WORLD");
        save.addLong("uniqueID", this.uniqueID);
        if (this.shouldSaveLoadedGameVersion && this.loadedGameVersion != null) {
            save.addSafeString("gameVersion", this.loadedGameVersion);
        } else {
            save.addSafeString("gameVersion", "1.0.1");
        }
        save.addSafeString("worldSeed", this.worldSeed);
        save.addLong("worldTime", this.worldTime);
        save.addFloat("worldTimeMod", this.worldTimeMod);
        save.addLong("time", this.time);
        save.addFloat("timeMod", this.timeMod);
        save.addUnsafeString("spawnLevel", this.spawnLevelIdentifier.stringID);
        save.addPoint("defaultSpawnTile", this.defaultSpawnTile);
        save.addPoint("spawnTile", this.spawnTile);
        SaveData modsData = new SaveData("MODS");
        for (LoadedMod loadedMod : ModLoader.getEnabledMods()) {
            SaveData data = loadedMod.getModSaveInfo().getSaveData();
            modsData.addSaveData(data);
        }
        save.addSaveData(modsData);
        SaveData events = new SaveData("EVENTS");
        for (WorldEvent event : this.events) {
            if (!event.shouldSave) continue;
            events.addSaveData(WorldEventSave.getSave(event));
        }
        save.addSaveData(events);
        SaveData saveData = new SaveData("WORLDDATA");
        HashMap<String, WorldData> worldData = this.getWorldData();
        for (String key : worldData.keySet()) {
            SaveData dataSave = new SaveData(key);
            worldData.get(key).addSaveData(dataSave);
            saveData.addSaveData(dataSave);
        }
        save.addSaveData(saveData);
        SaveData teamsData = new SaveData("TEAMS");
        this.teams.addSaveData(teamsData);
        save.addSaveData(teamsData);
        SaveData questsData = new SaveData("QUESTS");
        for (Quest quest : this.quests.getQuests()) {
            if (quest.isRemoved()) continue;
            questsData.addSaveData(QuestSave.getSave(quest));
        }
        save.addSaveData(questsData);
        SaveData stats = new SaveData("STATS");
        this.worldStats.addSaveData(stats);
        save.addSaveData(stats);
        return save;
    }

    public void applyLoadData(LoadData save, boolean isSimple) {
        LoadData statsData;
        this.uniqueID = save.getLong("uniqueID", this.uniqueID, false);
        this.loadedGameVersion = save.getSafeString("gameVersion", null, false);
        this.worldSeed = save.getSafeString("worldSeed", null, false);
        if (this.worldSeed == null) {
            this.worldSeed = ServerCreationSettings.getNewRandomSpawnSeed(new GameRandom(this.uniqueID));
        }
        this.worldTime = save.getLong("worldTime", this.worldTime);
        this.worldTimeMod = save.getFloat("worldTimeMod", this.worldTimeMod);
        this.time = save.getLong("time", this.time);
        this.timeMod = save.getFloat("timeMod", this.timeMod);
        try {
            this.spawnLevelIdentifier = new LevelIdentifier(save.getUnsafeString("spawnLevel", null, false));
        }
        catch (InvalidLevelIdentifierException e) {
            Point spawnIsland = save.getPoint("spawnIsland", new Point(0, 0));
            this.spawnLevelIdentifier = new LevelIdentifier(spawnIsland.x, spawnIsland.y, 0);
        }
        this.defaultSpawnTile = save.getPoint("defaultSpawnTile", null, false);
        if (save.getFirstLoadDataByName("spawnPoint") != null) {
            Point p = save.getPoint("spawnPoint", new Point(this.spawnTile.x * 32 + 16, this.spawnTile.y * 32 + 16));
            this.spawnTile = new Point(GameMath.getTileCoordinate(p.x), GameMath.getTileCoordinate(p.y));
        } else {
            this.spawnTile = save.getPoint("spawnTile", this.spawnTile);
        }
        if (this.defaultSpawnTile == null) {
            this.defaultSpawnTile = this.spawnTile;
        }
        this.hasFoundSpawn = true;
        LoadData modsData = save.getFirstLoadDataByName("MODS");
        if (modsData != null) {
            this.lastMods = new ArrayList();
            for (LoadData modData : modsData.getLoadData()) {
                try {
                    this.lastMods.add(ModSaveInfo.fromSave(modData));
                }
                catch (LoadDataException e) {
                    GameLog.warn.println("Could not load mod info: " + e.getMessage());
                }
            }
        }
        if ((statsData = save.getFirstLoadDataByName("STATS")) != null) {
            this.worldStats.applyLoadData(statsData);
        } else if (!isSimple) {
            GameLog.warn.println("Could not load world stats");
        }
        this.calculateAmbientLightValues();
        if (!isSimple) {
            try {
                List<LoadData> events = save.getFirstLoadDataByName("EVENTS").getLoadData();
                for (LoadData eventSave : events) {
                    try {
                        WorldEvent event = WorldEventSave.loadSave(eventSave);
                        if (event == null) continue;
                        this.addWorldEvent(event);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                GameLog.warn.println("Could not complete loading of world events");
            }
            try {
                List<LoadData> data = save.getFirstLoadDataByName("WORLDDATA").getLoadData();
                for (LoadData saveData : data) {
                    try {
                        WorldData loadedData = WorldDataRegistry.loadWorldData(this, saveData);
                        if (loadedData == null) continue;
                        this.addWorldData(saveData.getName(), loadedData);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                GameLog.warn.println("Could not complete loading of world data");
            }
            try {
                LoadData teamsSave = save.getFirstLoadDataByName("TEAMS");
                if (teamsSave != null) {
                    this.teams.applySaveData(teamsSave);
                }
            }
            catch (Exception e) {
                GameLog.warn.println("Could not complete loading of teams");
            }
            try {
                List<LoadData> questsData = save.getFirstLoadDataByName("QUESTS").getLoadData();
                for (LoadData questSave : questsData) {
                    try {
                        Quest quest = QuestSave.loadSave(questSave);
                        if (quest == null) continue;
                        this.quests.addQuest(quest, false);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                GameLog.warn.println("Could not complete loading of quests");
            }
        }
    }

    public void applyWorldPacket(PacketWorldData packet) {
        this.worldTime = packet.worldTime;
        this.time = packet.time;
        this.isSleeping = packet.isSleeping;
    }

    private WorldSettings getWorldSettings() {
        if (this.client != null) {
            return this.client.worldSettings;
        }
        if (this.serverWorld != null) {
            return this.serverWorld.settings;
        }
        return null;
    }

    public boolean isEarlierThanOneWorld() {
        if (this.loadedGameVersion == null) {
            return true;
        }
        return !new GameVersion(this.loadedGameVersion).isLaterThan("0.33.1");
    }

    public long getUniqueID() {
        return this.uniqueID;
    }

    public int getWorldSeed() {
        return this.worldSeed.hashCode();
    }

    public GameRandom getNewWorldRandom() {
        return new GameRandom(this.getWorldSeed());
    }

    public void resetUniqueID() {
        this.uniqueID = GameRandom.globalRandom.nextLong();
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getLocalTime() {
        return this.localTime;
    }

    @Override
    public long getWorldTime() {
        return this.worldTime;
    }

    public long getGameTicks() {
        return this.gameTicks;
    }

    public long getFrameTicks() {
        return this.frameTicks;
    }

    public void clientFrameTick(TickManager tickManager) {
        ++this.frameTicks;
        this.tickTime(tickManager);
        this.tickWorldEventsMovement(tickManager);
    }

    public void clientTick() {
        this.lastPreventSleep = this.preventSleep;
        this.preventSleep = false;
        ++this.gameTicks;
        for (int i = 0; i < this.events.size(); ++i) {
            WorldEvent event = this.events.get(i);
            if (event.isOver()) {
                this.events.remove(i);
                --i;
                continue;
            }
            event.clientTick();
        }
        for (WorldData data : this.data.values()) {
            data.tick();
        }
        this.tickWorldRegions();
    }

    public void serverFrameTick(TickManager tickManager) {
        ++this.frameTicks;
        this.tickTime(tickManager);
        this.tickWorldEventsMovement(tickManager);
    }

    public void serverTick() {
        this.lastPreventSleep = this.preventSleep;
        this.preventSleep = false;
        boolean beforeSleeping = this.isSleeping();
        this.lastIsSleeping = this.isSleeping;
        this.isSleeping = false;
        ++this.gameTicks;
        for (int i = 0; i < this.events.size(); ++i) {
            WorldEvent event = this.events.get(i);
            if (event.isOver()) {
                this.events.remove(i);
                --i;
                continue;
            }
            event.serverTick();
        }
        for (WorldData data : this.data.values()) {
            data.tick();
        }
        this.tickWorldRegions();
        if (this.isServer()) {
            int simulateTime;
            if (this.quests.isDirty()) {
                LinkedList<Quest> removedQuests = new LinkedList<Quest>();
                for (Quest quest : this.quests.getQuests()) {
                    if (quest.isRemoved()) {
                        removedQuests.add(quest);
                        continue;
                    }
                    if (!quest.isDirty()) continue;
                    this.getServer().network.sendPacket((Packet)new PacketQuestUpdate(quest), c -> quest.isActiveFor(c.authentication));
                    quest.clean();
                }
                removedQuests.forEach(this.quests::removeQuest);
                this.quests.cleanAll();
            }
            if (this.addedSleepingTime >= (float)(simulateTime = (int)Math.max(5000.0f, sleepingModifier * 1000.0f))) {
                this.serverWorld.simulateWorldTime(simulateTime, true);
                this.addedSleepingTime -= (float)simulateTime;
            }
            if (this.isSleeping() != beforeSleeping) {
                this.getServer().network.sendToAllClients(new PacketChangeWorldTime(this.getServer()));
            }
        }
    }

    private void tickTime(TickManager tickManager) {
        this.worldTimeBuffer += tickManager.getFullDelta() * this.worldTimeMod;
        if (this.lastIsSleeping) {
            float add = tickManager.getFullDelta() * sleepingModifier;
            this.addedSleepingTime += add;
            this.worldTimeBuffer += add;
        }
        long worldTimeAdd = (long)this.worldTimeBuffer;
        this.worldTimeBuffer -= (float)worldTimeAdd;
        this.worldTime += worldTimeAdd;
        this.timeBuffer += tickManager.getFullDelta() * this.timeMod;
        long timeAdd = (long)this.timeBuffer;
        this.timeBuffer -= (float)timeAdd;
        this.time += timeAdd;
        this.localTime += timeAdd;
    }

    private void tickWorldEventsMovement(TickManager tickManager) {
        for (int i = 0; i < this.events.size(); ++i) {
            WorldEvent event = this.events.get(i);
            if (event.isOver()) {
                this.events.remove(i);
                --i;
                continue;
            }
            event.tickMovement(tickManager.getDelta());
        }
    }

    private synchronized void tickWorldRegions() {
        for (HashMapPointEntry<Point, WorldPresetsRegion> entry : this.worldPresetsCache.getEntries()) {
            entry.getValue().tickUnloadBuffer();
        }
    }

    public void addWorldTime(long milliSeconds) {
        this.worldTime += milliSeconds;
    }

    public void setWorldTime(long worldTime) {
        this.worldTime = worldTime;
    }

    public boolean isSleeping() {
        return this.isSleeping || this.lastIsSleeping;
    }

    public void keepSleeping() {
        if (!this.isSleeping) {
            this.isSleeping = true;
            if (this.isServer()) {
                this.getServer().network.sendToAllClients(new PacketChangeWorldTime(this.getServer()));
            }
        }
    }

    public void preventSleep() {
        this.preventSleep = true;
    }

    public boolean isSleepPrevented() {
        return this.lastPreventSleep || this.preventSleep;
    }

    public BiomeGeneratorStack getGeneratorStack() {
        if (this.generatorStack == null) {
            this.generatorStack = new BiomeGeneratorStack(this.getWorldSeed());
        }
        return this.generatorStack;
    }

    public WorldPresetsRegion getWorldPresets(int regionX, int regionY) {
        int presetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionX);
        int presetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionY);
        return this.getWorldPresetsFromPresetRegion(presetRegionX, presetRegionY);
    }

    private synchronized WorldPresetsRegion getWorldPresetsFromPresetRegion(int presetRegionX, int presetRegionY) {
        return this.worldPresetsCache.compute(presetRegionX, presetRegionY, (point, lastValue) -> {
            if (lastValue == null) {
                lastValue = new WorldPresetsRegion(this, presetRegionX, presetRegionY);
            }
            return lastValue;
        });
    }

    public synchronized int getLoadedLevelRegionsCount(LevelIdentifier identifier) {
        int count = 0;
        for (WorldPresetsRegion value : this.worldPresetsCache.values()) {
            count += value.getLoadedLevelRegionsCount(identifier);
        }
        return count;
    }

    public void refreshWorldPresetCache(LevelIdentifier identifier, int tileX, int tileY) {
        int regionLoadRange = ClientLevelLoading.REGION_UNLOAD_RANGE + 4;
        int regionX = GameMath.getRegionCoordByTile(tileX);
        int regionY = GameMath.getRegionCoordByTile(tileY);
        int startRegionX = regionX - regionLoadRange;
        int startRegionY = regionY - regionLoadRange;
        int endRegionX = regionX + regionLoadRange;
        int endRegionY = regionY + regionLoadRange;
        int startPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionX);
        int startPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionY);
        int endPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionX);
        int endPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionY);
        int presetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionX);
        int presetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(regionY);
        this.getWorldPresetsFromPresetRegion(presetRegionX, presetRegionY).refreshRegionUnloadBuffer(identifier);
        for (int currentPresetRegionX = startPresetRegionX; currentPresetRegionX <= endPresetRegionX; ++currentPresetRegionX) {
            for (int currentPresetRegionY = startPresetRegionY; currentPresetRegionY <= endPresetRegionY; ++currentPresetRegionY) {
                if (currentPresetRegionX == presetRegionX && currentPresetRegionY == presetRegionY) continue;
                this.getWorldPresetsFromPresetRegion(currentPresetRegionX, currentPresetRegionY).refreshRegionUnloadBuffer(identifier);
            }
        }
    }

    public LevelPresetsRegion.FoundPresetData findClosestWorldPreset(LevelIdentifier levelIdentifier, Predicate<LevelPresetsRegion.PlaceableWorldPreset> nonPlacedFilter, int centerTileX, int centerTileY, int maxTileRange, Predicate<LevelPresetsRegion.FoundPresetData> filter) {
        int centerRegionX = GameMath.getRegionCoordByTile(centerTileX);
        int centerRegionY = GameMath.getRegionCoordByTile(centerTileY);
        int regionRange = GameMath.getRegionCoordByTile(maxTileRange) + 1;
        int startRegionX = centerRegionX - regionRange;
        int startRegionY = centerRegionY - regionRange;
        int endRegionX = centerRegionX + regionRange;
        int endRegionY = centerRegionY + regionRange;
        int startPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionX);
        int startPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(startRegionY);
        int endPresetRegionX = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionX);
        int endPresetRegionY = WorldPresetsRegion.getWorldPresetsRegionFromLevelRegion(endRegionY);
        Comparator<LevelPresetsRegion.FoundPresetData> comparator = Comparator.comparingDouble(data -> GameMath.diagonalMoveDistance(data.getTileX(), data.getTileY(), centerTileX, centerTileY));
        LevelPresetsRegion.FoundPresetData found = null;
        for (int presetRegionX = startPresetRegionX; presetRegionX <= endPresetRegionX; ++presetRegionX) {
            for (int presetRegionY = startPresetRegionY; presetRegionY <= endPresetRegionY; ++presetRegionY) {
                WorldPresetsRegion worldPresets = this.getWorldPresetsFromPresetRegion(presetRegionX, presetRegionY);
                LevelPresetsRegion levelRegions = worldPresets.getLevelRegions(levelIdentifier, 0);
                levelRegions.unloadBuffer = 0;
                LevelPresetsRegion.FoundPresetData bestPreset = levelRegions.streamPresets(nonPlacedFilter).filter(filter).min(comparator).orElse(null);
                if (bestPreset == null || found != null && comparator.compare(bestPreset, found) >= 0) continue;
                found = bestPreset;
            }
        }
        return found;
    }

    public LevelPresetsRegion.FoundPresetData findClosestWorldPreset(Level level, int centerTileX, int centerTileY, int maxTileRange, Predicate<LevelPresetsRegion.FoundPresetData> filter) {
        return this.findClosestWorldPreset(level.getIdentifier(), preset -> {
            for (Point regionPos : preset.occupiedRegions) {
                if (!level.regionManager.isRegionGenerated(regionPos.x, regionPos.y)) continue;
                return false;
            }
            return true;
        }, centerTileX, centerTileY, maxTileRange, filter);
    }

    public synchronized void saveGeneratedPresets(LevelIdentifier identifier) {
        for (WorldPresetsRegion value : this.worldPresetsCache.values()) {
            value.saveGeneratedPresetsFile(identifier);
        }
    }

    public int startPresetGenerationInRegion(Region region, int customSeed) {
        return this.getWorldPresets(region.regionX, region.regionY).startGenerateRegion(region.manager.level.getIdentifier(), region, customSeed);
    }

    public void runPresetGenerationInRegion(int generationUniqueID, Region region, int customSeed) {
        this.getWorldPresets(region.regionX, region.regionY).runGenerateRegion(region.manager.level.getIdentifier(), generationUniqueID, region, customSeed);
    }

    public boolean isWithinWorldBorder(int islandX, int islandY) {
        LevelIdentifier spawnLevel;
        if (Settings.worldBorderSize >= 0 && (spawnLevel = this.spawnLevelIdentifier).isIslandPosition()) {
            return GameMath.squareDistance(islandX, islandY, spawnLevel.getIslandX(), spawnLevel.getIslandY()) <= (float)Settings.worldBorderSize;
        }
        return true;
    }

    public void applyChangeWorldTimePacket(PacketChangeWorldTime packet) {
        this.worldTime = packet.worldTime;
        this.isSleeping = packet.isSleeping;
    }

    public int getDay() {
        return (int)((double)this.worldTime / 1000.0 / (double)this.getDayTimeMax());
    }

    public float getDayTime() {
        return (float)((double)this.worldTime / 1000.0 % (double)this.getDayTimeMax());
    }

    public int getDayTimeInt() {
        return (int)this.getDayTime();
    }

    public float getDayTimePercent() {
        long dayTime = this.worldTime % ((long)this.getDayTimeMax() * 1000L);
        return (float)dayTime / (float)this.getDayTimeMax() / 1000.0f;
    }

    public float getDayTimeHourFloat() {
        return (this.getDayTimePercent() * 24.0f + this.getHourOffset()) % 24.0f;
    }

    public float hourToDayTime(float hours) {
        if ((hours -= this.getHourOffset()) < 0.0f) {
            hours = 24.0f + hours % 24.0f;
        }
        float percent = hours % 24.0f / 24.0f;
        return (float)this.getDayTimeMax() * percent;
    }

    protected float getHourOffset() {
        WorldSettings settings = this.getWorldSettings();
        float dayTime = 1.0f;
        float nightTime = 1.0f;
        if (settings != null) {
            dayTime = GameMath.limit(settings.dayTimeMod, 0.0f, 10.0f);
            nightTime = GameMath.limit(settings.nightTimeMod, 0.0f, 10.0f);
        }
        return 8.0f / ((dayTime + nightTime) / 2.0f);
    }

    private static float defaultHourToDayTime(float hours) {
        if ((hours -= 8.0f) < 0.0f) {
            hours = 24.0f + hours % 24.0f;
        }
        float percent = hours % 24.0f / 24.0f;
        return (float)DEFAULT_DAY_TOTAL * percent;
    }

    public int getDayTimeHour() {
        return (int)this.getDayTimeHourFloat();
    }

    public int getDayTimeMinute() {
        float hourFloat = this.getDayTimeHourFloat();
        return (int)((hourFloat - (float)((int)hourFloat)) * 60.0f);
    }

    public String getDayTimeReadable() {
        int hour = this.getDayTimeHour();
        int min = this.getDayTimeMinute();
        return (hour < 10 ? "0" + hour : Integer.valueOf(hour)) + ":" + (min < 10 ? "0" + min : Integer.valueOf(min));
    }

    public TimeOfDay getTimeOfDay() {
        int dayTimeInt = this.getDayTimeInt();
        if ((float)dayTimeInt < (float)this.dayShares[0] / (DEFAULT_DAY_MODS[0] * (float)DEFAULT_DAY_TOTAL / WorldEntity.defaultHourToDayTime(12.0f))) {
            return TimeOfDay.MORNING;
        }
        if ((float)dayTimeInt < (float)this.dayShares[0] / (DEFAULT_DAY_MODS[0] * (float)DEFAULT_DAY_TOTAL / WorldEntity.defaultHourToDayTime(14.0f))) {
            return TimeOfDay.NOON;
        }
        if ((float)dayTimeInt < (float)this.dayShares[1] / ((DEFAULT_DAY_MODS[0] + DEFAULT_DAY_MODS[1]) * (float)DEFAULT_DAY_TOTAL / WorldEntity.defaultHourToDayTime(18.0f))) {
            return TimeOfDay.AFTERNOON;
        }
        if ((float)dayTimeInt < (float)this.dayShares[1] / ((DEFAULT_DAY_MODS[0] + DEFAULT_DAY_MODS[1]) * (float)DEFAULT_DAY_TOTAL / WorldEntity.defaultHourToDayTime(19.25f))) {
            return TimeOfDay.EVENING;
        }
        if ((float)dayTimeInt < (float)this.dayShares[3] / ((float)DEFAULT_DAY_TOTAL / WorldEntity.defaultHourToDayTime(6.5f))) {
            return TimeOfDay.NIGHT;
        }
        return TimeOfDay.MORNING;
    }

    public boolean isNight() {
        return this.getTimeOfDay() == TimeOfDay.NIGHT;
    }

    public void calculateAmbientLightValues() {
        float dayTime = 1.0f;
        float nightTime = 1.0f;
        WorldSettings settings = this.getWorldSettings();
        if (settings != null) {
            dayTime = GameMath.limit(settings.dayTimeMod, 0.0f, 10.0f);
            nightTime = GameMath.limit(settings.nightTimeMod, 0.0f, 10.0f);
            dayTime = (float)((int)(dayTime * 10.0f)) / 10.0f;
            nightTime = (float)((int)(nightTime * 10.0f)) / 10.0f;
            if (dayTime == 0.0f && nightTime == 0.0f) {
                dayTime = 1.0f;
            }
        }
        this.dayMods = new float[]{DEFAULT_DAY_MODS[0] * dayTime, DEFAULT_DAY_MODS[1] * (dayTime + nightTime) / 2.0f, DEFAULT_DAY_MODS[2] * nightTime, DEFAULT_DAY_MODS[3] * (dayTime + nightTime) / 2.0f};
        float totalMod = this.dayMods[0] + this.dayMods[1] + this.dayMods[2] + this.dayMods[3];
        this.dayTotal = (int)((float)DEFAULT_DAY_TOTAL * totalMod);
        this.dayShares = new int[4];
        int currentTime = 0;
        float extra = 0.0f;
        for (int i = 0; i < this.dayShares.length; ++i) {
            if (i == this.dayShares.length - 1) {
                this.dayShares[i] = this.dayTotal;
                continue;
            }
            float section = (float)DEFAULT_DAY_TOTAL * this.dayMods[i] + extra;
            extra = section - (float)((int)section);
            this.dayShares[i] = currentTime += (int)section;
        }
    }

    public int getDayTimeMax() {
        return this.dayTotal;
    }

    public float getSunProgress() {
        float dayTime = this.getDayTime();
        float currentShare = 0.0f;
        int totalShares = this.dayTotal - this.getNightDuration();
        if (dayTime <= (float)this.dayShares[1]) {
            currentShare = (float)this.getDayToNightDuration() + dayTime;
        } else {
            if (dayTime <= (float)this.dayShares[2]) {
                return -1.0f;
            }
            if (dayTime <= (float)this.dayShares[3]) {
                currentShare = dayTime - (float)this.dayShares[2];
            }
        }
        return currentShare / (float)totalShares;
    }

    public float getMoonProgress() {
        float dayTime = this.getDayTime();
        float currentShare = 0.0f;
        int totalShares = this.dayTotal - this.getDayDuration();
        if (dayTime <= (float)this.dayShares[0]) {
            return -1.0f;
        }
        if (dayTime <= (float)this.dayShares[3]) {
            currentShare = dayTime - (float)this.dayShares[0];
        }
        return currentShare / (float)totalShares;
    }

    public float getMoonLightFloat() {
        float dayTime = this.getDayTime();
        float ambientLight = 0.0f;
        if (dayTime <= (float)this.dayShares[0]) {
            ambientLight = 0.0f;
        } else if (dayTime <= (float)this.dayShares[1]) {
            ambientLight = (dayTime - (float)this.dayShares[0]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[1]);
        } else if (dayTime <= (float)this.dayShares[2]) {
            ambientLight = 1.0f;
        } else if (dayTime <= (float)this.dayShares[3]) {
            ambientLight = Math.abs((dayTime - (float)this.dayShares[2]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[3]) - 1.0f);
        }
        return ambientLight;
    }

    public float getAmbientLightFloat() {
        float dayTime = this.getDayTime();
        float ambientLight = 0.0f;
        if (dayTime <= (float)this.dayShares[0]) {
            ambientLight = 1.0f;
        } else if (dayTime <= (float)this.dayShares[1]) {
            ambientLight = Math.abs((dayTime - (float)this.dayShares[0]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[1]) - 1.0f);
        } else if (dayTime <= (float)this.dayShares[2]) {
            ambientLight = 0.0f;
        } else if (dayTime <= (float)this.dayShares[3]) {
            ambientLight = (dayTime - (float)this.dayShares[2]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[3]);
        }
        return ambientLight;
    }

    public float getAmbientLight() {
        return this.getAmbientLightFloat() * 150.0f;
    }

    public int getDayDuration() {
        return this.dayShares[0];
    }

    public int getDayToNightDuration() {
        return this.dayShares[1] - this.dayShares[0];
    }

    public int getNightDuration() {
        return this.dayShares[2] - this.dayShares[1];
    }

    public int getNightToDayDuration() {
        return this.dayShares[3] - this.dayShares[2];
    }

    public void addWorldEvent(WorldEvent event) {
        this.addWorldEventHidden(event);
        if (this.isServer()) {
            this.getServer().network.sendToAllClients(new PacketWorldEvent(event));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addWorldEventHidden(WorldEvent event) {
        event.world = this;
        Object object = this.lock;
        synchronized (object) {
            this.events.add(event);
        }
        event.init();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addWorldEventDrawables(TickManager tickManager, GameCamera camera, LevelDrawUtils.DrawArea area, Level level, List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables) {
        ArrayList<WorldEvent> events;
        Iterator<WorldEvent> iterator = this.lock;
        synchronized (iterator) {
            events = new ArrayList<WorldEvent>(this.events);
        }
        for (WorldEvent event : events) {
            event.addDrawables(sortedDrawables, tileDrawables, topDrawables, area, level, tickManager, camera);
        }
    }

    @Override
    public boolean isClient() {
        return this.client != null;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public boolean isServer() {
        return this.serverWorld != null;
    }

    @Override
    public Server getServer() {
        return this.serverWorld.server;
    }

    public WorldData getWorldData(String key) {
        return this.data.get(key);
    }

    public void addWorldData(String key, WorldData worldData) {
        if (!key.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Key \"" + key + "\" contains illegal characters");
        }
        worldData.setWorldEntity(this);
        this.data.put(key, worldData);
        worldData.init();
        this.dataComponentManager.add(key, worldData);
    }

    public WorldData removeWorldData(String key) {
        WorldData remove = this.data.remove(key);
        if (remove != null) {
            this.dataComponentManager.remove(key, remove);
        }
        return remove;
    }

    public HashMap<String, WorldData> getWorldData() {
        return this.data;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DisposableExecutorService executor() {
        Object object = this.executorLock;
        synchronized (object) {
            if (this.executor == null) {
                AtomicInteger counter = new AtomicInteger();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(0, EXECUTOR_POOL_SIZE, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(), r -> new Thread(null, r, "world-" + this.getHostString() + "-" + this.getUniqueID() + "-executor-" + counter.addAndGet(1)));
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
        Object object = this.executorLock;
        synchronized (object) {
            if (this.executor != null) {
                this.executor.dispose();
                this.executor = null;
            }
        }
    }

    public static WorldEntity getClientWorldEntity(Client client) {
        return new WorldEntity(client);
    }

    public static WorldEntity getPlainWorldEntity(World serverWorld) {
        WorldEntity out = new WorldEntity(serverWorld.server);
        out.serverWorld = serverWorld;
        return out;
    }

    public static WorldEntity getServerWorldEntity(World serverWorld, String worldSeed) {
        return new WorldEntity(serverWorld, worldSeed);
    }

    public static WorldEntity getDebugWorldEntity() {
        return new WorldEntity((Server)null);
    }

    public static WorldEntity getDebugWorldEntity(WorldEntity copyFrom) {
        WorldEntity worldEntity = WorldEntity.getDebugWorldEntity();
        worldEntity.time = copyFrom.time;
        worldEntity.localTime = copyFrom.localTime;
        worldEntity.worldTime = copyFrom.worldTime;
        return worldEntity;
    }

    public static enum TimeOfDay {
        MORNING(new LocalMessage("ui", "timemorning")),
        NOON(new LocalMessage("ui", "timenoon")),
        AFTERNOON(new LocalMessage("ui", "timeafternoon")),
        EVENING(new LocalMessage("ui", "timeevening")),
        NIGHT(new LocalMessage("ui", "timenight"));

        public GameMessage displayName;

        private TimeOfDay(GameMessage displayName) {
            this.displayName = displayName;
        }
    }
}

