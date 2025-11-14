/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.GameTileRange;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMapRegion;
import necesse.level.maps.mapData.DiscoveredMapBoundsExecutor;
import necesse.level.maps.mapData.DiscoveredMapData;
import necesse.level.maps.mapData.MapDrawElement;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionBoundsExecutor;

public class ClientDiscoveredMap {
    public static final GameTileRange DISCOVER_RANGE = new GameTileRange(40, new Point[0]);
    public static final GameTileRange DISCOVER_RANGE_EXTENDED = new GameTileRange(55, new Point[0]);
    public final Client client;
    public final Level level;
    public final boolean shouldSave;
    public final boolean useDiscoverRange;
    private boolean lastExtendedDiscoverRange = false;
    public final boolean alwaysLight;
    private final HashMap<Long, ClientDiscoveredMapRegion> regions = new HashMap();
    private LinkedList<MapDrawElement> drawElements = new LinkedList();
    private final PointHashSet mapRegionTextureUpdates = new PointHashSet();
    private final LinkedList<DelayedTileUpdate> delayedTextureTileUpdates = new LinkedList();
    private final LinkedList<DelayedRegionUpdate> delayedTextureRegionUpdates = new LinkedList();
    private Point lastDiscoverTile;
    private PointHashSet lastDiscoverRegions = new PointHashSet();

    public ClientDiscoveredMap(Client client, Level level, boolean useDiscoverRange, boolean shouldSave, boolean alwaysLight) {
        this.client = client;
        this.level = level;
        this.shouldSave = shouldSave;
        this.useDiscoverRange = useDiscoverRange;
        this.alwaysLight = alwaysLight;
    }

    public void setSameDrawElementsAs(ClientDiscoveredMap other) {
        this.drawElements = other.drawElements;
    }

    public void addDrawElement(MapDrawElement element) {
        this.cleanDrawElements();
        this.drawElements.add(element);
        element.init(this.level);
    }

    private void cleanDrawElements() {
        ListIterator it = this.drawElements.listIterator();
        while (it.hasNext()) {
            MapDrawElement next = (MapDrawElement)it.next();
            if (!next.isRemoved() && !next.shouldRemove()) continue;
            next.onRemove();
            it.remove();
        }
    }

    public Iterable<MapDrawElement> getDrawElements() {
        this.cleanDrawElements();
        return this.drawElements;
    }

    public void saveToCache() throws IOException {
        if (!this.shouldSave) {
            return;
        }
        for (ClientDiscoveredMapRegion region : this.regions.values()) {
            if (!region.shouldSave()) continue;
            File file = this.client.levelManager.getLevelWorldMapDataFile(this.level.getIdentifier(), region.mapRegionX, region.mapRegionY);
            region.saveToFileSystem(file);
            region.markSaved();
        }
    }

    public void deleteCache() {
        if (!this.shouldSave) {
            return;
        }
        for (ClientDiscoveredMapRegion region : this.regions.values()) {
            File file = this.client.levelManager.getLevelWorldMapDataFile(this.level.getIdentifier(), region.mapRegionX, region.mapRegionY);
            if (!file.exists() || file.delete()) continue;
            System.err.println("Error deleting map data region " + region.mapRegionX + "x" + region.mapRegionY);
        }
    }

    public void reloadFromCache() {
        for (ClientDiscoveredMapRegion region : this.regions.values()) {
            File file = this.client.levelManager.getLevelWorldMapDataFile(this.level.getIdentifier(), region.mapRegionX, region.mapRegionY);
            try {
                region.loadFromCache(file);
            }
            catch (Exception e) {
                System.err.println("Error reloading minimap region " + region.mapRegionX + "x" + region.mapRegionY + ": " + e.getMessage());
            }
        }
    }

    public void tickCache() {
        for (ClientDiscoveredMapRegion value : this.regions.values()) {
            value.tickCache();
        }
    }

