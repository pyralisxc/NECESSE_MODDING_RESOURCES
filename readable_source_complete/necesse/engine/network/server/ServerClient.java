/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.dlc.DLC;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.JournalEntry;
import necesse.engine.journal.listeners.ItemObtainedJournalChallengeListener;
import necesse.engine.journal.listeners.LevelChangedJournalChallengeListener;
import necesse.engine.journal.listeners.StatsCombinedJournalChallengeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketAddDeathLocation;
import necesse.engine.network.packet.PacketCharacterStatsUpdate;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketClientInstalledDLC;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketCloseContainer;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketJournalUpdated;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketNeedRequestSelf;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerDie;
import necesse.engine.network.packet.PacketPlayerLatency;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerLoadedRegions;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPrivateSync;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketPlayerSync;
import necesse.engine.network.packet.PacketRequestClientInstalledDLC;
import necesse.engine.network.packet.PacketRequestClientStats;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.packet.PacketSpawnPlayerReceipt;
import necesse.engine.network.packet.PacketTotalStatsUpdate;
import necesse.engine.network.packet.PacketUniqueFloatText;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.ItemDropperHandler;
import necesse.engine.network.server.Server;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.Quest;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.DPSTracker;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelDeathLocation;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.LevelIdentifierTilePos;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.TeleportResult;
import necesse.engine.util.WorldDeathLocation;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWorldPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.HumanLook;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.presets.PresetRedoData;
import necesse.level.maps.presets.PresetUndoData;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPosition;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class ServerClient
extends NetworkClient {
    public static float mobSpawnRate = TickManager.getTickDelta(0.6f);
    public static float mobSpawnRatePartyMemberModifier = 0.2f;
    public static float critterSpawnRate = TickManager.getTickDelta(0.7f);
    public static float settlerSpawnRate = TickManager.getTickDelta(1.0f);
    public static int MS_TO_AFK = 30000;
    private final long sessionID;
    public NetworkInfo networkInfo;
    private int characterUniqueID;
    private PermissionLevel permissionLevel;
    private LevelIdentifier levelIdentifier;
    public int latency;
    public int timeConnected;
    private long sessionTime;
    private long msTimeTicker;
    public long pvpSetCooldown;
    public int networkUpdateTimer;
    public int pingTimer;
    public long lastReceivedPacketTime;
    private long lastResetConnectionTime;
    private int pingKickBuffer;
    private ExpectedPing expectedPing;
    private final Object pingLock = new Object();
    public long respawnTime;
    private float nextMobSpawn;
    private float nextCritterSpawn;
    private float nextSettlerSpawn;
    public long lastActionTime;
    private boolean isAFK;
    private boolean hasRequestedSelf;
    private long lastRequestSelfPacketSentSystemTime;
    private long spawnedCheckTimer;
    private long lastSpawnPacketRequestSystemTime;
    private boolean needAppearance;
    private boolean submittedCharacter;
    private final LinkedList<WorldDeathLocation> deathLocations = new LinkedList();
    public HashMap<LevelIdentifier, PointHashSet> discoveredPresetTiles = new HashMap();
    private HashMapSet<LevelIdentifier, Long> loadedRegions;
    public LevelIdentifier spawnLevelIdentifier;
    public Point spawnTile;
    protected LevelIdentifier levelIdentifierFallback;
    protected Point tilePosFallback;
    public HashSet<Integer> teamInvites = new HashSet();
    public HashSet<Long> joinRequests = new HashSet();
    public HashMap<Integer, ServerClient> questInvites = new HashMap();
    private AchievementManager achievements;
    private PlayerStats totalStats;
    private PlayerStats characterStats;
    public PlayerStats newStats;
    private boolean shouldSendDirtyStats;
    private int lastDistanceRan;
    private int lastDistanceRidden;
    private final HashMap<Quest, Boolean> quests = new HashMap();
    public final AdventureParty adventureParty = new AdventureParty(this);
    public boolean hasNewJournalEntry;
    public DPSTracker trainingDummyDPSTracker = new DPSTracker();
    public ArrayList<MobWorldPosition> homePortals = new ArrayList();
    public ArrayList<PresetUndoData> presetUndoData = new ArrayList();
    public ArrayList<PresetRedoData> presetRedoData = new ArrayList();
    private boolean sentConnectingMessage;
    private boolean sentJoinedMessage;
    private final Server server;
    private Container inventoryContainer;
    private Container openContainer;
    private long packetsOutTotal;
    private long packetsOutBytes;
    private long packetsInTotal;
    private long packetsInBytes;

    public ServerClient(Server server, long sessionID, NetworkInfo networkInfo, int slot, long authentication, LoadData save) {
        super(slot, authentication);
        this.server = server;
        this.sessionID = sessionID;
        this.networkInfo = networkInfo;
        this.characterStats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        this.totalStats = null;
        this.newStats = new PlayerStats(false, EmptyStats.Mode.WRITE_ONLY);
        this.makeServerClient();
        this.permissionLevel = Settings.serverOwnerAuth != -1L && Settings.serverOwnerAuth == authentication ? PermissionLevel.OWNER : PermissionLevel.USER;
        this.reset();
        this.lastReceivedPacketTime = System.currentTimeMillis();
        this.pvpEnabled = false;
        this.needAppearance = false;
        if (!server.world.settings.allowOutsideCharacters) {
            this.submittedCharacter = true;
        }
        this.achievements = null;
        if (save != null) {
            this.applySave(save);
        } else {
            this.characterUniqueID = CharacterSave.getNewUniqueCharacterID(null);
        }
        this.refreshAFKTimer();
        if (authentication == GameAuth.getAuthentication()) {
            this.permissionLevel = PermissionLevel.OWNER;
        }
    }

    public long getSessionID() {
        return this.sessionID;
    }

    public int getCharacterUniqueID() {
        return this.characterUniqueID;
    }

    public SaveData getSave() {
        SaveData save = new SaveData("PLAYER");
        save.addSafeString("name", this.getName());
        save.addInt("characterUniqueID", this.characterUniqueID);
        save.addInt("permissions", this.permissionLevel.getLevel());
        save.addBoolean("needAppearance", this.needAppearance);
        save.addInt("team", this.getTeamID());
        save.addBoolean("pvp", this.pvpEnabled);
        save.addBoolean("isDead", this.isDead());
        SaveData mobData = new SaveData("MOB");
        this.playerMob.addSaveData(mobData);
        save.addSaveData(mobData);
        save.addUnsafeString("level", this.levelIdentifier.stringID);
        if (this.levelIdentifierFallback != null) {
            save.addUnsafeString("levelFallback", this.levelIdentifierFallback.stringID);
            if (this.tilePosFallback != null) {
                save.addPoint("tilePosFallback", this.tilePosFallback);
            }
        }
        if (this.levelIdentifier.isIslandPosition()) {
            save.addPoint("island", new Point(this.levelIdentifier.getIslandX(), this.levelIdentifier.getIslandY()));
            save.addInt("dimension", this.levelIdentifier.getIslandDimension());
        }
        save.addUnsafeString("spawnLevel", this.spawnLevelIdentifier.stringID);
        if (this.spawnLevelIdentifier.isIslandPosition()) {
            save.addPoint("spawnIsland", new Point(this.spawnLevelIdentifier.getIslandX(), this.spawnLevelIdentifier.getIslandY()));
            save.addInt("spawnDimension", this.spawnLevelIdentifier.getIslandDimension());
        }
        save.addPoint("spawnTile", this.spawnTile);
        SaveData adventurePartyData = new SaveData("adventureParty");
        this.adventureParty.addSaveData(adventurePartyData);
        save.addSaveData(adventurePartyData);
        save.addBoolean("hasNewJournalEntry", this.hasNewJournalEntry);
        if (!this.homePortals.isEmpty()) {
            SaveData homePortalsData = new SaveData("homePortals");
            for (MobWorldPosition mobWorldPosition : this.homePortals) {
                homePortalsData.addSaveData(mobWorldPosition.getSaveData("portal"));
            }
            save.addSaveData(homePortalsData);
        }
        save.addIntArray("quests", this.quests.keySet().stream().filter(Objects::nonNull).mapToInt(Quest::getUniqueID).toArray());
        save.addIntArray("trackedQuests", this.quests.entrySet().stream().filter(Map.Entry::getValue).mapToInt(e -> ((Quest)e.getKey()).getUniqueID()).toArray());
        SaveData deaths = new SaveData("DEATHS");
        for (WorldDeathLocation worldDeathLocation : this.getDeathLocations()) {
            SaveData saveData = new SaveData("death");
            worldDeathLocation.addSaveData(saveData);
            deaths.addSaveData(saveData);
        }
        save.addSaveData(deaths);
        SaveData discoveredPresets = new SaveData("DISCOVERED_PRESETS");
        for (Map.Entry<LevelIdentifier, PointHashSet> entry : this.discoveredPresetTiles.entrySet()) {
            LevelIdentifier levelIdentifier = entry.getKey();
            PointHashSet tiles = entry.getValue();
            if (tiles.isEmpty()) continue;
            SaveData discoveredPresetsSave = new SaveData("");
            discoveredPresetsSave.addUnsafeString("levelIdentifier", levelIdentifier.stringID);
            int currentTileIndex = 0;
            for (Point tile : tiles) {
                discoveredPresetsSave.addPoint("tile" + ++currentTileIndex, tile);
            }
            discoveredPresets.addSaveData(discoveredPresetsSave);
        }
        if (!discoveredPresets.isEmpty()) {
            save.addSaveData(discoveredPresets);
        }
        SaveData saveData = new SaveData("STATS");
        this.characterStats.addSaveData(saveData);
        save.addSaveData(saveData);
        SaveData saveData2 = new SaveData("NEWSTATS");
        this.newStats.addSaveData(saveData2);
        if (!saveData2.isEmpty()) {
            save.addSaveData(saveData2);
        }
        return save;
    }

    public static String loadClientName(LoadData save) {
        boolean needAppearance = save.getBoolean("needAppearance", true, false);
        if (needAppearance) {
            return "N/A";
        }
        return save.getUnsafeString("name", "N/A");
    }

    public static HumanLook loadClientLook(LoadData save) {
        HumanLook out = new HumanLook();
        if (save.hasLoadDataByName("MOB")) {
            LoadData mob = save.getFirstLoadDataByName("MOB");
            if (mob.hasLoadDataByName("LOOK")) {
                LoadData look = mob.getFirstLoadDataByName("LOOK");
                out.applyLoadData(look);
            } else {
                GameLog.warn.println("Could not load client look: Doesn't have MOB.LOOK component");
            }
        } else {
            GameLog.warn.println("Could not load client look: Doesn't have MOB component");
        }
        return out;
    }

    public static PlayerStats loadClientStats(LoadData save) {
        LoadData statsSave = save.getFirstLoadDataByName("STATS");
        PlayerStats stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        if (statsSave != null) {
            stats.applyLoadData(statsSave);
        }
        return stats;
    }

    public void applySave(LoadData save) {
        this.playerMob = new PlayerMob(this.authentication, this);
        try {
            LoadData discoveredPresets;
            LoadData adventurePartyData;
            LoadData mobData;
            OneWorldMigration migrationData = this.server.world.oneWorldMigration;
            LoadData statsData = save.getFirstLoadDataByName("STATS");
            if (statsData != null) {
                this.characterStats.applyLoadData(statsData);
            } else {
                GameLog.warn.println("Could not load " + this.getName() + " server stats");
            }
            LoadData newStatsData = save.getFirstLoadDataByName("NEWSTATS");
            if (newStatsData != null) {
                this.newStats.applyLoadData(newStatsData);
            }
            this.permissionLevel = PermissionLevel.getLevel(save.getInt("permissions", PermissionLevel.USER.getLevel()));
            String fallbackLevelIdentifierStringID = save.getUnsafeString("levelFallback", null, false);
            if (fallbackLevelIdentifierStringID != null) {
                try {
                    this.levelIdentifierFallback = new LevelIdentifier(fallbackLevelIdentifierStringID);
                    this.tilePosFallback = save.getPoint("tilePosFallback", null, false);
                    if (migrationData != null) {
                        LevelIdentifier oldFallbackLevelIdentifier = this.levelIdentifierFallback;
                        this.levelIdentifierFallback = migrationData.getNewLevelIdentifier(oldFallbackLevelIdentifier);
                        if (this.tilePosFallback != null) {
                            Point offset = migrationData.getTilePositionOffset(oldFallbackLevelIdentifier);
                            this.tilePosFallback = new Point(this.tilePosFallback.x + offset.x, this.tilePosFallback.y + offset.y);
                        }
                    }
                }
                catch (InvalidLevelIdentifierException oldFallbackLevelIdentifier) {
                    // empty catch block
                }
            }
            LevelIdentifier oldLevelIdentifier = null;
            boolean usedFallback = false;
            LevelIdentifier worldSpawn = this.server.world.worldEntity.spawnLevelIdentifier;
            try {
                LevelIdentifier levelIdentifier = null;
                String levelIdentifierString = save.getUnsafeString("level", null, false);
                if (levelIdentifierString != null) {
                    levelIdentifier = new LevelIdentifier(levelIdentifierString);
                    if (migrationData != null) {
                        oldLevelIdentifier = levelIdentifier;
                        if ((levelIdentifier = migrationData.getNewLevelIdentifier(levelIdentifier)) == null) {
                            levelIdentifier = LevelIdentifier.SURFACE_IDENTIFIER;
                        }
                    } else if (!this.server.world.levelExists(levelIdentifier)) {
                        levelIdentifier = null;
                    }
                }
                if (levelIdentifier == null && (levelIdentifier = this.levelIdentifierFallback) != null) {
                    usedFallback = true;
                }
                if (levelIdentifier == null) {
                    levelIdentifier = worldSpawn;
                }
                this.setLevelIdentifier(levelIdentifier);
            }
            catch (InvalidLevelIdentifierException e) {
                Point island = save.getPoint("island", null, false);
                int dimension = save.getInt("dimension", 0, false);
                if (island != null) {
                    LevelIdentifier levelIdentifier = new LevelIdentifier(island.x, island.y, dimension);
                    if (migrationData != null) {
                        oldLevelIdentifier = levelIdentifier;
                        if ((levelIdentifier = migrationData.getNewLevelIdentifier(levelIdentifier)) == null) {
                            levelIdentifier = LevelIdentifier.SURFACE_IDENTIFIER;
                        }
                    }
                    this.setLevelIdentifier(levelIdentifier);
                }
                GameLog.warn.println("Could not load player spawn level");
                this.setLevelIdentifier(worldSpawn);
            }
            this.playerMob.setLevel(this.server.world.getLevel(this));
            this.needAppearance = save.getBoolean("needAppearance", true);
            this.isDead = save.getBoolean("isDead", false);
            this.setTeamID(this.server.world.getTeams().getPlayerTeamID(this.authentication));
            this.pvpEnabled = save.getBoolean("pvp", false);
            this.playerMob.playerName = save.getSafeString("name", "N/A");
            if (this.playerMob.getDisplayName().equals("N/A")) {
                this.needAppearance = true;
            }
            this.characterUniqueID = save.getInt("characterUniqueID", this.characterUniqueID, false);
            if (this.characterUniqueID == 0) {
                this.characterUniqueID = CharacterSave.getNewUniqueCharacterID(null);
            }
            if ((mobData = save.getFirstLoadDataByName("MOB")) != null) {
                this.playerMob.applyLoadData(mobData);
            } else {
                GameLog.warn.println("Could not load player mob data");
            }
            if (usedFallback && this.tilePosFallback != null) {
                Point pos = this.getPlayerPosFromTile(this.getLevel(), this.tilePosFallback.x, this.tilePosFallback.y);
                this.playerMob.setPos(pos.x, pos.y, true);
            } else if (oldLevelIdentifier != null && migrationData != null) {
                this.playerMob.migrateToOneWorld(migrationData, oldLevelIdentifier, migrationData.getTilePositionOffset(oldLevelIdentifier), migrationData.getLevelPositionOffset(oldLevelIdentifier), this.playerMob);
            }
            LevelIdentifier oldSpawnLevelIdentifier = null;
            try {
                this.spawnLevelIdentifier = new LevelIdentifier(save.getUnsafeString("spawnLevel", null, false));
                if (migrationData != null) {
                    oldSpawnLevelIdentifier = this.spawnLevelIdentifier;
                    this.spawnLevelIdentifier = migrationData.getNewLevelIdentifier(this.spawnLevelIdentifier);
                    if (this.spawnLevelIdentifier == null) {
                        this.spawnLevelIdentifier = worldSpawn;
                    }
                }
            }
            catch (InvalidLevelIdentifierException e) {
                Point loadedSpawnIsland = save.getPoint("spawnIsland", null, false);
                int loadedSpawnDimension = save.getInt("spawnDimension", 0, false);
                this.spawnLevelIdentifier = loadedSpawnIsland != null ? new LevelIdentifier(loadedSpawnIsland.x, loadedSpawnIsland.y, loadedSpawnDimension) : worldSpawn;
            }
            this.spawnTile = save.getPoint("spawnTile", null);
            if (this.spawnTile != null) {
                if (oldSpawnLevelIdentifier != null) {
                    Point offset = migrationData.getTilePositionOffset(oldSpawnLevelIdentifier);
                    this.spawnTile = new Point(this.spawnTile.x + offset.x, this.spawnTile.y + offset.y);
                }
            } else {
                this.spawnTile = this.server.world.worldEntity.spawnTile;
            }
            if ((adventurePartyData = save.getFirstLoadDataByName("adventureParty")) != null) {
                try {
                    this.adventureParty.applyLoadData(adventurePartyData);
                }
                catch (Exception e) {
                    System.err.println("Failed to load adventure party data for " + this.getName());
                    e.printStackTrace();
                }
            }
            this.hasNewJournalEntry = save.getBoolean("hasNewJournalEntry", false, false);
            this.homePortals.clear();
            LoadData homePortalsData = save.getFirstLoadDataByName("homePortals");
            if (homePortalsData != null) {
                for (LoadData portalData : homePortalsData.getLoadDataByName("portal")) {
                    this.homePortals.add(new MobWorldPosition(portalData));
                }
            }
            int[] questIDs = save.getIntArray("quests", new int[0]);
            HashSet<Integer> trackedQuests = new HashSet<Integer>();
            for (int uniqueID : save.getIntArray("trackedQuests", new int[0], false)) {
                trackedQuests.add(uniqueID);
            }
            for (int questID : questIDs) {
                Quest quest = this.server.world.getQuests().getQuest(questID);
                if (quest == null) continue;
                this.quests.put(quest, trackedQuests.contains(quest.getUniqueID()));
            }
            LoadData deathsSave = save.getFirstLoadDataByName("DEATHS");
            if (deathsSave != null) {
                for (LoadData deathSave : deathsSave.getLoadDataByName("death")) {
                    try {
                        this.deathLocations.add(new WorldDeathLocation(deathSave));
                    }
                    catch (Exception e) {
                        System.err.println("Failed to load death location for " + this.getName());
                        e.printStackTrace();
                    }
                }
                this.deathLocations.sort(Comparator.comparingInt(d -> d.deathTime));
            }
            if ((discoveredPresets = save.getFirstLoadDataByName("DISCOVERED_PRESETS")) != null) {
                for (LoadData discoveredPresetsSave : discoveredPresets.getLoadData()) {
                    String levelIdentifierString = discoveredPresetsSave.getSafeString("levelIdentifier", null, false);
                    try {
                        Point tile;
                        if (levelIdentifierString == null) continue;
                        LevelIdentifier levelIdentifier = new LevelIdentifier(levelIdentifierString);
                        PointHashSet tiles = this.discoveredPresetTiles.compute(levelIdentifier, (k, v) -> v == null ? new PointHashSet() : v);
                        int currentTileIndex = 0;
                        while ((tile = discoveredPresetsSave.getPoint("tile" + ++currentTileIndex, null, false)) != null) {
                            tiles.add(tile.x, tile.y);
                        }
                    }
                    catch (Exception e) {
                        System.err.println("Failed to load discovered preset for " + this.getName() + ", level: " + levelIdentifierString);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (Settings.serverOwnerAuth != -1L && Settings.serverOwnerAuth == this.authentication) {
            this.permissionLevel = PermissionLevel.OWNER;
        }
        if (!this.needAppearance && this.playerMob.playerName.equals(Settings.serverOwnerName)) {
            this.permissionLevel = PermissionLevel.OWNER;
        }
        this.playerMob.setTeam(this.getTeamID());
        this.playerMob.setUniqueID(this.slot);
        if (!this.playerMob.isInitialized()) {
            this.playerMob.init();
        }
        this.updateInventoryContainer();
        this.server.usedNames.put(this.authentication, this.getName());
    }

    public void onUnloading() {
        if (this.playerMob.isRiding()) {
            this.playerMob.dismount();
        }
        this.playerMob.onUnloading(null);
    }

    public void setupSyncUpdate(PacketWriter writer, ServerClient receiver) {
        writer.putNextBoolean(this.hasSpawned);
        writer.putNextBoolean(this.isDead);
        if (this.isDead) {
            writer.putNextInt(this.getRespawnTimeRemaining());
        }
        writer.putNextBoolean(this.pvpEnabled);
        writer.putNextInt(this.getTeamID());
        writer.putNextBoolean(this.isSamePlace(receiver));
        if (receiver == this) {
            writer.putNextBoolean(this.openContainer != null);
            if (this.openContainer != null) {
                writer.putNextInt(this.openContainer.uniqueSeed);
            }
        }
        boolean sendPositionData = receiver != this && this.hasSpawned() && !this.isDead() && (this.isSameTeam(receiver) || this.getServer().getLocalServerClient() == receiver && GlobalData.debugCheatActive());
        writer.putNextBoolean(sendPositionData);
        if (sendPositionData) {
            writer.putNextInt(this.playerMob.getX());
            writer.putNextInt(this.playerMob.getY());
            writer.putNextByteUnsigned(this.playerMob.getDir());
        }
    }

    public void setupPrivateSyncUpdate(PacketWriter writer) {
        this.levelIdentifier.writePacket(writer);
        HashSet keys = (HashSet)this.loadedRegions.get(this.levelIdentifier);
        if (keys == null) {
            writer.putNextInt(0);
        } else {
            writer.putNextInt(keys.hashCode());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickMovement(float delta) {
        if (this.playerMob != null) {
            if (this.hasSpawned) {
                this.playerMob.setLevel(this.server.world.getLevel(this));
                if (!this.isDead()) {
                    Object object = this.playerMob.getLevel().entityManager.lock;
                    synchronized (object) {
                        this.playerMob.tickMovement(delta);
                        this.playerMob.getLevel().entityManager.players.updateRegion(this.playerMob);
                    }
                } else {
                    this.playerMob.updateRegion(null, null, null);
                }
            } else {
                this.playerMob.updateRegion(null, null, null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        if (this.spawnedCheckTimer != 0L && this.spawnedCheckTimer < System.currentTimeMillis()) {
            if (this.hasSpawned()) {
                this.spawnedCheckTimer = 0L;
            } else {
                GameLog.warn.println("Kicking player " + this.getName() + " because they did not submit a spawn packet");
                this.server.disconnectClient(this, PacketDisconnect.Code.STATE_DESYNC);
                return;
            }
        }
        if (!this.needPlayerStats() && this.newStats.isImportantDirty()) {
            this.combineNewStats();
        }
        long currentTime = this.server.world.getTime();
        this.trainingDummyDPSTracker.tick(currentTime);
        Level level = this.server.world.getLevel(this);
        this.msTimeTicker += 50L;
        if (this.msTimeTicker > 1000L) {
            this.msTimeTicker -= 1000L;
            if (!this.needAppearance() && this.hasSubmittedCharacter()) {
                this.newStats.time_played.increment(1);
                if (this.hasSpawned()) {
                    this.sendPacket(new PacketPlayerPrivateSync(this));
                }
            }
            ++this.networkUpdateTimer;
            if (this.networkUpdateTimer % 5 == 0) {
                this.sendPacket(new PacketNetworkUpdate(this));
                if (this.needPlayerStats()) {
                    this.sendPacket(new PacketRequestClientStats());
                } else if (this.newStats.isDirty()) {
                    this.combineNewStats();
                }
            }
            if (this.networkUpdateTimer % 10 == 0) {
                this.server.streamClients().forEach(c -> c.sendPacket(new PacketPlayerSync(this, (ServerClient)c)));
            }
            if (this.trainingDummyDPSTracker.isLastHitBeforeReset(currentTime)) {
                float dps = this.trainingDummyDPSTracker.getDPS(currentTime);
                this.sendPacket(new PacketShowDPS(this.playerMob.getUniqueID(), dps));
            }
            if (this.playerMob != null) {
                int distanceRan = (int)this.playerMob.getDistanceRan() - this.lastDistanceRan;
                if (distanceRan > 0) {
                    this.lastDistanceRan = (int)this.playerMob.getDistanceRan();
                    this.newStats.distance_ran.increment(distanceRan);
                    if (distanceRan > 1000) {
                        GameLog.warn.println(this.getName() + " ran more than 1000 units the last second? (" + distanceRan + ")");
                    }
                }
                this.tickDiscoveredBiomes(level, true);
                int distanceRidden = (int)this.playerMob.getDistanceRidden() - this.lastDistanceRidden;
                if (distanceRidden > 0) {
                    this.lastDistanceRidden = (int)this.playerMob.getDistanceRidden();
                    this.newStats.distance_ridden.increment(distanceRidden);
                    if (distanceRidden > 1000) {
                        GameLog.warn.println(this.getName() + " rode more than 1000 units the last second? (" + distanceRidden + ")");
                    }
                }
            }
            ++this.sessionTime;
        }
        if (this.shouldSendDirtyStats) {
            this.sendDirtyStats();
        }
        this.adventureParty.serverTick();
        for (Map.Entry entry : this.loadedRegions.entrySet()) {
            LevelIdentifier identifier = (LevelIdentifier)entry.getKey();
            Iterator iterator = ((HashSet)entry.getValue()).iterator();
            while (iterator.hasNext()) {
                int regionY;
                long uniqueKey = (Long)iterator.next();
                Level regionLevel = this.server.world.getLevel(identifier);
                int regionX = GameMath.getXFromUniqueLongKey(uniqueKey);
                Region region = regionLevel.regionManager.getRegion(regionX, regionY = GameMath.getYFromUniqueLongKey(uniqueKey), true);
                if (region == null) continue;
                region.unloadRegionBuffer.keepLoaded();
            }
        }
        if (level.isOneWorldLevel()) {
            this.server.world.worldEntity.refreshWorldPresetCache(level.getIdentifier(), this.playerMob.getTileX(), this.playerMob.getTileY());
        }
        if (this.hasSpawned && this.playerMob != null) {
            boolean nextAFK;
            Iterator<Quest> questsIt = this.quests.keySet().iterator();
            while (questsIt.hasNext()) {
                Quest quest = questsIt.next();
                if (quest.isRemoved()) {
                    questsIt.remove();
                    continue;
                }
                quest.tick(this);
            }
            if (this.playerMob.getAttackHandler() != null) {
                this.refreshAFKTimer();
            }
            if (!this.isDead()) {
                if (this.openContainer != null && !this.openContainer.isValid(this)) {
                    this.closeContainer(true);
                }
                this.getContainer().tick();
                this.playerMob.setLevel(level);
                Object quest = this.playerMob.getLevel().entityManager.lock;
                synchronized (quest) {
                    this.playerMob.serverTick();
                }
                this.nextMobSpawn += this.getMobSpawnRate(level);
                while (this.nextMobSpawn >= 1.0f) {
                    this.nextMobSpawn -= 1.0f;
                    if (level.entityManager.tickMobSpawning(this.server, this)) continue;
                    this.nextMobSpawn += 0.5f;
                }
                this.nextCritterSpawn += this.getCritterSpawnRate(level);
                while (this.nextCritterSpawn >= 1.0f) {
                    this.nextCritterSpawn -= 1.0f;
                    if (level.entityManager.tickCritterSpawning(this.server, this)) continue;
                    this.nextCritterSpawn += 0.5f;
                }
                this.nextSettlerSpawn += settlerSpawnRate;
                while (this.nextSettlerSpawn >= 1.0f) {
                    this.nextSettlerSpawn -= 1.0f;
                    Settler.tickServerClientSpawn(this.server, this);
                }
            }
            Object quest = this.playerMob.getLevel().entityManager.lock;
            synchronized (quest) {
                this.playerMob.tickSync();
            }
            boolean bl = nextAFK = this.getTimeSinceLastAction() >= (long)MS_TO_AFK;
            if (this.isAFK != nextAFK) {
                this.isAFK = nextAFK;
                if (this.isAFK) {
                    System.out.println(this.getName() + " is now AFK");
                } else {
                    System.out.println(this.getName() + " is no longer AFK");
                }
            }
        }
    }

    public void forceCombineNewStats() {
        if (this.needPlayerStats()) {
            return;
        }
        this.combineNewStats();
    }

    private void combineNewStats() {
        this.characterStats.combineDirty(this.newStats);
        if (this.server.world.settings.achievementsEnabled() && !this.needPlayerStats()) {
            this.totalStats.combineDirty(this.newStats);
        }
        this.server.world.worldEntity.worldStats.combineDirty(this.newStats);
        Level level = this.getLevel();
        if (level != null) {
            level.levelStats.combineDirty(this.newStats);
        }
        this.newStats.resetCombine();
        this.newStats.cleanAll();
        this.shouldSendDirtyStats = true;
        JournalChallengeRegistry.handleListeners(this, StatsCombinedJournalChallengeListener.class, challenge -> challenge.onNewStatsCombined(this));
    }

    private void sendDirtyStats() {
        this.shouldSendDirtyStats = false;
        if (this.characterStats.isDirty()) {
            this.sendPacket(new PacketCharacterStatsUpdate(this.characterStats));
            this.characterStats.cleanAll();
        }
        if (!this.needPlayerStats() && this.totalStats.isDirty()) {
            this.sendPacket(new PacketTotalStatsUpdate(this.totalStats));
            this.achievements.runStatsUpdate(this);
            this.totalStats.cleanAll();
        }
    }

    private boolean hasAxe() {
        return this.playerMob.getInv().streamInventorySlots(true, false, true, true).anyMatch(slot -> {
            if (!slot.isSlotClear()) {
                InventoryItem item = slot.getItem();
                if (item.item instanceof ToolDamageItem) {
                    ToolType toolType = ((ToolDamageItem)item.item).getToolType();
                    return toolType == ToolType.ALL || toolType == ToolType.AXE;
                }
            }
            return false;
        });
    }

    public void die(int respawnTime) {
        if (!this.isDead()) {
            this.isDead = true;
            this.newStats.deaths.increment(1);
            this.closeContainer(false);
            if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_MATS) {
                ItemDropperHandler dropper = (item, slot, isLocked) -> {
                    ItemPickupEntity entity = item.getPickupEntity(this.playerMob.getLevel(), this.playerMob.x, this.playerMob.y).setPlayerDeathAuth(this, slot, isLocked);
                    this.playerMob.getLevel().entityManager.pickups.add(entity);
                };
                this.playerMob.getInv().streamPlayerSlots(true, false, true, true).forEach(slot -> {
                    boolean isLocked;
                    InventoryItem item;
                    if (!slot.isSlotClear(this.playerMob.getInv()) && item.item.dropAsMatDeathPenalty((PlayerInventorySlot)slot, isLocked = slot.isItemLocked(this.playerMob.getInv()), item = slot.getItem(this.playerMob.getInv()), dropper)) {
                        slot.setItem(this.playerMob.getInv(), null);
                    }
                });
            } else if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_MAIN_INVENTORY) {
                this.playerMob.getInv().dropMainInventory();
            } else if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_FULL_INVENTORY || this.server.world.settings.deathPenalty == GameDeathPenalty.HARDCORE) {
                this.playerMob.getInv().dropInventory();
            }
            if (!this.hasAxe()) {
                this.playerMob.getInv().addItem(new InventoryItem("woodaxe", 1), true, "respawnitem", null);
            }
            this.respawnTime = this.server.world.worldEntity.getTime() + (long)respawnTime;
            this.deathLocations.addLast(new WorldDeathLocation(this.characterStats, this.levelIdentifier, this.playerMob.getX(), this.playerMob.getY()));
            this.sendPacket(new PacketAddDeathLocation(new LevelDeathLocation(0, this.playerMob.getX(), this.playerMob.getY())));
            this.server.network.sendToAllClients(new PacketPlayerDie(this.slot, respawnTime));
        }
    }

    public boolean removeDeathLocation(LevelIdentifier levelIdentifier, int x, int y) {
        return this.deathLocations.removeIf(l -> l.levelIdentifier.equals(levelIdentifier) && l.x == x && l.y == y);
    }

    public boolean removeDeathLocations(int islandX, int islandY) {
        return this.deathLocations.removeIf(l -> l.levelIdentifier.isIslandPosition() && l.levelIdentifier.getIslandX() == islandX && l.levelIdentifier.getIslandY() == islandY);
    }

    public boolean isPresetDiscovered(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        PointHashSet tiles = this.discoveredPresetTiles.get(levelIdentifier);
        return tiles != null && tiles.contains(tileX, tileY);
    }

    public void addDiscoveredPreset(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        PointHashSet tiles = this.discoveredPresetTiles.compute(levelIdentifier, (k, v) -> v == null ? new PointHashSet() : v);
        tiles.add(tileX, tileY);
    }

    public float getCritterSpawnRate(Level level) {
        return critterSpawnRate * this.getSpawnRateModifier(level, Mob.CRITTER_SPAWN_AREA, 0.75f);
    }

    public float getCritterSpawnCap(Level level, int tileX, int tileY) {
        return EntityManager.getSpawnCap(level.presentPlayers, 20.0f, -10.0f) * this.server.world.settings.difficulty.enemySpawnCapModifier * level.entityManager.getSpawnCapMod(tileX, tileY) * this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP).floatValue();
    }

    public MobSpawnTable getCritterSpawnTable(Level level, int tileX, int tileY) {
        MobSpawnTable mobSpawnTable = new MobSpawnTable().include(level.getBiome(tileX, tileY).getCritterSpawnTable(level));
        for (Quest quest : this.quests.keySet()) {
            MobSpawnTable extra = quest.getExtraCritterSpawnTable(this, level);
            if (extra == null) continue;
            mobSpawnTable.include(extra);
        }
        return mobSpawnTable;
    }

    public int getOtherNearbyPlayers(Level level, int range) {
        if (level.presentPlayers > 1) {
            return (int)level.entityManager.players.streamArea(this.playerMob.getX(), this.playerMob.getY(), range).filter(p -> p != this.playerMob).filter(p -> p.getDistance(this.playerMob) <= (float)range).count();
        }
        return 0;
    }

    public int getOtherNearbyPlayers(Level level, MobSpawnArea area) {
        return this.getOtherNearbyPlayers(level, (area.minSpawnDistance + area.maxSpawnDistance) / 2);
    }

    public float getSpawnRateModifier(Level level, MobSpawnArea area, float exponent) {
        int nearbyPlayers = this.getOtherNearbyPlayers(level, area);
        if (nearbyPlayers <= 0) {
            return 1.0f;
        }
        return (float)Math.pow(1.0f / (float)nearbyPlayers, exponent);
    }

    public float getMobSpawnRate(Level level) {
        return mobSpawnRate * (1.0f + (float)this.adventureParty.getSize() * mobSpawnRatePartyMemberModifier) * this.server.world.settings.difficulty.enemySpawnRateModifier * level.entityManager.getSpawnRate(this.playerMob.getTileX(), this.playerMob.getTileY()) * this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_RATE).floatValue() * this.getSpawnRateModifier(level, Mob.MOB_SPAWN_AREA, 0.75f);
    }

    public float getMobSpawnCap(Level level) {
        return EntityManager.getSpawnCap(level.presentPlayers, 25.0f, 5.0f) * this.server.world.settings.difficulty.enemySpawnCapModifier * level.entityManager.getSpawnCapMod(this.playerMob.getTileX(), this.playerMob.getTileY()) * this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP).floatValue();
    }

    public MobSpawnTable getMobSpawnTable(Level level, int tileX, int tileY) {
        MobSpawnTable mobSpawnTable = new MobSpawnTable().include(level.getBiome(tileX, tileY).getMobSpawnTable(level));
        for (Quest quest : this.quests.keySet()) {
            MobSpawnTable extra = quest.getExtraMobSpawnTable(this, level);
            if (extra == null) continue;
            mobSpawnTable.include(extra);
        }
        return mobSpawnTable;
    }

    public FishingLootTable getFishingLoot(FishingSpot spot) {
        FishingLootTable fishingLootTable = new FishingLootTable(spot.getBiome().getFishingLootTable(spot));
        for (Quest quest : this.quests.keySet()) {
            FishingLootTable extra = quest.getExtraFishingLoot(this, spot);
            if (extra == null) continue;
            fishingLootTable.addAll(extra);
        }
        return fishingLootTable;
    }

    protected void cleanDeathLocations() {
        WorldDeathLocation first;
        while (!this.deathLocations.isEmpty() && (first = this.deathLocations.getFirst()).getSecondsSince(this.characterStats) > 7200) {
            this.deathLocations.removeFirst();
        }
        while (this.deathLocations.size() > 5) {
            this.deathLocations.removeFirst();
        }
    }

    public Iterable<WorldDeathLocation> getDeathLocations() {
        this.cleanDeathLocations();
        return this.deathLocations;
    }

    public Stream<WorldDeathLocation> streamDeathLocations() {
        this.cleanDeathLocations();
        return this.deathLocations.stream();
    }

    public void addLoadedRegion(RegionPosition position, boolean forceSendSpawnPackets) {
        this.addLoadedRegion(position.level, position.regionX, position.regionY, forceSendSpawnPackets);
    }

    public void addLoadedRegion(Level level, int regionX, int regionY, boolean forceSendSpawnPackets) {
        Region region;
        if ((!this.hasRegionLoaded(level, regionX, regionY) || forceSendSpawnPackets) && (region = level.regionManager.getRegion(regionX, regionY, true)) != null) {
            this.loadedRegions.add(level.getIdentifier(), GameMath.getUniqueLongKey(regionX, regionY));
            level.entityManager.onServerClientLoadedRegion(region, this);
        }
    }

    public boolean removeLoadedRegion(Level level, int regionX, int regionY, boolean sendPacket, boolean onlySendPacketIfLoaded) {
        boolean success = this.loadedRegions.remove(level.getIdentifier(), GameMath.getUniqueLongKey(regionX, regionY));
        if (sendPacket && (!onlySendPacketIfLoaded || success)) {
            this.sendPacket(new PacketUnloadRegion(level, regionX, regionY));
        }
        return success;
    }

    public boolean removeLoadedRegions(Level level, HashSet<Point> regionPositions, boolean sendPacket, boolean onlySendPacketIfLoaded) {
        List keyList = regionPositions.stream().map(p -> GameMath.getUniqueLongKey(p.x, p.y)).collect(Collectors.toList());
        boolean success = this.loadedRegions.removeAll(level.getIdentifier(), keyList);
        if (sendPacket && (!onlySendPacketIfLoaded || success)) {
            this.sendPacket(new PacketUnloadRegions(level, regionPositions));
        }
        return success;
    }

    public void removeLoadedRegions(Level level) {
        this.loadedRegions.clear(level.getIdentifier());
    }

    public boolean hasRegionLoaded(RegionPosition position) {
        return this.hasRegionLoaded(position.level, position.regionX, position.regionY);
    }

    public boolean hasRegionLoaded(Level level, int regionX, int regionY) {
        return this.hasRegionLoaded(level.getIdentifier(), regionX, regionY);
    }

    public boolean hasRegionLoaded(LevelIdentifier identifier, int regionX, int regionY) {
        return this.loadedRegions.contains(identifier, GameMath.getUniqueLongKey(regionX, regionY));
    }

    public boolean hasAnyLoadedRegionAtLevel(LevelIdentifier levelIdentifier) {
        return !this.loadedRegions.isEmpty(levelIdentifier);
    }

    public int getLoadedRegionsCount(LevelIdentifier identifier) {
        return this.loadedRegions.getSize(identifier);
    }

    public PacketPlayerLoadedRegions getLoadedRegionsPacket(LevelIdentifier identifier) {
        HashSet keys = (HashSet)this.loadedRegions.get(identifier);
        if (keys == null) {
            return new PacketPlayerLoadedRegions(identifier, new PointHashSet());
        }
        PointHashSet positions = new PointHashSet(keys.size());
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            positions.add(regionX, regionY);
        }
        return new PacketPlayerLoadedRegions(identifier, positions);
    }

    public boolean hasAnyRegionLoaded(LevelIdentifier levelIdentifier, Predicate<Point> tester) {
        HashSet keys = (HashSet)this.loadedRegions.get(levelIdentifier);
        if (keys == null || keys.isEmpty()) {
            return false;
        }
        return keys.stream().map(key -> new Point(GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key))).anyMatch(tester);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickTimeConnected() {
        long timeSinceLastReset;
        long timeSinceLastReceivedPacket;
        long timeBetweenPingRequests;
        this.timeConnected += 50;
        if (this.timeConnected % 10000 == 0) {
            this.sendPacket(new PacketWorldData(this.server.world.worldEntity));
        }
        this.pingTimer += 50;
        long l = timeBetweenPingRequests = Settings.maxClientLatencySeconds > 4 ? 2000L : Math.max((long)Settings.maxClientLatencySeconds * 1000L / 2L, 500L);
        if ((long)this.pingTimer > timeBetweenPingRequests) {
            this.pingTimer = 0;
            Object object = this.pingLock;
            synchronized (object) {
                this.expectedPing = new ExpectedPing(GameRandom.globalRandom.nextInt(), System.currentTimeMillis(), this.expectedPing);
                this.sendPacket(new PacketPing(this.expectedPing.responseKey));
            }
        }
        if ((timeSinceLastReceivedPacket = System.currentTimeMillis() - this.lastReceivedPacketTime) >= 5000L && (timeSinceLastReset = System.currentTimeMillis() - this.lastResetConnectionTime) >= 5000L) {
            if (this.networkInfo != null) {
                this.networkInfo.resetConnection();
            }
            this.lastResetConnectionTime = System.currentTimeMillis();
        }
        if (Settings.maxClientLatencySeconds > 0) {
            if ((long)this.latency > (long)Settings.maxClientLatencySeconds * 1000L || timeSinceLastReceivedPacket > (long)Settings.maxClientLatencySeconds * 1000L) {
                ++this.pingKickBuffer;
                if (this.pingKickBuffer > 100) {
                    System.out.println("Ping threshold for \"" + this.getName() + "\" reached, resulting in kick.");
                    this.server.disconnectClient(this.slot, PacketDisconnect.Code.CLIENT_NOT_RESPONDING);
                }
            } else if (this.pingKickBuffer > 0) {
                --this.pingKickBuffer;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void submitPingPacket(PacketPing packet) {
        if (packet.responseKey == -1) {
            this.sendPacket(new PacketPing(-1));
            return;
        }
        Object object = this.pingLock;
        synchronized (object) {
            ExpectedPing last = null;
            ExpectedPing next = this.expectedPing;
            int i = 0;
            while (next != null) {
                if (next.responseKey == packet.responseKey) {
                    if (last != null) {
                        last.next = null;
                    } else {
                        this.expectedPing = null;
                    }
                    this.latency = (int)(System.currentTimeMillis() - next.timeSent);
                    this.pingTimer = 0;
                    this.server.network.sendToAllClients(new PacketPlayerLatency(this.slot, this.latency));
                    break;
                }
                last = next;
                next = next.next;
                ++i;
            }
        }
    }

    @Override
    public String getName() {
        if (this.playerMob == null) {
            return "AUTH:" + this.authentication;
        }
        return this.playerMob.getDisplayName();
    }

    public void reset() {
        this.loadedRegions = new HashMapSet();
        this.hasSpawned = false;
        this.lastSpawnPacketRequestSystemTime = 0L;
        this.expectedPing = null;
        this.hasRequestedSelf = false;
        this.lastRequestSelfPacketSentSystemTime = 0L;
        this.closeContainer(true);
    }

    public boolean checkHasRequestedSelf() {
        long timeSinceLastPacket;
        if (!this.hasRequestedSelf && (timeSinceLastPacket = System.currentTimeMillis() - this.lastRequestSelfPacketSentSystemTime) >= 1000L) {
            this.lastRequestSelfPacketSentSystemTime = System.currentTimeMillis();
            this.sendPacket(new PacketNeedRequestSelf());
        }
        return this.hasRequestedSelf;
    }

    public void requestSelf() {
        this.hasRequestedSelf = true;
    }

    public int getRespawnTimeRemaining() {
        return (int)Math.max(0L, this.respawnTime - this.server.world.worldEntity.getTime());
    }

    public void respawn() {
        if (!this.isDead() || this.getRespawnTimeRemaining() > 200) {
            return;
        }
        if (this.server.world.settings.deathPenalty == GameDeathPenalty.HARDCORE) {
            return;
        }
        this.validateSpawnPoint(true);
        Point spawnPos = null;
        Level spawnLevel = this.server.world.getLevel(this.spawnLevelIdentifier);
        if (!this.isDefaultSpawnPoint()) {
            Point offset = RespawnObject.calculateSpawnOffset(spawnLevel, this.spawnTile.x, this.spawnTile.y, this);
            spawnPos = new Point(this.spawnTile.x * 32 + offset.x, this.spawnTile.y * 32 + offset.y);
        } else {
            spawnPos = this.getPlayerPosFromTile(spawnLevel, this.spawnTile.x, this.spawnTile.y);
        }
        this.playerMob.restore();
        this.hasSpawned = false;
        this.isDead = false;
        if (!this.levelIdentifier.equals(this.spawnLevelIdentifier)) {
            this.reset();
        }
        this.setLevelIdentifier(this.spawnLevelIdentifier);
        this.playerMob.setPos(spawnPos.x, spawnPos.y, true);
        this.playerMob.dx = 0.0f;
        this.playerMob.dy = 0.0f;
        this.playerMob.setHealth(Math.max(this.playerMob.getMaxHealth() / 2, 1));
        this.playerMob.setMana(Math.max(this.playerMob.getMaxMana(), 1));
        this.playerMob.hungerLevel = Math.max(0.5f, this.playerMob.hungerLevel);
        this.server.network.sendToAllClients(new PacketPlayerRespawn(this));
    }

    public boolean validateSpawnPoint(boolean sendMessage) {
        Level level;
        Point point;
        if (!this.isDefaultSpawnPoint() && (point = RespawnObject.calculateSpawnOffset(level = this.server.world.getLevel(this.spawnLevelIdentifier), this.spawnTile.x, this.spawnTile.y, this)) == null) {
            this.resetSpawnPoint(this.server);
            if (sendMessage) {
                this.sendChatMessage(new LocalMessage("misc", "spawninvalid"));
            }
            return false;
        }
        return true;
    }

    public boolean isDefaultSpawnPoint() {
        return this.server.world.worldEntity.spawnLevelIdentifier.equals(this.spawnLevelIdentifier) && this.server.world.worldEntity.spawnTile.equals(this.spawnTile);
    }

    public void resetSpawnPoint(Server server) {
        this.spawnLevelIdentifier = server.world.worldEntity.spawnLevelIdentifier;
        this.spawnTile = server.world.worldEntity.spawnTile;
    }

    public void checkSpawned() {
        if (!this.hasSpawned()) {
            long timeSinceLastPacket = System.currentTimeMillis() - this.lastSpawnPacketRequestSystemTime;
            if (timeSinceLastPacket >= 300L) {
                this.lastSpawnPacketRequestSystemTime = System.currentTimeMillis();
                this.sendPacket(new PacketSpawnPlayerReceipt(true));
            }
            if (this.spawnedCheckTimer == 0L) {
                GameLog.warn.println(this.getName() + " has not submitted spawn packet, giving them 5 seconds to do so.");
                this.spawnedCheckTimer = System.currentTimeMillis() + 5000L;
            }
        }
    }

    @Override
    public boolean pvpEnabled() {
        return this.server.world.settings.forcedPvP || this.pvpEnabled;
    }

    public long getSessionTime() {
        return this.sessionTime;
    }

    public void refreshAFKTimer() {
        this.lastActionTime = this.server.world.worldEntity.getTime();
    }

    public long getTimeSinceLastAction() {
        return this.server.world.worldEntity.getTime() - this.lastActionTime;
    }

    public boolean isAFK() {
        return this.isAFK;
    }

    public boolean needAppearance() {
        return this.needAppearance;
    }

    public boolean hasSubmittedCharacter() {
        return this.submittedCharacter;
    }

    public LevelIdentifierTilePos getFallbackIsland(boolean loadLevelsIfNeeded) {
        LevelIdentifierTilePos fallback = this.getFallbackLevel();
        HashSet<LevelIdentifier> checked = new HashSet<LevelIdentifier>();
        while (true) {
            if (fallback == null) {
                return null;
            }
            if (fallback.identifier.isIslandPosition()) {
                return fallback;
            }
            if (!loadLevelsIfNeeded && !this.server.world.levelManager.isLoaded(fallback.identifier)) break;
            Level nextLevel = this.server.world.getLevel(fallback.identifier);
            if (nextLevel.fallbackIdentifier == null) {
                return null;
            }
            if (checked.contains(nextLevel.fallbackIdentifier)) {
                return null;
            }
            checked.add(nextLevel.fallbackIdentifier);
            fallback = new LevelIdentifierTilePos(nextLevel.fallbackIdentifier, nextLevel.fallbackTilePos);
        }
        return null;
    }

    public LevelIdentifierTilePos getFallbackLevel() {
        if (this.levelIdentifierFallback == null) {
            Level level = this.getLevel();
            if (level.fallbackIdentifier != null) {
                return new LevelIdentifierTilePos(level.fallbackIdentifier, level.fallbackTilePos);
            }
            return null;
        }
        return new LevelIdentifierTilePos(this.levelIdentifierFallback, this.tilePosFallback);
    }

    public void clearFallbackLevel() {
        this.levelIdentifierFallback = null;
        this.tilePosFallback = null;
    }

    public void setFallbackLevel(Level level, int tileX, int tileY) {
        this.levelIdentifierFallback = level.getIdentifier();
        this.tilePosFallback = new Point(tileX, tileY);
    }

    public void setLevelIdentifier(LevelIdentifier identifier) {
        this.levelIdentifier = identifier;
        if (this.levelIdentifierFallback != null && this.levelIdentifierFallback.equals(identifier)) {
            this.clearFallbackLevel();
        }
        if (this.levelIdentifier.equals(LevelIdentifier.SURFACE_IDENTIFIER) || this.levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER) || this.levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            this.clearFallbackLevel();
        }
    }

    public void changeToFallbackLevel(LevelIdentifier makeSureItsNotThis, boolean sendChangePacket) {
        if (!(this.levelIdentifierFallback == null || this.tilePosFallback == null || makeSureItsNotThis != null && this.levelIdentifierFallback.equals(makeSureItsNotThis))) {
            if (sendChangePacket) {
                this.changeLevel(this.levelIdentifierFallback, level -> this.getPlayerPosFromTile((Level)level, this.tilePosFallback.x, this.tilePosFallback.y), true);
            } else {
                this.playerMob.setPos(this.tilePosFallback.x * 32 + 16, this.tilePosFallback.y * 32 + 16, true);
                this.setLevelIdentifier(this.levelIdentifierFallback);
            }
        } else {
            this.validateSpawnPoint(true);
            if (!(this.spawnLevelIdentifier == null || makeSureItsNotThis != null && this.spawnLevelIdentifier.equals(makeSureItsNotThis))) {
                if (sendChangePacket) {
                    this.changeLevel(this.spawnLevelIdentifier, level -> this.getPlayerPosFromTile((Level)level, this.spawnTile.x, this.spawnTile.y), true);
                } else {
                    this.playerMob.setPos(this.spawnTile.x * 32 + 16, this.spawnTile.y * 32 + 16, true);
                    this.setLevelIdentifier(this.spawnLevelIdentifier);
                }
            } else if (sendChangePacket) {
                this.changeLevel(this.server.world.worldEntity.spawnLevelIdentifier, level -> this.getPlayerPosFromTile((Level)level, this.server.world.worldEntity.spawnTile.x, this.server.world.worldEntity.spawnTile.y), true);
            } else {
                this.playerMob.setPos(this.server.world.worldEntity.spawnTile.x * 32 + 16, this.server.world.worldEntity.spawnTile.y * 32 + 16, true);
                this.setLevelIdentifier(this.server.world.worldEntity.spawnLevelIdentifier);
            }
        }
    }

    public Point getPlayerPosFromTile(Level level, int tileX, int tileY) {
        Point pathOffset = this.playerMob.getPathMoveOffset();
        if (!this.playerMob.collidesWith(level, tileX * 32 + pathOffset.x, tileY * 32 + pathOffset.y) && !this.playerMob.collidesWithAnyMob(level, tileX * 32 + pathOffset.x, tileY * 32 + pathOffset.y)) {
            return new Point(tileX * 32 + pathOffset.x, tileY * 32 + pathOffset.y);
        }
        Point pos = PortalObjectEntity.getTeleportDestinationAroundObject(level, this.playerMob, tileX, tileY, true);
        if (pos != null) {
            return pos;
        }
        return new Point(tileX * 32 + pathOffset.x, tileY * 32 + pathOffset.y);
    }

    public void changeIsland(int islandX, int islandY, int dimension) {
        int lastPosX = this.playerMob.getX();
        int lastPosY = this.playerMob.getY();
        LevelIdentifier oldLevel = this.levelIdentifier;
        this.changeIsland(islandX, islandY, dimension, level -> {
            int newPosX = lastPosX;
            if (level.tileWidth > 0) {
                newPosX = GameMath.limit(lastPosX, 160, (level.tileWidth - 5) * 32);
                if (oldLevel.isIslandPosition()) {
                    if (oldLevel.getIslandX() < islandX) {
                        newPosX = 160;
                    } else if (oldLevel.getIslandX() > islandX) {
                        newPosX = (level.tileWidth - 5) * 32;
                    }
                }
            }
            int newPosY = lastPosY;
            if (level.tileHeight > 0) {
                newPosY = GameMath.limit(lastPosY, 160, (level.tileHeight - 5) * 32);
                if (oldLevel.isIslandPosition()) {
                    if (oldLevel.getIslandY() < islandY) {
                        newPosY = 160;
                    } else if (oldLevel.getIslandY() > islandY) {
                        newPosY = (level.tileHeight - 5) * 32;
                    }
                }
            }
            return new Point(newPosX, newPosY);
        }, true);
    }

    @Deprecated
    public void changeIsland(LevelIdentifier identifier) {
        this.changeLevel(identifier);
    }

    public void changeLevel(LevelIdentifier identifier) {
        int lastPosX = this.playerMob.getX();
        int lastPosY = this.playerMob.getY();
        LevelIdentifier oldLevel = this.levelIdentifier;
        this.changeLevel(identifier, level -> {
            int newPosX = lastPosX;
            if (level.tileWidth > 0) {
                newPosX = GameMath.limit(lastPosX, 160, (level.tileWidth - 5) * 32);
                if (oldLevel.isIslandPosition() && identifier.isIslandPosition()) {
                    if (oldLevel.getIslandX() < identifier.getIslandX()) {
                        newPosX = 160;
                    } else if (oldLevel.getIslandX() > identifier.getIslandX()) {
                        newPosX = (level.tileWidth - 5) * 32;
                    }
                }
            }
            int newPosY = lastPosY;
            if (level.tileHeight > 0) {
                newPosY = GameMath.limit(lastPosY, 160, (level.tileHeight - 5) * 32);
                if (oldLevel.isIslandPosition() && identifier.isIslandPosition()) {
                    if (oldLevel.getIslandY() < identifier.getIslandY()) {
                        newPosY = 160;
                    } else if (oldLevel.getIslandY() > identifier.getIslandY()) {
                        newPosY = (level.tileHeight - 5) * 32;
                    }
                }
            }
            return new Point(newPosX, newPosY);
        }, true);
    }

    public void changeIsland(int islandX, int islandY, int dimension, Function<Level, Point> positionSetter, boolean mountFollow) {
        this.changeLevel(new LevelIdentifier(islandX, islandY, dimension), positionSetter, mountFollow);
    }

    @Deprecated
    public void changeIsland(LevelIdentifier identifier, Function<Level, Point> positionSetter, boolean mountFollow) {
        this.changeLevelCheck(identifier, level -> new TeleportResult(true, positionSetter == null ? null : (Point)positionSetter.apply((Level)level)), mountFollow);
    }

    public void changeIslandCheck(int islandX, int islandY, int dimension, Function<Level, TeleportResult> check, boolean mountFollow) {
        this.changeLevelCheck(new LevelIdentifier(islandX, islandY, dimension), check, mountFollow);
    }

    @Deprecated
    public void changeIslandCheck(LevelIdentifier identifier, Function<Level, TeleportResult> check, boolean mountFollow) {
        this.changeLevelCheck(identifier, check, mountFollow);
    }

    public void changeLevel(LevelIdentifier identifier, Function<LevelIdentifier, Level> generator, Function<Level, Point> positionSetter, boolean mountFollow) {
        this.changeLevelCheck(identifier, generator, level -> new TeleportResult(true, positionSetter == null ? null : (Point)positionSetter.apply((Level)level)), mountFollow);
    }

    public void changeLevel(LevelIdentifier identifier, Function<Level, Point> positionSetter, boolean mountFollow) {
        this.changeLevelCheck(identifier, level -> new TeleportResult(true, positionSetter == null ? null : (Point)positionSetter.apply((Level)level)), mountFollow);
    }

    public void changeLevelCheck(LevelIdentifier identifier, Function<Level, TeleportResult> check, boolean mountFollow) {
        this.changeLevelCheck(identifier, null, check, mountFollow);
    }

    public void changeLevelCheck(LevelIdentifier identifier, Function<LevelIdentifier, Level> generator, Function<Level, TeleportResult> check, boolean mountFollow) {
        if (this.isSamePlace(identifier) && check != null) {
            TeleportResult result = check.apply(this.getLevel());
            if (result.isValid && result.targetPosition != null) {
                if (result.newDestination == null || result.newDestination.equals(identifier)) {
                    RegionPositionGetter lastRegionPos = this.playerMob.saveRegionPosition();
                    this.playerMob.clearUsingObject();
                    this.playerMob.setPos(result.targetPosition.x, result.targetPosition.y, true);
                    this.server.network.sendToClientsWithAnyRegion(new PacketPlayerMovement(this, true), this.playerMob.getRegionPositionsCombined(lastRegionPos));
                    return;
                }
                identifier = result.newDestination;
                check = null;
            }
        }
        this.combineNewStats();
        System.out.println("Changed " + this.getName() + " level to " + identifier);
        boolean isValid = check == null;
        Point pos = null;
        Level oldLevel = this.getLevel();
        LevelIdentifier newDestination = null;
        boolean isLoaded = this.server.world.levelManager.isLoaded(identifier);
        if (!isLoaded) {
            this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, identifier, mountFollow));
        }
        LevelIdentifier newLevelIdentifier = identifier;
        Level newLevel = this.server.world.getLevel(identifier, generator == null ? null : () -> (Level)generator.apply(newLevelIdentifier));
        if (check != null) {
            TeleportResult result = check.apply(newLevel);
            isValid = result.isValid;
            newDestination = result.newDestination;
            pos = result.targetPosition;
        }
        if (!isValid) {
            if (!isLoaded) {
                this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, oldLevel.getIdentifier(), mountFollow));
            }
            return;
        }
        if (newDestination != null && !newDestination.equals(identifier)) {
            LevelIdentifier newDestinationIdentifier = identifier = newDestination;
            newLevel = this.server.world.getLevel(identifier, generator == null ? null : () -> (Level)generator.apply(newDestinationIdentifier));
            if (!isLoaded) {
                this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, identifier, mountFollow));
            }
        }
        if (isLoaded) {
            this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, identifier, mountFollow));
        }
        this.setLevelIdentifier(identifier);
        this.playerMob.moveX = 0.0f;
        this.playerMob.moveY = 0.0f;
        this.reset();
        if (pos != null) {
            this.playerMob.setPos(pos.x, pos.y, true);
            this.server.network.sendToAllClients(new PacketPlayerMovement(this, true));
        }
        Mob mount = this.playerMob.getMount();
        if (mountFollow && mount != null) {
            oldLevel.entityManager.changeMobLevel(mount, newLevel, pos != null ? pos.x : mount.getX(), pos != null ? pos.y : mount.getY(), true);
        } else {
            this.playerMob.dismount();
        }
        this.saveClient();
        this.playerMob.boomerangs.clear();
        this.playerMob.toolHits.clear();
        this.playerMob.setLevel(newLevel);
        if (oldLevel != newLevel) {
            this.playerMob.onLevelChanged();
        }
        this.playerMob.setUniqueID(this.slot);
        LevelIdentifier newIdentifier = newLevel.getIdentifier();
        this.tickDiscoveredBiomes(newLevel, true);
        Level finalNewLevel = newLevel;
        JournalChallengeRegistry.handleListeners(this, LevelChangedJournalChallengeListener.class, challenge -> challenge.onLevelChanged(this, oldLevel, finalNewLevel));
        if (newIdentifier.isIslandPosition() && newIdentifier.getIslandDimension() == -2 && this.achievementsLoaded()) {
            this.achievements().GETTING_HOT.markCompleted(this);
        }
        int playerRegionX = newLevel.regionManager.getRegionCoordByTile(this.playerMob.getTileX());
        int playerRegionY = newLevel.regionManager.getRegionCoordByTile(this.playerMob.getTileY());
        ArrayList<Point> regions = new ArrayList<Point>(9);
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                Region region = newLevel.regionManager.getRegion(playerRegionX + x, playerRegionY + y, true);
                if (region == null) continue;
                regions.add(new Point(region.regionX, region.regionY));
                this.addLoadedRegion(newLevel, region.regionX, region.regionY, true);
            }
        }
        this.sendPacket(new PacketLevelData(newLevel, this, regions));
    }

    @Override
    public LevelIdentifier getLevelIdentifier() {
        return this.levelIdentifier;
    }

    public boolean isSupporter(boolean requestUpdate) {
        return this.hasDLC(DLC.SUPPORTER_PACK, requestUpdate);
    }

    public boolean hasDLC(DLC dlc, boolean requestUpdate) {
        boolean hasDLC = this.installedDLC.containsKey(dlc.getID());
        if (!hasDLC && requestUpdate) {
            this.sendPacket(new PacketRequestClientInstalledDLC());
        }
        return hasDLC;
    }

    public void tickDiscoveredBiomes(Level level, boolean markNewJournalEntry) {
        Region region = level.regionManager.getRegionByTile(this.playerMob.getTileX(), this.playerMob.getTileY(), false);
        if (region != null) {
            int regionTileY;
            int regionTileX = this.playerMob.getTileX() - region.tileXOffset;
            Biome biome = region.biomeLayer.getBiomeByRegion(regionTileX, regionTileY = this.playerMob.getTileY() - region.tileYOffset);
            if (biome == BiomeRegistry.UNKNOWN) {
                biome = level.baseBiome;
            }
            this.markDiscoveredBiome(biome, level.getIdentifier(), true);
            if (region.isPirateVillageRegion) {
                this.markDiscoveredBiome(BiomeRegistry.PIRATE_VILLAGE, level.getIdentifier(), markNewJournalEntry);
            }
        }
    }

    public void markDiscoveredBiome(Biome biome, LevelIdentifier levelIdentifier, boolean markNewJournalEntry) {
        this.newStats.biomes_visited.markBiomeVisited(biome);
        Iterable<JournalEntry> journalEntries = JournalRegistry.getEntriesForBiome(biome.getID());
        boolean discoveredAnyNew = false;
        for (JournalEntry journalEntry : journalEntries) {
            if (!journalEntry.canDiscoverWithLevelIdentifier(levelIdentifier) || this.characterStats.discovered_journal_entries.isJournalDiscovered(journalEntry.getStringID())) continue;
            this.newStats.discovered_journal_entries.markDiscoveredJournal(journalEntry.getStringID());
            discoveredAnyNew = true;
            if (!markNewJournalEntry) continue;
            this.hasNewJournalEntry = true;
            this.sendPacket(new PacketJournalUpdated(journalEntry.getID()));
        }
        if (discoveredAnyNew) {
            this.forceCombineNewStats();
        }
    }

    public void saveClient() {
        this.server.world.savePlayer(this);
    }

    public PlayerTeam getPlayerTeam() {
        if (this.getTeamID() == -1) {
            return null;
        }
        PlayerTeam team = this.server.world.getTeams().getTeam(this.getTeamID());
        if (team != null && team.isMember(this.authentication)) {
            return team;
        }
        return null;
    }

    public void addQuest(Quest quest, boolean isNew) {
        if (this.quests.containsKey(quest)) {
            return;
        }
        this.quests.put(quest, isNew && this.trackNewQuests);
    }

    public void removeQuest(Quest quest) {
        this.quests.remove(quest);
    }

    public Quest getQuest(int questUniqueID) {
        return this.quests.keySet().stream().filter(q -> q.getUniqueID() == questUniqueID).findFirst().orElse(null);
    }

    public boolean hasQuest(Quest quest) {
        return this.quests.containsKey(quest);
    }

    public boolean setTrackedQuest(int questUniqueID, boolean tracked) {
        for (Map.Entry<Quest, Boolean> entry : this.quests.entrySet()) {
            if (entry.getKey().getUniqueID() != questUniqueID) continue;
            entry.setValue(tracked);
            return true;
        }
        return false;
    }

    public HashMap<Quest, Boolean> getQuests() {
        return this.quests;
    }

    public void sendConnectingMessage() {
        if (this.sentConnectingMessage) {
            return;
        }
        this.server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("misc", "playerconnecting", "player", this.getName())));
        this.sentConnectingMessage = true;
    }

    public void sendJoinedMessage() {
        PlayerTeam team;
        ServerClient localClient;
        if (this.sentJoinedMessage) {
            return;
        }
        this.server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("misc", "playerjoined", "player", this.getName())));
        if (!Settings.serverMOTD.isEmpty()) {
            this.sendChatMessage(GameColor.PURPLE.getColorCode() + Settings.serverMOTD);
        }
        if (this.playerMob.hasInvincibility) {
            this.sendChatMessage(GameColor.RED.getColorCode() + "You have invincibility enabled");
        }
        this.sentJoinedMessage = true;
        if (this.server.isHosted() && this.server.streamClients().allMatch(c -> c == this || c.networkInfo == null) && (localClient = this.server.getLocalServerClient()) != null && localClient != this && ((team = localClient.getPlayerTeam()) == null || team.getMemberCount() <= 1)) {
            localClient.sendChatMessage(new LocalMessage("misc", "teamcooptip"));
        }
    }

    public void sendChatMessage(String message) {
        this.sendPacket(new PacketChatMessage(message));
    }

    public void sendChatMessage(GameMessage message) {
        this.sendPacket(new PacketChatMessage(message));
    }

    public void sendUniqueFloatText(int levelX, int levelY, GameMessage message, String uniqueType, int hoverTime) {
        this.sendPacket(new PacketUniqueFloatText(levelX, levelY, message, uniqueType, hoverTime));
    }

    public void sendPacket(Packet packet) {
        this.server.network.sendPacket(packet, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void submitSpawnPacket(PacketSpawnPlayer p) {
        if (this.needAppearance()) {
            this.server.disconnectClient(this, PacketDisconnect.Code.MISSING_APPEARANCE);
            System.out.println("Disconnected client " + this.authentication + " for missing appearance.");
            return;
        }
        if (this.playerMob == null) {
            return;
        }
        this.sendPacket(new PacketWorldData(this.server.world.worldEntity));
        this.sendPacket(new PacketSpawnPlayerReceipt(false));
        this.refreshAFKTimer();
        if (!this.hasSpawned) {
            this.playerMob.refreshSpawnTime();
        }
        this.hasSpawned = true;
        this.spawnedCheckTimer = 0L;
        this.updateInventoryContainer();
        Level level = this.server.world.getLevel(this);
        this.tickDiscoveredBiomes(level, false);
        JournalChallengeRegistry.handleListeners(this, LevelChangedJournalChallengeListener.class, challenge -> challenge.onLevelChanged(this, null, level));
        Object object = level.entityManager.lock;
        synchronized (object) {
            for (LevelEvent levelEvent : level.entityManager.events.regionList.getInNoRegion()) {
                if (!levelEvent.isNetworkImportant() || levelEvent.isOver()) continue;
                this.sendPacket(new PacketLevelEvent(levelEvent));
            }
            for (LevelEvent levelEvent : level.entityManager.events.regionList.getInRegionTileByTile(this.playerMob.getTileX(), this.playerMob.getTileY())) {
                if (!levelEvent.isNetworkImportant() || levelEvent.isOver()) continue;
                this.sendPacket(new PacketLevelEvent(levelEvent));
            }
        }
        this.sendJoinedMessage();
        this.server.network.sendToAllClients(new PacketSpawnPlayer(this));
    }

    public void applyLoadedCharacterPacket(PacketSelectedCharacter packet) {
        this.characterUniqueID = packet.characterUniqueID;
        if (packet.networkData != null) {
            this.newStats = new PlayerStats(false, EmptyStats.Mode.WRITE_ONLY);
            this.newStats.resetCombine();
            this.newStats.cleanAll();
            packet.networkData.applyToPlayer(this.playerMob);
            if (!packet.networkData.applyToStats(this.characterStats)) {
                this.characterStats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
            }
        }
        this.server.usedNames.put(this.authentication, this.getName());
        this.server.world.savePlayer(this);
        this.server.network.sendToAllClients(new PacketPlayerAppearance(this));
        this.needAppearance = false;
        this.submittedCharacter = true;
    }

    public void applyAppearancePacket(PacketPlayerAppearance packet) {
        this.characterUniqueID = packet.characterUniqueID;
        this.playerMob.applyAppearancePacket(packet);
        this.server.usedNames.put(this.authentication, this.getName());
        this.server.world.savePlayer(this);
        this.server.network.sendToAllClients(packet);
        this.needAppearance = false;
        this.submittedCharacter = true;
    }

    public void applyClientInstalledDLCPacket(PacketClientInstalledDLC packet) {
        for (int i = 0; i < packet.installedDLC.length; ++i) {
            int id = packet.installedDLC[i];
            this.installedDLC.put(id, DLC.DLCs.get(id));
        }
    }

    public boolean needPlayerStats() {
        return this.totalStats == null;
    }

    public void applyClientStatsPacket(PacketClientStats packet) {
        if (!this.needPlayerStats()) {
            return;
        }
        this.totalStats = packet.stats;
        this.achievements = packet.achievements;
        this.totalStats.cleanAll();
        if (this.playerMob.getInv().equipment.getTrinketSlotsSize() > 4) {
            this.achievements().MAGICAL_DROP.markCompleted(this);
        }
        if (this.playerMob.getInv().equipment.getTotalSets() >= 4) {
            this.achievements().GET_4_ITEM_SETS.markCompleted(this);
        }
    }

    public PlayerStats playerStats() {
        return this.totalStats;
    }

    public PlayerStats characterStats() {
        return this.characterStats;
    }

    public void resetStats() {
        this.characterStats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        if (!this.needPlayerStats()) {
            this.totalStats = new PlayerStats(false, EmptyStats.Mode.READ_AND_WRITE);
        }
        this.newStats = new PlayerStats(false, EmptyStats.Mode.WRITE_ONLY);
    }

    public void markObtainItem(String itemStringID) {
        boolean isPetItem;
        this.newStats.items_obtained.addItem(itemStringID);
        JournalChallengeRegistry.handleListeners(this, ItemObtainedJournalChallengeListener.class, challenge -> challenge.onNewItemObtained(this, itemStringID));
        if (this.achievementsLoaded() && !this.achievements().GET_PET.isCompleted() && (isPetItem = AchievementManager.GET_PET_ITEMS.contains(itemStringID))) {
            this.achievements().GET_PET.markCompleted(this);
        }
    }

    public boolean achievementsLoaded() {
        return this.achievements != null;
    }

    public AchievementManager achievements() {
        return this.achievements;
    }

    public PermissionLevel getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean setPermissionLevel(PermissionLevel level, boolean sendChatMessage) {
        if (this.permissionLevel == level) {
            return false;
        }
        this.permissionLevel = level;
        if (sendChatMessage) {
            LocalMessage msg = new LocalMessage("misc", "permchange");
            msg.addReplacement("perm", level.name);
            this.sendChatMessage(msg);
        }
        this.sendPacket(new PacketPermissionUpdate(level));
        return true;
    }

    public void updateInventoryContainer() {
        this.inventoryContainer = new Container(this, 0);
    }

    public Container getContainer() {
        if (this.openContainer != null) {
            return this.openContainer;
        }
        return this.inventoryContainer;
    }

    public void openContainer(Container container) {
        if (this.openContainer != null) {
            this.openContainer.onClose();
        }
        this.openContainer = container;
        this.openContainer.init();
    }

    public void closeContainer(boolean sendPacket) {
        if (this.openContainer != null) {
            if (sendPacket) {
                this.sendPacket(new PacketCloseContainer());
            }
            this.openContainer.onClose();
            this.openContainer = null;
        }
    }

    public void addQuestDrops(List<InventoryItem> drops, Mob mob, GameRandom random) {
        for (Quest quest : this.quests.keySet()) {
            LootTable extraMobDrops = quest.getExtraMobDrops(this, mob);
            if (extraMobDrops == null) continue;
            extraMobDrops.addItems(drops, random, 1.0f, mob, this);
        }
    }

    public Server getServer() {
        return this.server;
    }

    public Level getLevel() {
        return this.getServer().world.getLevel(this);
    }

    public void submitOutPacket(NetworkPacket packet) {
        this.packetsOutBytes += (long)packet.getByteSize();
        ++this.packetsOutTotal;
    }

    public long getPacketsOutTotal() {
        return this.packetsOutTotal;
    }

    public long getPacketsOutBytes() {
        return this.packetsOutBytes;
    }

    public void submitInPacket(NetworkPacket packet) {
        this.packetsInBytes += (long)packet.getByteSize();
        ++this.packetsInTotal;
        this.lastReceivedPacketTime = System.currentTimeMillis();
    }

    public long getPacketsInTotal() {
        return this.packetsInTotal;
    }

    public long getPacketsInBytes() {
        return this.packetsInBytes;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.networkInfo != null) {
            this.networkInfo.closeConnection();
        }
        this.closeContainer(false);
        if (this.playerMob != null) {
            this.playerMob.remove();
            this.playerMob.dispose();
        }
    }

    public static ServerClient getNewPlayerClient(Server server, long sessionID, NetworkInfo networkInfo, int slot, long authentication) {
        ServerClient out = new ServerClient(server, sessionID, networkInfo, slot, authentication, null);
        out.spawnLevelIdentifier = server.world.worldEntity.spawnLevelIdentifier;
        out.spawnTile = server.world.worldEntity.spawnTile;
        out.setLevelIdentifier(out.spawnLevelIdentifier);
        out.playerMob = new PlayerMob(authentication, out);
        out.playerMob.getInv().giveStarterItems();
        out.playerMob.setPos(out.spawnTile.x * 32 + 16, out.spawnTile.y * 32 + 16, true);
        out.playerMob.setUniqueID(slot);
        out.needAppearance = true;
        out.setTeamID(-1);
        out.updateInventoryContainer();
        out.playerMob.setLevel(server.world.getLevel(out.levelIdentifier));
        out.playerMob.init();
        return out;
    }

    private static class ExpectedPing {
        public final int responseKey;
        public final long timeSent;
        public ExpectedPing next;

        public ExpectedPing(int responseKey, long timeSent, ExpectedPing next) {
            this.responseKey = responseKey;
            this.timeSent = timeSent;
            this.next = next;
        }
    }
}

