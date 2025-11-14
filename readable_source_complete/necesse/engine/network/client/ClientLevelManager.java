/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.client.ClientDebugMapHudDrawElement;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketRemoveDeathLocation;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelDeathLocation;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.manager.WorldLevelUnloadedEntityComponent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.mapData.BasicDiscoveredMapManager;
import necesse.level.maps.mapData.ClientDiscoveredMap;
import necesse.level.maps.mapData.GameMapIcon;
import necesse.level.maps.mapData.MapDrawElement;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class ClientLevelManager {
    public static int MAP_SAVE_COOLDOWN = 60000;
    public final Client client;
    private Level previousLevel;
    private Level currentLevel;
    private ClientLevelLoading loading;
    private ClientDiscoveredMap map;
    private ClientDiscoveredMap cheatMap;
    private final LinkedList<ClientDeathLocation> deathLocations = new LinkedList();
    private final LinkedList<MapMarker> mapMarkers = new LinkedList();
    private int mapSaveTimer;
    public ClientDebugMapHudDrawElement debugMapDrawElement;
    public static final Pattern mapRegionFilePattern = Pattern.compile("(-?(?:\\d)+)x(-?(?:\\d)+)\\.mapdata");

    public ClientLevelManager(Client client) {
        this.client = client;
    }

    public Level getLevel() {
        return this.currentLevel;
    }

    public Level getDrawnLevel() {
        if (this.previousLevel != null) {
            return this.previousLevel;
        }
        return this.currentLevel;
    }

    public boolean updateLevel(PacketLevelData packet) {
        ClientClient me = this.client.getClient();
        if (me != null && !me.isSamePlace(packet.levelIdentifier)) {
            return false;
        }
        if (this.currentLevel == null || !packet.isSameLevel(this.currentLevel)) {
            this.setLevel(LevelRegistry.getNewLevel(packet.levelID, packet.levelIdentifier, packet.width, packet.height, this.client.worldEntity));
        }
        this.currentLevel.readLevelDataPacket(new PacketReader(packet.levelContent));
        PlayerMob player = this.client.getPlayer();
        if (player != null) {
            player.setLevel(this.currentLevel);
        }
        for (LevelDeathLocation location : packet.deathLocations) {
            this.addDeathLocation(location);
        }
        return true;
    }

    public void setLevel(Level level) {
        if (this.currentLevel == level) {
            return;
        }
        if (this.currentLevel != null) {
            if (this.previousLevel != null) {
                this.client.worldEntity.dataComponentManager.streamAll(WorldLevelUnloadedEntityComponent.class).forEach(component -> component.onLevelUnloaded(this.previousLevel.getIdentifier()));
                this.previousLevel.dispose();
                this.previousLevel.runGLContextRunnables();
            }
            this.previousLevel = this.currentLevel;
        }
        if (this.debugMapDrawElement != null) {
            this.debugMapDrawElement.remove();
        }
        this.currentLevel = level;
        if (level != null) {
            this.debugMapDrawElement = new ClientDebugMapHudDrawElement(this.client, level);
            this.currentLevel.hudManager.addElement(this.debugMapDrawElement);
            if (this.client.recordLoadingPerformance) {
                level.debugLoadingPerformance = new PerformanceTimerManager();
            }
            level.setWorldEntity(this.client.worldEntity);
            level.makeClientLevel(this.client);
            this.loading = new ClientLevelLoading(this.client, level);
            this.map = new ClientDiscoveredMap(this.client, level, true, true, false);
            this.cheatMap = new ClientDiscoveredMap(this.client, level, false, false, true);
            this.cheatMap.setSameDrawElementsAs(this.map);
        } else {
            this.saveMap();
            this.map.dispose();
            this.map = null;
            this.cheatMap.dispose();
            this.cheatMap = null;
            this.loading = null;
            this.debugMapDrawElement = null;
        }
        this.deathLocations.clear();
        for (int i = 0; i < this.client.getSlots(); ++i) {
            ClientClient c = this.client.getClient(i);
            if (c == null || c.playerMob == null || !c.loadedPlayer) continue;
            if (level == null) {
                c.playerMob.setLevel(null);
                continue;
            }
            if (c.isSamePlace(level)) {
                c.playerMob.setLevel(level);
                continue;
            }
            c.playerMob.setLevel(null);
        }
    }

    public void tick() {
        if (this.loading != null) {
            this.loading.tickLoading(this.client.getPlayer());
        }
        if (this.previousLevel != null && this.currentLevel != null && (this.loading == null || this.loading.level != this.previousLevel && this.loading.isPreloadingDone())) {
            this.client.worldEntity.dataComponentManager.streamAll(WorldLevelUnloadedEntityComponent.class).forEach(component -> component.onLevelUnloaded(this.previousLevel.getIdentifier()));
            this.previousLevel.dispose();
            this.previousLevel.runGLContextRunnables();
            this.previousLevel = null;
            GlobalData.getCurrentState().onClientDrawnLevelChanged();
        }
    }

    public void addDeathLocation(LevelDeathLocation location) {
        if (this.deathLocations.stream().noneMatch(dl -> dl.location.x == location.x && dl.location.y == location.y)) {
            this.deathLocations.add(new ClientDeathLocation(this.client, location));
        }
    }

    public void clearDeathLocations() {
        for (ClientDeathLocation l : this.deathLocations) {
            l.drawElement.remove();
        }
        this.deathLocations.clear();
    }

    public ClientLevelLoading loading() {
        return this.loading;
    }

    public boolean isLevelLoaded(int levelIdentifierHashCode) {
        return this.currentLevel != null && this.currentLevel.getIdentifierHashCode() == levelIdentifierHashCode;
    }

    public boolean isRegionLoaded(int regionX, int regionY) {
        return this.loading != null && this.loading.isRegionLoaded(regionX, regionY);
    }

    public boolean isRegionLoadedAtTile(int tileX, int tileY) {
        int regionX = this.currentLevel.regionManager.getRegionCoordByTile(tileX);
        int regionY = this.currentLevel.regionManager.getRegionCoordByTile(tileY);
        return this.loading != null && this.loading.isRegionLoaded(regionX, regionY);
    }

    public boolean checkIfLoadedRegionAtTile(int levelIdentifierHashCode, int tileX, int tileY, boolean sendPacketIfNot) {
        if (this.loading != null && this.isLevelLoaded(levelIdentifierHashCode)) {
            int regionY;
            int regionX = this.currentLevel.regionManager.getRegionCoordByTile(tileX);
            if (this.loading.isRegionLoaded(regionX, regionY = this.currentLevel.regionManager.getRegionCoordByTile(tileY))) {
                return true;
            }
            if (sendPacketIfNot && !this.loading.isRegionInQueue(regionX, regionY)) {
                this.client.network.sendPacket(new PacketUnloadRegion(this.currentLevel, regionX, regionY));
            }
        }
        return false;
    }

    public boolean checkIfLoadedRegionAtTile(int levelIdentifierHashCode, RegionPositionGetter getter, boolean sendPacketIfNot) {
        if (getter == null) {
            return false;
        }
        if (this.loading != null && this.isLevelLoaded(levelIdentifierHashCode)) {
            HashSet unloadRegions;
            PointSetAbstract<?> regionPositions = getter.getRegionPositions();
            if (regionPositions.isEmpty() || regionPositions.stream().anyMatch(pos -> this.loading.isRegionLoaded(pos.x, pos.y))) {
                return true;
            }
            if (sendPacketIfNot && !(unloadRegions = regionPositions.stream().filter(pos -> !this.loading.isRegionLoaded(pos.x, pos.y) && !this.loading.isRegionInQueue(pos.x, pos.y)).map(pos -> new Point(pos.x, pos.y)).collect(Collectors.toCollection(HashSet::new))).isEmpty()) {
                this.client.network.sendPacket(new PacketUnloadRegions(this.currentLevel, unloadRegions));
            }
        }
        return false;
    }

    private String getWorldMapDataPath() {
        return GlobalData.appDataPath() + "saves/maps/" + this.client.getWorldUniqueID();
    }

    public File getLevelWorldMapDataFile(LevelIdentifier identifier, int mapRegionX, int mapRegionY) {
        return new File(this.getWorldMapDataPath() + "/" + identifier.stringID + "/" + mapRegionX + "x" + mapRegionY + ".mapdata");
    }

    public File getLevelWorldMapIconsFile() {
        return new File(this.getWorldMapDataPath() + "/icons.mapdata");
    }

    public BasicDiscoveredMapManager loadAllMapData() {
        this.saveMap();
        BasicDiscoveredMapManager manager = new BasicDiscoveredMapManager();
        manager.loadFromFileSystem(this.getWorldMapDataPath());
        return manager;
    }

    public void applyMapData(BasicDiscoveredMapManager manager) {
        manager.saveToFileSystem(this.client.levelManager::getLevelWorldMapDataFile);
        if (this.map != null) {
            this.map.reloadFromCache();
        }
        if (this.cheatMap != null) {
            this.cheatMap.reloadFromCache();
        }
    }

    public ClientDiscoveredMap getMap() {
        if (GlobalData.debugCheatActive()) {
            return this.cheatMap;
        }
        return this.map;
    }

    public void finishUp() {
        this.currentLevel.onLoadingComplete();
        PlayerMob player = this.client.getPlayer();
        this.map.tickDiscovery(this.currentLevel, player.getTileX(), player.getTileY());
        if (GlobalData.debugCheatActive()) {
            this.cheatMap.tickDiscovery(this.currentLevel, player.getTileX(), player.getTileY());
        }
    }

    public void clientTick(PlayerMob player) {
        this.currentLevel.clientTick();
        if (!this.client.isPaused()) {
            if (this.map != null) {
                this.mapSaveTimer += 50;
                if (this.mapSaveTimer >= MAP_SAVE_COOLDOWN) {
                    this.mapSaveTimer = 0;
                    this.saveMap();
                }
            }
            if (player != null && player.getLevel() != null && this.map.level.isSamePlace(player.getLevel())) {
                this.map.tickDiscovery(player.getLevel(), player.getTileX(), player.getTileY());
                this.map.tickUpdate(player.getTileX(), player.getTileY());
                this.map.tickCache();
                if (GlobalData.debugCheatActive()) {
                    this.cheatMap.tickDiscovery(player.getLevel(), player.getTileX(), player.getTileY());
                    this.cheatMap.tickUpdate(player.getTileX(), player.getTileY());
                    this.cheatMap.tickCache();
                }
            }
        }
    }

    public void updateMapTile(int tileX, int tileY) {
        this.map.addDelayedTileUpdate(tileX, tileY);
        if (GlobalData.debugCheatActive()) {
            this.cheatMap.addDelayedTileUpdate(tileX, tileY);
        }
    }

    public void updateMapRegion(int regionX, int regionY) {
        this.map.addDelayedRegionUpdate(regionX, regionY);
        if (GlobalData.debugCheatActive()) {
            this.cheatMap.addDelayedRegionUpdate(regionX, regionY);
        }
    }

    public void deleteMapData() {
        this.map.deleteCache();
        this.map = new ClientDiscoveredMap(this.client, this.currentLevel, true, true, false);
        this.cheatMap = new ClientDiscoveredMap(this.client, this.currentLevel, false, false, true);
        this.cheatMap.setSameDrawElementsAs(this.map);
        this.mapMarkers.clear();
        File iconsFile = this.getLevelWorldMapIconsFile();
        if (iconsFile.exists()) {
            iconsFile.delete();
        }
    }

    public Iterable<MapMarker> getMapMarkers() {
        return this.mapMarkers;
    }

    public MapMarker addMapMarker(GameMapIcon icon, GameMessage name, LevelIdentifier levelIdentifier, int tileX, int tileY) {
        if (icon == null) {
            throw new IllegalArgumentException("Icon cannot be null");
        }
        MapMarker mapMarker = new MapMarker(icon, name, levelIdentifier, tileX, tileY);
        this.mapMarkers.add(mapMarker);
        return mapMarker;
    }

    public MapMarker addMapMarker(String iconStringID, GameMessage name, LevelIdentifier levelIdentifier, int tileX, int tileY) {
        return this.addMapMarker(MapIconRegistry.getIcon(iconStringID), name, levelIdentifier, tileX, tileY);
    }

    public void deleteMapIcon(MapMarker icon) {
        this.mapMarkers.remove(icon);
    }

    public void loadMapIcons() {
        File file = this.getLevelWorldMapIconsFile();
        if (!file.exists()) {
            return;
        }
        try {
            this.mapMarkers.clear();
            LoadData iconsSave = new LoadData(file);
            for (LoadData iconSave : iconsSave.getLoadData()) {
                try {
                    this.mapMarkers.add(new MapMarker(iconSave));
                }
                catch (LoadDataException e) {
                    System.err.println("Could not load map icon: " + e.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            System.err.println("Could not load map icons: " + e.getMessage());
        }
    }

    public void saveMap() {
        try {
            this.map.saveToCache();
            SaveData iconsSave = new SaveData("ICONS");
            for (MapMarker icon : this.mapMarkers) {
                iconsSave.addSaveData(icon.getSaveData("ICON"));
            }
            iconsSave.saveScript(this.getLevelWorldMapIconsFile());
        }
        catch (IOException e) {
            System.err.println("Error saving map data:");
            e.printStackTrace();
        }
    }

    public void dispose() {
        if (this.previousLevel != null) {
            this.previousLevel.dispose();
            this.previousLevel.runGLContextRunnables();
        }
        if (this.currentLevel != null) {
            this.currentLevel.dispose();
            this.currentLevel.runGLContextRunnables();
        }
        if (this.map != null) {
            this.saveMap();
            this.map.dispose();
        }
        if (this.cheatMap != null) {
            this.cheatMap.dispose();
        }
    }

    public class ClientDeathLocation {
        public final LevelDeathLocation location;
        public final MapDrawElement drawElement;

        public ClientDeathLocation(final Client client, final LevelDeathLocation location) {
            this.location = location;
            final long startTime = client.worldEntity.getLocalTime() - (long)location.secondsSince * 1000L;
            this.drawElement = new MapDrawElement(location.x, location.y, new Rectangle(-12, -12, 24, 24)){

                @Override
                public void draw(int x, int y, PlayerMob perspective) {
                    Settings.UI.deathmarker.initDraw().posMiddle(x, y).draw();
                }

                @Override
                public boolean shouldRemove() {
                    long secondsSince = (client.worldEntity.getLocalTime() - startTime) / 1000L;
                    return secondsSince > 18000L;
                }

                @Override
                public GameTooltips getTooltips(int x, int y, PlayerMob perspective) {
                    ListGameTooltips tooltips = new ListGameTooltips();
                    tooltips.add(Localization.translate("misc", "recentdeath"));
                    long secondsSince = (client.worldEntity.getLocalTime() - startTime) / 1000L;
                    tooltips.add(GameUtils.formatSeconds(secondsSince));
                    return tooltips;
                }

                @Override
                public String getMapInteractTooltip() {
                    return Localization.translate("controls", "cleartip");
                }

                @Override
                public void onMapInteract(InputEvent event, PlayerMob perspective) {
                    if (ClientLevelManager.this.currentLevel != null) {
                        client.network.sendPacket(new PacketRemoveDeathLocation(ClientLevelManager.this.currentLevel.getIdentifier(), location.x, location.y));
                    }
                    this.remove();
                    event.use();
                }
            };
            ClientLevelManager.this.map.addDrawElement(this.drawElement);
        }
    }

    public static class MapMarker {
        public GameMapIcon icon;
        public GameMessage name;
        public LevelIdentifier levelIdentifier;
        public final int tileX;
        public final int tileY;

        public MapMarker(GameMapIcon icon, GameMessage name, LevelIdentifier levelIdentifier, int tileX, int tileY) {
            this.icon = icon;
            this.name = name;
            this.levelIdentifier = levelIdentifier;
            this.tileX = tileX;
            this.tileY = tileY;
        }

        public MapMarker(LoadData save) throws LoadDataException {
            String stringID = save.getSafeString("iconStringID", null, false);
            if (stringID == null) {
                throw new LoadDataException("Missing map icon stringID");
            }
            GameMapIcon icon = MapIconRegistry.getIcon(stringID);
            if (icon == null) {
                throw new LoadDataException("Invalid map icon stringID: " + stringID);
            }
            this.icon = icon;
            try {
                this.name = GameMessage.loadSave(save.getFirstLoadDataByName("name"));
            }
            catch (Exception e) {
                throw new LoadDataException("Could not load map icon name: " + e.getMessage());
            }
            String levelIdentifierString = save.getSafeString("levelIdentifier", null, false);
            if (levelIdentifierString == null) {
                throw new LoadDataException("Missing map icon levelIdentifier");
            }
            try {
                this.levelIdentifier = new LevelIdentifier(levelIdentifierString);
            }
            catch (InvalidLevelIdentifierException e) {
                throw new LoadDataException("Invalid map icon levelIdentifier: " + levelIdentifierString);
            }
            this.tileX = save.getInt("tileX", Integer.MIN_VALUE, false);
            this.tileY = save.getInt("tileY", Integer.MIN_VALUE, false);
            if (this.tileX == Integer.MIN_VALUE || this.tileY == Integer.MIN_VALUE) {
                throw new LoadDataException("Missing map icon tile");
            }
        }

        public SaveData getSaveData(String name) {
            SaveData save = new SaveData(name);
            save.addSafeString("iconStringID", this.icon.getStringID());
            save.addSaveData(this.name.getSaveData("name"));
            save.addSafeString("levelIdentifier", this.levelIdentifier.stringID);
            save.addInt("tileX", this.tileX);
            save.addInt("tileY", this.tileY);
            return save;
        }
    }
}