    protected ClientDiscoveredMapRegion loadRegion(int mapRegionX, int mapRegionY, boolean onlyIfFileExists) {
        int tileXOffset = GameMath.multiplyByPowerOf2(mapRegionX, 8);
        int tileYOffset = GameMath.multiplyByPowerOf2(mapRegionY, 8);
        int tileWidth = 256;
        if (this.level.tileWidth > 0) {
            if (mapRegionX < 0) {
                return null;
            }
            if ((tileWidth = Math.min(tileWidth, this.level.tileWidth - tileXOffset)) <= 0) {
                return null;
            }
        }
        int tileHeight = 256;
        if (this.level.tileHeight > 0) {
            if (mapRegionY < 0) {
                return null;
            }
            if ((tileHeight = Math.min(tileHeight, this.level.tileHeight - tileYOffset)) <= 0) {
                return null;
            }
        }
        File file = this.client.levelManager.getLevelWorldMapDataFile(this.level.getIdentifier(), mapRegionX, mapRegionY);
        if (onlyIfFileExists && !file.exists()) {
            return null;
        }
        ClientDiscoveredMapRegion region = new ClientDiscoveredMapRegion(mapRegionX, mapRegionY, tileXOffset, tileYOffset, tileWidth, tileHeight, this.level.getIdentifier().toString());
        try {
            region.loadFromCache(file);
        }
        catch (Exception e) {
            System.err.println("Error loading minimap region " + mapRegionX + "x" + mapRegionY + ": " + e.getMessage());
        }
        return region;
    }

    protected ClientDiscoveredMapRegion getRegion(int mapRegionX, int mapRegionY, boolean createIfDoesntExist, boolean onlyIfFileExists) {
        long key = GameMath.getUniqueLongKey(mapRegionX, mapRegionY);
        ClientDiscoveredMapRegion region = this.regions.get(key);
        if (region == null && createIfDoesntExist && (region = this.loadRegion(mapRegionX, mapRegionY, onlyIfFileExists)) != null) {
            this.regions.put(key, region);
            this.mapRegionTextureUpdates.add(mapRegionX, mapRegionY);
        }
        return region;
    }

    protected ClientDiscoveredMapRegion getRegionByTile(int tileX, int tileY, boolean createIfDoesntExist, boolean onlyIfFileExists) {
        if (this.level.tileWidth > 0 && (tileX < 0 || tileX >= this.level.tileWidth)) {
            return null;
        }
        if (this.level.tileHeight > 0 && (tileY < 0 || tileY >= this.level.tileHeight)) {
            return null;
        }
        int mapRegionX = GameMath.divideByPowerOf2RoundedDown(tileX, 8);
        int mapRegionY = GameMath.divideByPowerOf2RoundedDown(tileY, 8);
        return this.getRegion(mapRegionX, mapRegionY, createIfDoesntExist, onlyIfFileExists);
    }

    public boolean isTileKnown(int tileX, int tileY) {
        ClientDiscoveredMapRegion region = this.getRegionByTile(tileX, tileY, true, true);
        if (region == null) {
            return false;
        }
        return region.getRGB(tileX - region.tileXOffset, tileY - region.tileYOffset) != 0;
    }

    public boolean combine(int mapRegionX, int mapRegionY, DiscoveredMapData other) {
        ClientDiscoveredMapRegion region = this.getRegion(mapRegionX, mapRegionY, true, false);
        if (region == null) {
            return false;
        }
        if (region.combine(other)) {
            this.mapRegionTextureUpdates.add(mapRegionX, mapRegionY);
            return true;
        }
        return false;
    }

