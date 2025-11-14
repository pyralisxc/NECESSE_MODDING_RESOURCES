/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRemoveSettlementData;
import necesse.engine.network.packet.PacketRequestSettlementData;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.engine.world.WorldFile;
import necesse.engine.world.worldData.WorldData;
import necesse.entity.manager.WorldLevelUnloadedEntityComponent;
import necesse.entity.manager.WorldRegionLoadedEntityComponent;
import necesse.entity.manager.WorldRegionUnloadedEntityComponent;
import necesse.entity.manager.WorldSavedEntityComponent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBoundsManager;
import necesse.level.maps.regionSystem.Region;

public class SettlementsWorldData
extends WorldData
implements WorldSavedEntityComponent,
WorldRegionLoadedEntityComponent,
WorldRegionUnloadedEntityComponent,
WorldLevelUnloadedEntityComponent {
    private final HashMap<LevelIdentifier, SettlementsLevelRegionData> regionData = new HashMap();
    private final HashMap<Integer, SettlementData> settlements = new HashMap();
    private final HashMap<Integer, SettlementData> loadedSettlements = new HashMap();
    private final TreeSet<Integer> clientRequestQueue = new TreeSet();
    private final HashMap<Integer, Long> clientRequestedTimes = new HashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onWorldSaved() {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            this.saveSettlements();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLevelRegionLoaded(Level level, Region region) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            if (level.isServer()) {
                SettlementsLevelRegionData regionData = this.getRegionData(level.getIdentifier());
                int settlementUniqueID = regionData.regionSettlements.getOrDefault(region.regionX, region.regionY, 0);
                if (settlementUniqueID != 0) {
                    this.getOrLoadServerData(settlementUniqueID);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLevelRegionUnloaded(Level level, Region region) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            NetworkSettlementData settlement;
            SettlementsLevelRegionData regionData = this.getRegionData(level.getIdentifier());
            int settlementUniqueID = regionData.regionSettlements.getOrDefault(region.regionX, region.regionY, 0);
            if (settlementUniqueID != 0 && (settlement = this.getNetworkData(settlementUniqueID)) != null) {
                Iterable<Point> regionPositions = SettlementBoundsManager.getRegionPositions(settlement.getTileX(), settlement.getTileY(), settlement.getFlagTier());
                boolean hasAnyRegionsLoaded = false;
                for (Point regionPosition : regionPositions) {
                    if (!level.regionManager.isRegionLoaded(regionPosition.x, regionPosition.y)) continue;
                    hasAnyRegionsLoaded = true;
                    break;
                }
                if (!hasAnyRegionsLoaded) {
                    System.out.println(level.getHostString() + " unloading settlement with uniqueID: " + settlementUniqueID + " at " + settlement.getTileX() + "x" + settlement.getTileY() + " because no regions are loaded within the settlement");
                    this.unloadSettlement(settlementUniqueID, true);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onLevelUnloaded(LevelIdentifier identifier) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            HashSet<Integer> unloadUniqueIDs = new HashSet<Integer>();
            for (SettlementData value : this.loadedSettlements.values()) {
                if (!value.networkData.level.getIdentifier().equals(identifier)) continue;
                unloadUniqueIDs.add(value.networkData.uniqueID);
            }
            Iterator<SettlementData> iterator = unloadUniqueIDs.iterator();
            while (iterator.hasNext()) {
                int settlementUniqueID = (Integer)((Object)iterator.next());
                this.unloadSettlement(settlementUniqueID, true);
            }
        }
    }

    public void saveSettlements() {
        for (SettlementData settlementData : this.loadedSettlements.values()) {
            this.saveSettlementData(settlementData.networkData, settlementData.serverData);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SaveData settlementsSave = new SaveData("SETTLEMENTS");
            for (SettlementData data : this.settlements.values()) {
                CachedSettlementData cacheData = data.networkData != null ? data.networkData.getCacheData(false) : data.cachedData;
                settlementsSave.addSaveData(this.getCacheSave(cacheData));
            }
            if (!settlementsSave.isEmpty()) {
                save.addSaveData(settlementsSave);
            }
        }
    }

    private SaveData getCacheSave(CachedSettlementData cacheData) {
        SaveData settlementSave = new SaveData("SETTLEMENT");
        settlementSave.addUnsafeString("levelIdentifier", cacheData.levelIdentifier.stringID);
        settlementSave.addInt("uniqueID", cacheData.uniqueID);
        cacheData.addSaveData(settlementSave);
        return settlementSave;
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData settlementsSave = save.getFirstLoadDataByName("SETTLEMENTS");
        if (settlementsSave != null) {
            for (LoadData settlementSave : settlementsSave.getLoadDataByName("SETTLEMENT")) {
                try {
                    String levelIdentifierString = settlementSave.getUnsafeString("levelIdentifier", null, false);
                    if (levelIdentifierString == null) {
                        throw new LoadDataException("Found invalid settlement levelIdentifier");
                    }
                    LevelIdentifier levelIdentifier = new LevelIdentifier(levelIdentifierString);
                    int settlementUniqueID = settlementSave.getInt("uniqueID", 0, false);
                    if (settlementUniqueID == 0) {
                        throw new LoadDataException("Found invalid settlement uniqueID");
                    }
                    SettlementData settlementData = new SettlementData();
                    settlementData.cachedData = new CachedSettlementData(levelIdentifier, settlementUniqueID, settlementSave);
                    this.settlements.put(settlementUniqueID, settlementData);
                    this.updateSettlement(settlementData.cachedData);
                }
                catch (Exception e) {
                    System.err.println("Could not load settlement cache from save:");
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tick() {
        super.tick();
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            HashSet<Integer> uniqueIDsToUnload = new HashSet<Integer>();
            LinkedList<SettlementData> settlementsToDisband = new LinkedList<SettlementData>();
            for (SettlementData settlementData : this.loadedSettlements.values()) {
                if (settlementData.networkData.tickIsRegionsUnloadedCheck()) {
                    uniqueIDsToUnload.add(settlementData.networkData.uniqueID);
                    continue;
                }
                settlementData.networkData.tickSettlementFlagBuff();
                if (!this.isServer()) continue;
                settlementData.networkData.serverTick();
                if (settlementData.networkData.isDisbanded()) {
                    settlementsToDisband.add(settlementData);
                    continue;
                }
                settlementData.serverData.serverTick();
            }
            for (SettlementData settlementData : settlementsToDisband) {
                settlementData.networkData.onDisbanded();
                settlementData.serverData.onDisbanded();
                this.getServer().network.sendToClientsWithAnyRegion((Packet)new PacketRemoveSettlementData(settlementData.serverData.uniqueID), settlementData.serverData.getLevel(), settlementData.networkData.getLoadedRegionRectangle());
                this.deleteSettlement(settlementData.serverData.uniqueID);
            }
            Iterator<SettlementData> iterator = uniqueIDsToUnload.iterator();
            while (iterator.hasNext()) {
                int settlementUniqueID = (Integer)((Object)iterator.next());
                this.unloadSettlement(settlementUniqueID, true);
            }
            if (this.isClient()) {
                while (!this.clientRequestQueue.isEmpty()) {
                    int settlementUniqueID = this.clientRequestQueue.pollFirst();
                    this.getClient().network.sendPacket(new PacketRequestSettlementData(settlementUniqueID));
                    this.clientRequestedTimes.put(settlementUniqueID, this.getLocalTime());
                }
                if (!this.clientRequestedTimes.isEmpty()) {
                    HashSet<Integer> removes = new HashSet<Integer>();
                    for (Map.Entry<Integer, Long> entry : this.clientRequestedTimes.entrySet()) {
                        long timeSinceRequest = this.getLocalTime() - entry.getValue();
                        if (timeSinceRequest < 5000L) continue;
                        int settlementUniqueID = entry.getKey();
                        this.clientRequestQueue.add(settlementUniqueID);
                        removes.add(settlementUniqueID);
                    }
                    Iterator<Map.Entry<Integer, Long>> iterator2 = removes.iterator();
                    while (iterator2.hasNext()) {
                        int settlementUniqueID = (Integer)((Object)iterator2.next());
                        this.clientRequestedTimes.remove(settlementUniqueID);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void migrateFromOldSettlementSystem(OneWorldMigration migration, LevelIdentifier oldLevelIdentifier, Level newLevel, LoadData settlementLayerSave, LoadData settlementLevelDataSave, Point tileOffset, Point positionOffset) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int settlementUniqueID = this.getNewSettlementUniqueID();
            SettlementData settlementData = new SettlementData();
            Point flagTile = settlementLevelDataSave.getPoint("entityPos", null, false);
            if (flagTile != null) {
                flagTile.translate(tileOffset.x, tileOffset.y);
                settlementData.networkData = new NetworkSettlementData(newLevel, settlementUniqueID, flagTile.x, flagTile.y);
                settlementData.networkData.setFlagTier(SettlementBoundsManager.flagTiers.length - 2);
                settlementData.serverData = new ServerSettlementData(this, settlementData.networkData, settlementUniqueID);
                settlementData.networkData.setServerData(settlementData.serverData);
                try {
                    settlementData.networkData.applyLoadData(settlementLayerSave, migration, tileOffset.x, tileOffset.y);
                }
                catch (Exception e) {
                    GameLog.warn.println("Error migrating old settlement layer from " + oldLevelIdentifier + ":");
                    e.printStackTrace(GameLog.warn);
                }
                try {
                    settlementData.serverData.applyLoadData(settlementLevelDataSave, migration, tileOffset.x, tileOffset.y);
                }
                catch (Exception e) {
                    GameLog.warn.println("Error migrating old settlement level data from " + oldLevelIdentifier + ":");
                    e.printStackTrace(GameLog.warn);
                }
                System.out.println("Successfully migrated old settlement at " + oldLevelIdentifier + " to new system with uniqueID: " + settlementUniqueID);
                this.settlements.put(settlementUniqueID, settlementData);
                this.loadedSettlements.put(settlementUniqueID, settlementData);
                this.updateSettlement(settlementData.networkData);
                migration.setOldSettlementLevelIdentifier(oldLevelIdentifier, settlementUniqueID);
            } else {
                GameLog.warn.println("Could not migrate old settlement from " + oldLevelIdentifier + " because flag tile was not found");
            }
        }
    }

    public int getNewSettlementUniqueID() {
        int uniqueID = GameRandom.globalRandom.nextInt();
        while (uniqueID == 0 || this.settlements.containsKey(uniqueID)) {
            uniqueID = GameRandom.globalRandom.nextInt();
        }
        return uniqueID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SettlementsLevelRegionData getRegionData(LevelIdentifier levelIdentifier) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            return this.regionData.compute(levelIdentifier, (key, value) -> {
                if (value == null) {
                    return new SettlementsLevelRegionData();
                }
                return value;
            });
        }
    }

    public int getSettlementUniqueIDAtRegion(LevelIdentifier levelIdentifier, int regionX, int regionY) {
        return this.getRegionData((LevelIdentifier)levelIdentifier).regionSettlements.getOrDefault(regionX, regionY, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean canPlaceSettlementFlagAt(LevelIdentifier levelIdentifier, int tileX, int tileY, int flagTier) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementsLevelRegionData regionData = this.getRegionData(levelIdentifier);
            Rectangle regionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(tileX, tileY, flagTier);
            int foundSettlementUniqueID = 0;
            for (int regionX = regionRectangle.x; regionX < regionRectangle.x + regionRectangle.width; ++regionX) {
                for (int regionY = regionRectangle.y; regionY < regionRectangle.y + regionRectangle.height; ++regionY) {
                    int settlementUniqueID = regionData.regionSettlements.getOrDefault(regionX, regionY, 0);
                    if (settlementUniqueID == 0) continue;
                    if (foundSettlementUniqueID != 0 && foundSettlementUniqueID != settlementUniqueID) {
                        return false;
                    }
                    foundSettlementUniqueID = settlementUniqueID;
                }
            }
            if (foundSettlementUniqueID != 0) {
                // MONITOREXIT @DISABLED, blocks:[0, 1, 5] lbl18 : MonitorExitStatement: MONITOREXIT : var5_5
                SettlementData settlement = this.getOrLoadData(foundSettlementUniqueID);
                return settlement == null || !settlement.networkData.hasFlag();
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSettlement(ServerSettlementData settlement) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            Iterable<Point> regionPositions = settlement.boundsManager.getRegionPositions();
            this.updateSettlement(settlement.getLevel().getIdentifier(), settlement.uniqueID, regionPositions);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSettlement(NetworkSettlementData data) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            Iterable<Point> regionPositions = SettlementBoundsManager.getRegionPositions(data.getTileX(), data.getTileY(), data.getFlagTier());
            this.updateSettlement(data.level.getIdentifier(), data.uniqueID, regionPositions);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSettlement(CachedSettlementData data) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            Iterable<Point> regionPositions = SettlementBoundsManager.getRegionPositions(data.getTileX(), data.getTileY(), data.getFlagTier());
            this.updateSettlement(data.levelIdentifier, data.uniqueID, regionPositions);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void updateSettlement(LevelIdentifier levelIdentifier, int settlementUniqueID, Iterable<Point> regionPositions) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementsLevelRegionData regionData = this.getRegionData(levelIdentifier);
            PointHashSet lastBounds = regionData.settlementRegions.get(settlementUniqueID);
            if (lastBounds != null) {
                for (Point region : lastBounds) {
                    regionData.regionSettlements.remove(region.x, region.y);
                }
            }
            PointHashSet newRegionPositions = new PointHashSet();
            for (Point regionPosition : regionPositions) {
                regionData.regionSettlements.put(regionPosition.x, regionPosition.y, settlementUniqueID);
                newRegionPositions.add(regionPosition.x, regionPosition.y);
            }
            regionData.settlementRegions.put(settlementUniqueID, newRegionPositions);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clearSettlementRegions(NetworkSettlementData settlement) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementsLevelRegionData regionData = this.getRegionData(settlement.level.getIdentifier());
            PointHashSet lastBounds = regionData.settlementRegions.get(settlement.uniqueID);
            if (lastBounds != null) {
                for (Point region : lastBounds) {
                    regionData.regionSettlements.remove(region.x, region.y);
                }
            }
            regionData.settlementRegions.remove(settlement.uniqueID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SettlementData getSettlementData(int uniqueID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            return this.settlements.get(uniqueID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<CachedSettlementData> collectCachedSettlements(Predicate<CachedSettlementData> filer) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            return this.settlements.values().stream().map(data -> {
                if (data.networkData != null) {
                    return data.networkData.getCacheData(true);
                }
                return data.cachedData;
            }).filter(filer).collect(Collectors.toList());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CachedSettlementData getCachedData(int uniqueID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementData settlementData = this.getSettlementData(uniqueID);
            if (settlementData != null) {
                if (settlementData.networkData != null) {
                    return settlementData.networkData.getCacheData(true);
                }
                return settlementData.cachedData;
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NetworkSettlementData getNetworkData(int uniqueID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementData settlementData = this.getSettlementData(uniqueID);
            return settlementData == null ? null : settlementData.networkData;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerSettlementData getServerData(int uniqueID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementData settlementData = this.getSettlementData(uniqueID);
            return settlementData == null ? null : settlementData.serverData;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasSettlementAtTile(Level level, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int regionX = GameMath.getRegionCoordByTile(tileX);
            int regionY = GameMath.getRegionCoordByTile(tileY);
            return this.getRegionData((LevelIdentifier)level.getIdentifier()).regionSettlements.getOrDefault(regionX, regionY, 0) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getSettlementUniqueIDAtTile(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int regionX = GameMath.getRegionCoordByTile(tileX);
            int regionY = GameMath.getRegionCoordByTile(tileY);
            return this.getRegionData((LevelIdentifier)levelIdentifier).regionSettlements.getOrDefault(regionX, regionY, 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CachedSettlementData getCachedDataAtTile(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int uniqueID = this.getSettlementUniqueIDAtTile(levelIdentifier, tileX, tileY);
            if (uniqueID != 0) {
                return this.getCachedData(uniqueID);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NetworkSettlementData getNetworkDataAtTile(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int uniqueID = this.getSettlementUniqueIDAtTile(levelIdentifier, tileX, tileY);
            if (uniqueID != 0) {
                return this.getNetworkData(uniqueID);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerSettlementData getServerDataAtTile(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int uniqueID = this.getSettlementUniqueIDAtTile(levelIdentifier, tileX, tileY);
            if (uniqueID != 0) {
                return this.getServerData(uniqueID);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerSettlementData getOrLoadServerDataAtTile(LevelIdentifier levelIdentifier, int tileX, int tileY) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int uniqueID = this.getSettlementUniqueIDAtTile(levelIdentifier, tileX, tileY);
            if (uniqueID != 0) {
                return this.getOrLoadServerData(uniqueID);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateOwnerTeamID(long ownerAuth, int teamID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            for (SettlementData data : this.settlements.values()) {
                if (data.cachedData != null) {
                    if (data.cachedData.getOwnerAuth() != ownerAuth) continue;
                    data.cachedData.updateOwnerTeamID(teamID);
                    continue;
                }
                if (data.networkData.getOwnerAuth() != ownerAuth) continue;
                data.networkData.updateOwnerVariables();
                data.networkData.markDirty(false);
            }
        }
    }

    protected void saveSettlementData(NetworkSettlementData networkData, ServerSettlementData serverData) {
        WorldFile file = this.getServer().world.fileSystem.getSettlementFile(serverData.uniqueID);
        SaveData saveData = new SaveData("");
        SaveData networkSave = new SaveData("NETWORK");
        networkData.addSaveData(networkSave);
        saveData.addSaveData(networkSave);
        SaveData serverSave = new SaveData("SERVER");
        serverData.addSaveData(serverSave);
        saveData.addSaveData(serverSave);
        saveData.saveScript(file);
    }

    protected boolean loadSettlementData(NetworkSettlementData networkData, ServerSettlementData serverData) {
        try {
            WorldFile file = this.getServer().world.fileSystem.getSettlementFile(serverData.uniqueID);
            if (file.exists()) {
                LoadData saveData = new LoadData(file);
                LoadData networkDataSave = saveData.getFirstLoadDataByName("NETWORK");
                if (networkDataSave == null) {
                    throw new LoadDataException("Settlement file for uniqueID: " + serverData.uniqueID + " does not contain network data.");
                }
                networkData.applyLoadData(networkDataSave, null, 0, 0);
                serverData.ensureRegionsLoaded(true);
                LoadData serverDataSave = saveData.getFirstLoadDataByName("SERVER");
                if (serverDataSave == null) {
                    throw new LoadDataException("Settlement file for uniqueID: " + serverData.uniqueID + " does not contain server data.");
                }
                serverData.applyLoadData(serverDataSave, null, 0, 0);
            } else {
                System.err.println("Could not find settlement file for uniqueID: " + serverData.uniqueID);
            }
        }
        catch (Exception e) {
            System.err.println("Error loading settlement data for uniqueID: " + serverData.uniqueID);
            e.printStackTrace();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SettlementData getOrCreateLoadedData(Level level, int tileX, int tileY) {
        if (level.isClient()) {
            throw new IllegalStateException("Cannot load settlement data on the client side");
        }
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            int uniqueID = this.getSettlementUniqueIDAtTile(level.getIdentifier(), tileX, tileY);
            if (uniqueID != 0) {
                SettlementData settlement = this.settlements.get(uniqueID);
                if (settlement != null) {
                    if (settlement.networkData == null) {
                        settlement.networkData = new NetworkSettlementData(level, uniqueID, settlement.cachedData.getTileX(), settlement.cachedData.getTileY());
                        if (this.isServer()) {
                            settlement.serverData = new ServerSettlementData(this, settlement.networkData, uniqueID);
                            settlement.networkData.setServerData(settlement.serverData);
                            settlement.cachedData = null;
                            this.loadedSettlements.put(uniqueID, settlement);
                            this.loadSettlementData(settlement.networkData, settlement.serverData);
                        } else {
                            this.loadedSettlements.put(uniqueID, settlement);
                        }
                    }
                } else {
                    GameLog.warn.println("Could not find settlement data when expected at " + tileX + "x" + tileY);
                    settlement = new SettlementData();
                    settlement.networkData = new NetworkSettlementData(level, uniqueID, tileX, tileY);
                    if (this.isServer()) {
                        settlement.serverData = new ServerSettlementData(this, settlement.networkData, uniqueID);
                        settlement.networkData.setServerData(settlement.serverData);
                    }
                    this.loadedSettlements.put(uniqueID, settlement);
                    this.settlements.put(uniqueID, settlement);
                    this.updateSettlement(settlement.networkData);
                }
                return settlement;
            }
            int newUniqueID = this.getNewSettlementUniqueID();
            SettlementData settlement = new SettlementData();
            settlement.networkData = new NetworkSettlementData(level, newUniqueID, tileX, tileY);
            if (this.isServer()) {
                settlement.serverData = new ServerSettlementData(this, settlement.networkData, newUniqueID);
                settlement.networkData.setServerData(settlement.serverData);
            }
            this.loadedSettlements.put(newUniqueID, settlement);
            this.settlements.put(newUniqueID, settlement);
            this.updateSettlement(settlement.networkData);
            return settlement;
        }
    }

    public NetworkSettlementData getOrCreateNetworkData(Level level, int tileX, int tileY) {
        return this.getOrCreateLoadedData((Level)level, (int)tileX, (int)tileY).networkData;
    }

    protected SettlementData getOrLoadData(int uniqueID) {
        if (uniqueID == 0) {
            throw new IllegalArgumentException("Cannot load settlement data with uniqueID 0");
        }
        if (!this.isServer()) {
            throw new IllegalStateException("Cannot load settlement data on the client side");
        }
        SettlementData settlement = this.settlements.get(uniqueID);
        if (settlement != null) {
            if (settlement.networkData == null) {
                Level level = this.getServer().world.getLevel(settlement.cachedData.levelIdentifier);
                settlement.networkData = new NetworkSettlementData(level, uniqueID, settlement.cachedData.getTileX(), settlement.cachedData.getTileY());
                settlement.serverData = new ServerSettlementData(this, settlement.networkData, uniqueID);
                settlement.networkData.setServerData(settlement.serverData);
                settlement.cachedData = null;
                this.loadedSettlements.put(uniqueID, settlement);
                this.loadSettlementData(settlement.networkData, settlement.serverData);
                settlement.serverData.ensureRegionsLoaded(true);
            } else {
                this.loadedSettlements.put(uniqueID, settlement);
            }
            return settlement;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerSettlementData getOrLoadServerData(int uniqueID) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementData settlement = this.getOrLoadData(uniqueID);
            return settlement == null ? null : settlement.serverData;
        }
    }

    public NetworkSettlementData applyNetworkDataPacket(Level level, int uniqueID, PacketReader reader, boolean isFull) {
        Point lastSettlementTile = null;
        SettlementData settlement = this.settlements.get(uniqueID);
        if (settlement == null) {
            settlement = new SettlementData();
            settlement.networkData = new NetworkSettlementData(level, uniqueID, Integer.MIN_VALUE, Integer.MIN_VALUE);
            this.settlements.put(uniqueID, settlement);
            this.loadedSettlements.put(uniqueID, settlement);
            if (!isFull && this.isClient()) {
                this.ensureClientRequestedSettlement(uniqueID);
            }
        } else {
            lastSettlementTile = new Point(settlement.networkData.getTileX(), settlement.networkData.getTileY());
        }
        settlement.networkData.readPacket(reader, isFull);
        if (lastSettlementTile == null || lastSettlementTile.x != settlement.networkData.getTileX() || lastSettlementTile.y != settlement.networkData.getTileY()) {
            this.updateSettlement(settlement.networkData);
        }
        if (isFull && this.isClient()) {
            this.submitSettlementRequestFulfilled(uniqueID);
        }
        return settlement.networkData;
    }

    public ServerSettlementData getOrCreateServerData(Level level, int tileX, int tileY) {
        if (!this.isServer()) {
            return null;
        }
        return this.getOrCreateLoadedData((Level)level, (int)tileX, (int)tileY).serverData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream<CachedSettlementData> streamSettlements() {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            return this.settlements.values().stream().map(data -> data.networkData != null ? data.networkData.getCacheData(true) : data.cachedData);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteSettlementsAt(LevelIdentifier levelIdentifier) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            HashSet<Integer> removes = new HashSet<Integer>();
            for (SettlementData settlement : this.settlements.values()) {
                if (settlement.cachedData != null) {
                    if (!settlement.cachedData.levelIdentifier.equals(levelIdentifier)) continue;
                    removes.add(settlement.cachedData.uniqueID);
                    continue;
                }
                if (!settlement.networkData.level.getIdentifier().equals(levelIdentifier)) continue;
                removes.add(settlement.networkData.uniqueID);
            }
            Iterator<SettlementData> iterator = removes.iterator();
            while (iterator.hasNext()) {
                int settlementUniqueID = (Integer)((Object)iterator.next());
                this.unloadSettlement(settlementUniqueID, false);
                WorldFile file = this.getServer().world.fileSystem.getSettlementFile(settlementUniqueID);
                if (file.exists()) {
                    try {
                        file.delete();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                this.settlements.remove(settlementUniqueID);
            }
        }
    }

    public void deleteSettlement(int uniqueID) {
        SettlementData settlementData = this.settlements.get(uniqueID);
        NetworkSettlementData networkCache = settlementData != null ? settlementData.networkData : null;
        this.unloadSettlement(uniqueID, false);
        if (networkCache != null) {
            this.clearSettlementRegions(networkCache);
        }
        if (this.isServer()) {
            WorldFile file = this.getServer().world.fileSystem.getSettlementFile(uniqueID);
            if (file.exists()) {
                try {
                    file.delete();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.settlements.remove(uniqueID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean unloadSettlement(int uniqueID, boolean saveSettlement) {
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            SettlementData settlementData = this.settlements.get(uniqueID);
            if (settlementData != null) {
                boolean out = true;
                if (this.isServer()) {
                    if (settlementData.cachedData != null) {
                        out = false;
                    } else {
                        if (saveSettlement) {
                            this.saveSettlementData(settlementData.networkData, settlementData.serverData);
                        }
                        settlementData.networkData.markUnloaded();
                        settlementData.cachedData = settlementData.networkData.getCacheData(false);
                        settlementData.networkData = null;
                        settlementData.serverData = null;
                    }
                } else {
                    settlementData.networkData.markUnloaded();
                    this.clearSettlementRegions(settlementData.networkData);
                    this.settlements.remove(uniqueID);
                }
                this.loadedSettlements.remove(uniqueID);
                return out;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean ensureClientRequestedSettlement(int settlementUniqueID) {
        if (!this.isClient()) {
            throw new IllegalStateException("Cannot request settlement data on the server side");
        }
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            if (this.loadedSettlements.containsKey(settlementUniqueID) || this.clientRequestQueue.contains(settlementUniqueID) || this.clientRequestedTimes.containsKey(settlementUniqueID)) {
                return false;
            }
            this.clientRequestQueue.add(settlementUniqueID);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void submitSettlementRequestFulfilled(int settlementUniqueID) {
        if (!this.isClient()) {
            throw new IllegalStateException("Cannot submit settlement request fulfilled on the server side");
        }
        SettlementsWorldData settlementsWorldData = this;
        synchronized (settlementsWorldData) {
            this.clientRequestQueue.remove(settlementUniqueID);
            this.clientRequestedTimes.remove(settlementUniqueID);
        }
    }

    public int getTotalSettlements() {
        return this.settlements.size();
    }

    public int getTotalLoadedSettlements() {
        return this.loadedSettlements.size();
    }

    public void runFinalSettlementMigrations(OneWorldMigration migrationData) {
        for (SettlementData settlement : this.settlements.values()) {
            try {
                int uniqueID;
                boolean unloadAfter = false;
                if (settlement.serverData == null) {
                    unloadAfter = true;
                    uniqueID = settlement.cachedData.uniqueID;
                    Level level = migrationData.world.getLevel(settlement.cachedData.levelIdentifier);
                    settlement.networkData = new NetworkSettlementData(level, uniqueID, settlement.cachedData.getTileX(), settlement.cachedData.getTileY());
                    settlement.serverData = new ServerSettlementData(this, settlement.networkData, uniqueID);
                    settlement.networkData.setServerData(settlement.serverData);
                    settlement.cachedData = null;
                    this.loadSettlementData(settlement.networkData, settlement.serverData);
                } else {
                    uniqueID = settlement.networkData.uniqueID;
                }
                settlement.serverData.runFinalMigration(migrationData);
                if (!unloadAfter) continue;
                this.unloadSettlement(uniqueID, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static SettlementsWorldData getSettlementsData(WorldEntity world) {
        WorldData lastLevelData = world.getWorldData("settlements");
        if (lastLevelData instanceof SettlementsWorldData) {
            return (SettlementsWorldData)lastLevelData;
        }
        SettlementsWorldData data = new SettlementsWorldData();
        world.addWorldData("settlements", data);
        return data;
    }

    public static SettlementsWorldData getSettlementsData(WorldEntityGameClock world) {
        return SettlementsWorldData.getSettlementsData(world.getWorldEntity());
    }

    public static SettlementsWorldData getSettlementsData(Server server) {
        return SettlementsWorldData.getSettlementsData(server.world.worldEntity);
    }

    public static SettlementsWorldData getSettlementsData(Client client) {
        return SettlementsWorldData.getSettlementsData(client.worldEntity);
    }

    protected static class SettlementsLevelRegionData {
        protected PointHashMap<Integer> regionSettlements = new PointHashMap();
        protected HashMap<Integer, PointHashSet> settlementRegions = new HashMap();

        protected SettlementsLevelRegionData() {
        }
    }

    protected static class SettlementData {
        protected CachedSettlementData cachedData;
        protected NetworkSettlementData networkData;
        protected ServerSettlementData serverData;

        protected SettlementData() {
        }
    }
}