    public void tickDiscovery(Level level, int centerTileX, int centerTileY) {
        if (this.useDiscoverRange) {
            boolean extendedRange;
            PlayerMob player = this.client.getPlayer();
            boolean bl = extendedRange = player != null && player.buffManager.getModifier(BuffModifiers.EXTENDED_MAP_DISCOVER_RANGE) != false;
            if (this.lastDiscoverTile != null && this.lastDiscoverTile.x == centerTileX && this.lastDiscoverTile.y == centerTileY && extendedRange == this.lastExtendedDiscoverRange) {
                return;
            }
            GameTileRange nextDiscoverRange = extendedRange ? DISCOVER_RANGE_EXTENDED : DISCOVER_RANGE;
            GameTileRange lastDiscoverRange = this.lastExtendedDiscoverRange ? DISCOVER_RANGE_EXTENDED : DISCOVER_RANGE;
            this.lastExtendedDiscoverRange = extendedRange;
            RegionBoundsExecutor regions = new RegionBoundsExecutor(level.regionManager, level.limitTileXToBounds(centerTileX - nextDiscoverRange.maxRange - 1), level.limitTileYToBounds(centerTileY - nextDiscoverRange.maxRange - 1), level.limitTileXToBounds(centerTileX + nextDiscoverRange.maxRange + 1), level.limitTileYToBounds(centerTileY + nextDiscoverRange.maxRange + 1), false);
            DiscoveredMapBoundsExecutor maps = new DiscoveredMapBoundsExecutor(this, level.limitTileXToBounds(centerTileX - nextDiscoverRange.maxRange - 1), level.limitTileYToBounds(centerTileY - nextDiscoverRange.maxRange - 1), level.limitTileXToBounds(centerTileX + nextDiscoverRange.maxRange + 1), level.limitTileYToBounds(centerTileY + nextDiscoverRange.maxRange + 1), true, false);
            maps.runOnTiles(nextDiscoverRange.getValidTiles(centerTileX, centerTileY), true, (map, mapRegionTileX, mapRegionTileY) -> {
                int color;
                boolean shouldForceUpdate;
                int tileX = mapRegionTileX + map.tileXOffset;
                int tileY = mapRegionTileY + map.tileYOffset;
                if (!regions.isInsideBounds(tileX, tileY)) {
                    return;
                }
                boolean bl = shouldForceUpdate = this.lastDiscoverTile != null && !lastDiscoverRange.isWithinRange(this.lastDiscoverTile.x, this.lastDiscoverTile.y, tileX, tileY);
                if ((shouldForceUpdate || map.getRGB(mapRegionTileX, mapRegionTileY) == 0) && (color = this.getMapColor(regions, tileX, tileY, true)) != 0 && map.setRGB(mapRegionTileX, mapRegionTileY, color)) {
                    this.mapRegionTextureUpdates.add(map.mapRegionX, map.mapRegionY);
                }
            });
            this.lastDiscoverTile = new Point(centerTileX, centerTileY);
        } else {
            int centerRegionX = level.regionManager.getRegionCoordByTile(centerTileX);
            int centerRegionY = level.regionManager.getRegionCoordByTile(centerTileY);
            if (this.lastDiscoverTile != null && this.lastDiscoverTile.x == centerRegionX && this.lastDiscoverTile.y == centerRegionY) {
                return;
            }
            ClientLevelLoading loading = this.client.levelManager.loading();
            PointHashSet nextDiscoveredRegions = new PointHashSet();
            for (Point regionPos : loading.getLoadedRegions()) {
                Region region;
                if (this.lastDiscoverRegions.contains(regionPos.x, regionPos.y) || (region = level.regionManager.getRegion(regionPos.x, regionPos.y, false)) == null) continue;
                nextDiscoveredRegions.add(regionPos.x, regionPos.y);
                this.runRegionsUpdate(centerTileX, centerTileY, region.regionX, region.regionY);
            }
            this.lastDiscoverRegions = nextDiscoveredRegions;
            this.lastDiscoverTile = new Point(centerRegionX, centerRegionY);
        }
    }

    public void addDelayedTileUpdate(int tileX, int tileY) {
        this.delayedTextureTileUpdates.addLast(new DelayedTileUpdate(this.level.getLocalTime() + 1000L, tileX, tileY));
    }

    public void addDelayedRegionUpdate(int regionX, int regionY) {
        this.delayedTextureRegionUpdates.addLast(new DelayedRegionUpdate(this.level.getLocalTime() + 1000L, regionX, regionY));
    }

    protected GameTileRange getCurrentDiscoverRange() {
        return this.lastExtendedDiscoverRange ? DISCOVER_RANGE_EXTENDED : DISCOVER_RANGE;
    }

    public void tickUpdate(int centerTileX, int centerTileY) {
        if (!this.delayedTextureTileUpdates.isEmpty()) {
            LinkedList<Point> openTiles = new LinkedList<Point>();
            int minTileX = centerTileX;
            int minTileY = centerTileY;
            int maxTileX = centerTileX;
            int maxTileY = centerTileY;
            while (!this.delayedTextureTileUpdates.isEmpty()) {
                DelayedTileUpdate first = this.delayedTextureTileUpdates.getFirst();
                if (this.level.getLocalTime() < first.updateLocalTime) break;
                if (!this.useDiscoverRange || GameMath.diagonalMoveDistance(centerTileX, centerTileY, first.tileX, first.tileY) <= (double)(this.getCurrentDiscoverRange().maxRange + 20)) {
                    openTiles.add(new Point(first.tileX, first.tileY));
                    minTileX = Math.min(minTileX, first.tileX);
                    minTileY = Math.min(minTileY, first.tileY);
                    maxTileX = Math.max(maxTileX, first.tileX);
                    maxTileY = Math.max(maxTileY, first.tileY);
                }
                this.delayedTextureTileUpdates.removeFirst();
            }
            if (!openTiles.isEmpty()) {
                this.runDepthFirstUpdate(centerTileX, centerTileY, openTiles, minTileX, minTileY, maxTileX, maxTileY);
            }
        }
        if (!this.delayedTextureRegionUpdates.isEmpty()) {
            while (!this.delayedTextureRegionUpdates.isEmpty()) {
                DelayedRegionUpdate first = this.delayedTextureRegionUpdates.getFirst();
                if (this.level.getLocalTime() < first.updateLocalTime) break;
                this.runRegionsUpdate(centerTileX, centerTileY, first.regionX, first.regionY);
                this.delayedTextureRegionUpdates.removeFirst();
            }
        }
        for (long key : this.mapRegionTextureUpdates.getKeys()) {
            ClientDiscoveredMapRegion map = this.regions.get(key);
            if (map == null) continue;
            map.tickTextureUpdate();
        }
        this.mapRegionTextureUpdates.clear();
    }

    protected void runDepthFirstUpdate(int centerTileX, int centerTileY, LinkedList<Point> openTiles, int minTileX, int minTileY, int maxTileX, int maxTileY) {
        RegionBoundsExecutor regions = new RegionBoundsExecutor(this.level.regionManager, this.level.limitTileXToBounds(minTileX - 15), this.level.limitTileYToBounds(minTileY - 15), this.level.limitTileXToBounds(maxTileX + 15), this.level.limitTileYToBounds(maxTileY + 15), false);
        DiscoveredMapBoundsExecutor maps = new DiscoveredMapBoundsExecutor(this, this.level.limitTileXToBounds(centerTileX - 15), this.level.limitTileYToBounds(centerTileY - 15), this.level.limitTileXToBounds(centerTileX + 15), this.level.limitTileYToBounds(centerTileY + 15), true, false);
        PointHashSet closedTiles = new PointHashSet();
        while (!openTiles.isEmpty()) {
            int color;
            ClientDiscoveredMapRegion map;
            Region region;
            Point currentTile = openTiles.removeFirst();
            closedTiles.add(currentTile.x, currentTile.y);
            if (this.useDiscoverRange && !this.getCurrentDiscoverRange().isWithinRange(centerTileX, centerTileY, currentTile) || !regions.isInsideBounds(currentTile.x, currentTile.y) || (region = (Region)regions.getRegionByTile(currentTile.x, currentTile.y)) == null || !maps.isInsideBounds(currentTile.x, currentTile.y) || (map = (ClientDiscoveredMapRegion)maps.getRegionByTile(currentTile.x, currentTile.y)) == null || (color = this.getMapColor(regions, currentTile.x, currentTile.y, true)) == 0 || !map.setRGB(currentTile.x - map.tileXOffset, currentTile.y - map.tileYOffset, color)) continue;
            this.mapRegionTextureUpdates.add(map.mapRegionX, map.mapRegionY);
            if (!closedTiles.contains(currentTile.x - 1, currentTile.y)) {
                openTiles.addLast(new Point(currentTile.x - 1, currentTile.y));
            }
            if (!closedTiles.contains(currentTile.x + 1, currentTile.y)) {
                openTiles.addLast(new Point(currentTile.x + 1, currentTile.y));
            }
            if (!closedTiles.contains(currentTile.x, currentTile.y - 1)) {
                openTiles.addLast(new Point(currentTile.x, currentTile.y - 1));
            }
            if (closedTiles.contains(currentTile.x, currentTile.y + 1)) continue;
            openTiles.addLast(new Point(currentTile.x, currentTile.y + 1));
        }
    }

    protected void runRegionsUpdate(int centerTileX, int centerTileY, int regionX, int regionY) {
        Region startRegion = this.level.regionManager.getRegion(regionX, regionY, false);
        if (startRegion == null) {
            return;
        }
        int startTileX = startRegion.tileXOffset;
        int startTileY = startRegion.tileYOffset;
        int endTileX = startTileX + startRegion.tileWidth - 1;
        int endTileY = startTileY + startRegion.tileHeight - 1;
        RegionBoundsExecutor regions = new RegionBoundsExecutor(this.level.regionManager, this.level.limitTileXToBounds(startTileX - 1), this.level.limitTileYToBounds(startTileY - 1), this.level.limitTileXToBounds(endTileX + 1), this.level.limitTileYToBounds(endTileY + 1), false);
        DiscoveredMapBoundsExecutor maps = new DiscoveredMapBoundsExecutor(this, this.level.limitTileXToBounds(startTileX - 1), this.level.limitTileYToBounds(startTileY - 1), this.level.limitTileXToBounds(endTileX + 1), this.level.limitTileYToBounds(endTileY + 1), true, false);
        for (int tileX = startTileX; tileX <= endTileX; ++tileX) {
            for (int tileY = startTileY; tileY <= endTileY; ++tileY) {
                int color;
                ClientDiscoveredMapRegion map;
                if (this.useDiscoverRange && !this.getCurrentDiscoverRange().isWithinRange(centerTileX, centerTileY, tileX, tileY) || (map = (ClientDiscoveredMapRegion)maps.getRegionByTile(tileX, tileY)) == null || (color = this.getMapColor(regions, tileX, tileY, true)) == 0 || !map.setRGB(tileX - map.tileXOffset, tileY - map.tileYOffset, color)) continue;
                this.mapRegionTextureUpdates.add(map.mapRegionX, map.mapRegionY);
            }
        }
    }

    protected int getMapColor(RegionBoundsExecutor regions, int tileX, int tileY, boolean enableLighting) {
        Color objectColor;
        GameObject object;
        Region region = (Region)regions.getRegionByTile(tileX, tileY);
        if (region == null) {
            return 0;
        }
        if (!region.isLoadingComplete()) {
            return 0;
        }
        int regionTileX = tileX - region.tileXOffset;
        int regionTileY = tileY - region.tileYOffset;
        GameTile tile = region.tileLayer.getTileByRegion(regionTileX, regionTileY);
        Color color = null;
        if (tile.getID() == TileRegistry.emptyID) {
            return 0;
        }
        Color tileColor = tile.getMapColor(this.level, tileX, tileY);
        if (tileColor != null) {
            color = tileColor;
        }
        if ((object = region.objectLayer.getObjectByRegion(0, regionTileX, regionTileY)).getID() != 0 && (objectColor = object.getMapColor(this.level, tileX, tileY)) != null) {
            color = objectColor;
        }
        if (color == null) {
            return 0;
        }
        if (enableLighting) {
            GameLight staticLight;
            if (this.level.isCave) {
                staticLight = this.alwaysLight || Settings.alwaysLight ? this.level.lightManager.newLight(150.0f) : region.lightLayer.getStaticLight(regionTileX, regionTileY);
                if (staticLight != null) {
                    float fLight = staticLight.getFloatLevel();
                    color = new Color((int)((float)color.getRed() * staticLight.getFloatRed() * fLight), (int)((float)color.getGreen() * staticLight.getFloatGreen() * fLight), (int)((float)color.getBlue() * staticLight.getFloatBlue() * fLight), color.getAlpha());
                } else {
                    color = new Color(0, 0, 0, color.getAlpha());
                }
            } else if (!(region.subRegionData.isOutsideByRegion(regionTileX, regionTileY) || this.isOutside(regions, tileX - 1, tileY) || this.isOutside(regions, tileX + 1, tileY) || this.isOutside(regions, tileX, tileY - 1) || this.isOutside(regions, tileX, tileY + 1))) {
                staticLight = this.alwaysLight || Settings.alwaysLight ? this.level.lightManager.newLight(150.0f) : region.lightLayer.getStaticLight(regionTileX, regionTileY);
                if (staticLight != null) {
                    float fLight = staticLight.getFloatLevel();
                    color = new Color((int)((float)color.getRed() * staticLight.getFloatRed() * fLight), (int)((float)color.getGreen() * staticLight.getFloatGreen() * fLight), (int)((float)color.getBlue() * staticLight.getFloatBlue() * fLight), color.getAlpha());
                } else {
                    color = new Color(0, 0, 0, color.getAlpha());
                }
            }
        }
        return color.getRGB();
    }

    protected boolean isOutside(RegionBoundsExecutor regions, int tileX, int tileY) {
        if (!regions.isInsideBounds(tileX, tileY)) {
            return true;
        }
        Region region = (Region)regions.getRegionByTile(tileX, tileY);
        if (region == null) {
            return true;
        }
        return region.subRegionData.isOutsideByRegion(tileX - region.tileXOffset, tileY - region.tileYOffset);
    }

    public void dispose() {
        for (ClientDiscoveredMapRegion value : this.regions.values()) {
            value.dispose();
        }
    }

    private static class DelayedTileUpdate {
        public final long updateLocalTime;
        public final int tileX;
        public final int tileY;

        public DelayedTileUpdate(long updateLocalTime, int tileX, int tileY) {
            this.updateLocalTime = updateLocalTime;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }

    private static class DelayedRegionUpdate {
        public final long updateLocalTime;
        public final int regionX;
        public final int regionY;

        public DelayedRegionUpdate(long updateLocalTime, int regionX, int regionY) {
            this.updateLocalTime = updateLocalTime;
            this.regionX = regionX;
            this.regionY = regionY;
        }
    }
}

