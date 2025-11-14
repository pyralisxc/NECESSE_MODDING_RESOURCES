/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import necesse.engine.GameTileRange;
import necesse.engine.Settings;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.packet.PacketRequestRegionData;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapPointEntry;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class ClientLevelLoading {
    public static int REGION_LOAD_RANGE = 5;
    public static int REGION_UNLOAD_RANGE = 8;
    public static int MAX_PACKETS_PER_TICK_PRELOAD = 10;
    public static int MAX_PACKETS_PER_TICK_STREAMING = 5;
    public static boolean DEBUG_STREAMING_PAUSED = false;
    private static Dimension LAST_REGION_WINDOW_SIZE = null;
    private static GameTileRange LAST_REGION_LOAD_RANGE = null;
    private static GameTileRange LAST_REGION_UNLOAD_RANGE = null;
    public final Client client;
    public final Level level;
    protected Point lastRegionStartPos;
    protected boolean started;
    private int preloadElements;
    private PriorityUniqueArrayList<Long> queue;
    private PriorityUniqueArrayList<Long> requested;
    private HashSet<Long> loaded;

    private static void refreshRegionLoadRanges(int regionWidth, int regionHeight) {
        if (LAST_REGION_WINDOW_SIZE == null || ClientLevelLoading.LAST_REGION_WINDOW_SIZE.width != regionWidth || ClientLevelLoading.LAST_REGION_WINDOW_SIZE.height != regionHeight) {
            LAST_REGION_WINDOW_SIZE = new Dimension(regionWidth, regionHeight);
            LAST_REGION_LOAD_RANGE = new GameTileRange(REGION_LOAD_RANGE, new Rectangle(LAST_REGION_WINDOW_SIZE));
            LAST_REGION_UNLOAD_RANGE = new GameTileRange(REGION_UNLOAD_RANGE, new Rectangle(LAST_REGION_WINDOW_SIZE));
        }
    }

    public ClientLevelLoading(Client client, Level level) {
        this.client = client;
        this.level = level;
        this.reset();
    }

    public void reset() {
        this.started = false;
        this.preloadElements = 0;
        this.queue = new PriorityUniqueArrayList();
        this.requested = new PriorityUniqueArrayList();
        this.loaded = new HashSet();
        this.lastRegionStartPos = null;
    }

    public void start(PlayerMob player) {
        this.reset();
        if (DEBUG_STREAMING_PAUSED) {
            this.started = true;
            this.preloadElements = 0;
            return;
        }
        int preloadSize = 0;
        GameWindow window = WindowManager.getWindow();
        int screenTileWidth = window.getSceneWidth() / 32;
        int screenTileHeight = window.getSceneHeight() / 32;
        int startTileX = this.level.limitTileXToBounds(player.getTileX() - screenTileWidth / 2);
        int startTileY = this.level.limitTileYToBounds(player.getTileY() - screenTileHeight / 2);
        int endTileX = this.level.limitTileXToBounds(startTileX + screenTileWidth);
        int endTileY = this.level.limitTileYToBounds(startTileY + screenTileHeight);
        int startRegionX = this.level.regionManager.getRegionXByTileLimited(startTileX);
        int startRegionY = this.level.regionManager.getRegionYByTileLimited(startTileY);
        int endRegionX = this.level.regionManager.getRegionXByTileLimited(endTileX);
        int endRegionY = this.level.regionManager.getRegionYByTileLimited(endTileY);
        if (Settings.instantLevelChange) {
            for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
                for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                    if (this.isOrHasLoaded(GameMath.getUniqueLongKey(regionX, regionY))) continue;
                    this.sendRequest(regionX, regionY);
                    ++preloadSize;
                }
            }
        }
        this.refreshLoading(player);
        this.started = true;
        this.preloadElements = Settings.instantLevelChange ? 0 : preloadSize;
    }

    public boolean isStarted() {
        return this.started;
    }

    private boolean isOrHasLoaded(long uniqueKey) {
        return this.loaded.contains(uniqueKey) || this.requested.contains(uniqueKey) || this.queue.contains(uniqueKey);
    }

    public boolean isPreloading() {
        return this.loaded.size() < this.preloadElements;
    }

    public boolean isPreloadingDone() {
        return this.loaded.size() >= this.preloadElements;
    }

    public boolean isLoadingDone() {
        return this.queue.isEmpty() && this.requested.isEmpty();
    }

    public boolean isRegionLoaded(int regionX, int regionY) {
        return this.loaded.contains(GameMath.getUniqueLongKey(regionX, regionY));
    }

    public boolean isTileLoaded(int tileX, int tileY) {
        return this.isRegionLoaded(GameMath.getRegionCoordByTile(tileX), GameMath.getRegionCoordByTile(tileY));
    }

    public int getLoadedRegionsHashCode() {
        return this.loaded.hashCode();
    }

    public int getLoadedRegionsCount() {
        return this.loaded.size();
    }

    public void refreshLoadedRegionsFromServer(PointHashSet loadedRegionPositions) {
        HashSet<Long> lastLoaded = this.loaded;
        this.loaded = new HashSet();
        HashSet<Point> notLoaded = new HashSet<Point>();
        for (Point regionPosition : loadedRegionPositions) {
            long key = GameMath.getUniqueLongKey(regionPosition.x, regionPosition.y);
            if (lastLoaded.contains(key)) {
                lastLoaded.remove(key);
                this.loaded.add(key);
                continue;
            }
            notLoaded.add(regionPosition);
        }
        System.out.println("Synced loaded regions from server. Current loaded: " + this.loaded.size() + ", requesting: " + lastLoaded.size() + ", unloading: " + notLoaded.size());
        Iterator<Serializable> iterator = lastLoaded.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            this.addLoadQueue(0L, GameMath.getXFromUniqueLongKey(key), GameMath.getYFromUniqueLongKey(key));
        }
        if (!notLoaded.isEmpty()) {
            this.client.network.sendPacket(new PacketUnloadRegions(this.level, notLoaded));
        }
    }

    public Iterable<Point> getLoadedRegions() {
        return GameUtils.mapIterable(this.loaded.iterator(), key -> {
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            return new Point(regionX, regionY);
        });
    }

    public Iterable<Point> getRequestedRegions() {
        return GameUtils.mapIterable(((PriorityUniqueArrayList)this.requested).values.keySet().iterator(), key -> {
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            return new Point(regionX, regionY);
        });
    }

    public Iterable<Point> getQueuedRegions() {
        return GameUtils.mapIterable(((PriorityUniqueArrayList)this.queue).values.keySet().iterator(), key -> {
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            return new Point(regionX, regionY);
        });
    }

    public boolean isRegionInQueue(int regionX, int regionY) {
        long uniqueKey = GameMath.getUniqueLongKey(regionX, regionY);
        return this.requested.contains(uniqueKey) || this.queue.contains(uniqueKey);
    }

    private boolean addLoadQueue(long priority, int regionX, int regionY) {
        if (!this.level.regionManager.isRegionWithinBounds(regionX, regionY)) {
            return false;
        }
        long key = GameMath.getUniqueLongKey(regionX, regionY);
        if (!this.loaded.contains(key) && !this.requested.contains(key)) {
            this.queue.add(priority, key);
            return true;
        }
        return false;
    }

    public boolean tickLoading(PlayerMob player) {
        long priority;
        if (!this.started) {
            return false;
        }
        if (player == null) {
            return false;
        }
        if (!DEBUG_STREAMING_PAUSED) {
            this.refreshLoading(player);
        }
        if (this.isLoadingDone()) {
            return false;
        }
        int playerRegionX = this.level.regionManager.getRegionXByTileLimited(player.getTileX());
        int playerRegionY = this.level.regionManager.getRegionYByTileLimited(player.getTileY());
        long timeLimit = System.currentTimeMillis() - 2000L;
        while (!this.requested.isEmpty() && (priority = this.requested.getFirstPriority()) < timeLimit) {
            long uniqueKey = this.requested.getFirst();
            this.requested.removeFirst();
            int regionX = GameMath.getXFromUniqueLongKey(uniqueKey);
            int regionY = GameMath.getYFromUniqueLongKey(uniqueKey);
            int distance = (int)GameMath.squareDistance(regionX, regionY, playerRegionX, playerRegionY);
            this.queue.add(distance, uniqueKey);
        }
        boolean out = false;
        if (this.isPreloading()) {
            if (this.isPreloadingDone()) {
                return false;
            }
            for (int i = 0; i < MAX_PACKETS_PER_TICK_PRELOAD && this.requestNextRegion(); ++i) {
                out = true;
            }
            return out;
        }
        if (this.isLoadingDone()) {
            return false;
        }
        for (int i = 0; i < MAX_PACKETS_PER_TICK_STREAMING && this.requestNextRegion(); ++i) {
            out = true;
        }
        return out;
    }

    private boolean requestNextRegion() {
        if (this.queue.isEmpty()) {
            return false;
        }
        long uniqueKey = this.queue.removeFirst();
        if (this.loaded.contains(uniqueKey)) {
            System.err.println("Loaded map region in load queue");
            return false;
        }
        if (this.requested.contains(uniqueKey)) {
            System.err.println("Loaded map region in sent loaded queue");
            return false;
        }
        int regionX = GameMath.getXFromUniqueLongKey(uniqueKey);
        int regionY = GameMath.getYFromUniqueLongKey(uniqueKey);
        this.sendRequest(regionX, regionY);
        return true;
    }

    protected void refreshLoading(PlayerMob player) {
        int playerRegionX = this.level.regionManager.getRegionXByTileLimited(player.getTileX());
        int playerRegionY = this.level.regionManager.getRegionYByTileLimited(player.getTileY());
        GameWindow window = WindowManager.getWindow();
        int screenTileWidth = window.getSceneWidth() / 32;
        int screenTileHeight = window.getSceneHeight() / 32;
        int startTileX = this.level.limitTileXToBounds(player.getTileX() - screenTileWidth / 2);
        int startTileY = this.level.limitTileYToBounds(player.getTileY() - screenTileHeight / 2);
        int endTileX = this.level.limitTileXToBounds(startTileX + screenTileWidth);
        int endTileY = this.level.limitTileYToBounds(startTileY + screenTileHeight);
        int startRegionX = this.level.regionManager.getRegionXByTileLimited(startTileX);
        int startRegionY = this.level.regionManager.getRegionYByTileLimited(startTileY);
        int endRegionX = Math.max(this.level.regionManager.getRegionXByTileLimited(endTileX), startRegionX + 1);
        int endRegionY = Math.max(this.level.regionManager.getRegionYByTileLimited(endTileY), startRegionY + 1);
        int regionWidth = endRegionX - startRegionX;
        int regionHeight = endRegionY - startRegionY;
        HashSet<Point> regionToUnload = new HashSet<Point>();
        ClientLevelLoading.refreshRegionLoadRanges(regionWidth, regionHeight);
        for (Point point : LAST_REGION_LOAD_RANGE.getValidTiles(startRegionX, startRegionY)) {
            Region region = this.level.regionManager.getRegion(point.x, point.y, false);
            long uniqueKey = GameMath.getUniqueLongKey(point.x, point.y);
            if (region != null) {
                if (!this.isOrHasLoaded(uniqueKey)) {
                    regionToUnload.add(point);
                    continue;
                }
                region.unloadRegionBuffer.keepLoaded();
                continue;
            }
            if (this.isOrHasLoaded(uniqueKey)) continue;
            int distance = (int)GameMath.squareDistance(point.x, point.y, playerRegionX, playerRegionY);
            this.addLoadQueue(distance, point.x, point.y);
        }
        LinkedList<Region> regionsToUnload = this.level.regionManager.tickUnloadRegions(Math.max(Settings.unloadLevelsCooldown / 2, 2));
        for (Region region : regionsToUnload) {
            regionToUnload.add(new Point(region.regionX, region.regionY));
        }
        Point point = new Point(startRegionX, startRegionY);
        if (this.lastRegionStartPos != null && (this.lastRegionStartPos.x != point.x || this.lastRegionStartPos.y != point.y)) {
            for (Point regionPos : LAST_REGION_UNLOAD_RANGE.getValidTiles(this.lastRegionStartPos.x, this.lastRegionStartPos.y)) {
                if (LAST_REGION_UNLOAD_RANGE.isWithinRange(startRegionX, startRegionY, regionPos)) continue;
                regionToUnload.add(regionPos);
            }
        }
        this.unloadRegions(regionToUnload, true);
        this.lastRegionStartPos = point;
    }

    public void sendRequest(int regionX, int regionY) {
        this.client.network.sendPacket(new PacketRequestRegionData(this.level, regionX, regionY));
        long uniqueKey = GameMath.getUniqueLongKey(regionX, regionY);
        this.requested.add(System.currentTimeMillis(), uniqueKey);
        this.queue.remove(uniqueKey);
    }

    public void unloadRegion(int regionX, int regionY, boolean sendPacket) {
        long uniqueKey = GameMath.getUniqueLongKey(regionX, regionY);
        this.loaded.remove(uniqueKey);
        this.requested.remove(uniqueKey);
        this.queue.remove(uniqueKey);
        this.level.regionManager.unloadRegion(regionX, regionY);
        if (sendPacket) {
            this.client.network.sendPacket(new PacketUnloadRegion(this.level, regionX, regionY));
        }
    }

    public void unloadRegions(HashSet<Point> regionPositions, boolean sendPacket) {
        if (regionPositions.isEmpty()) {
            return;
        }
        for (Point regionPosition : regionPositions) {
            long uniqueKey = GameMath.getUniqueLongKey(regionPosition.x, regionPosition.y);
            this.loaded.remove(uniqueKey);
            this.requested.remove(uniqueKey);
            this.queue.remove(uniqueKey);
            this.level.regionManager.unloadRegion(regionPosition.x, regionPosition.y);
        }
        if (sendPacket) {
            this.client.network.sendPacket(new PacketUnloadRegions(this.level, regionPositions));
        }
    }

    public void applyRegionData(PacketRegionData p) {
        this.applyRegionData(p.regionX, p.regionY, p.regionData);
    }

    public void applyRegionData(PointHashMap<Packet> regionData) {
        for (HashMapPointEntry<Point, Packet> entry : regionData.getEntries()) {
            Point regionPos = entry.getKey();
            this.applyRegionData(regionPos.x, regionPos.y, entry.getValue());
        }
    }

    public void applyRegionData(int regionX, int regionY, Packet regionContent) {
        long uniqueKey = GameMath.getUniqueLongKey(regionX, regionY);
        boolean valid = this.level.regionManager.applyRegionDataPacket(regionX, regionY, regionContent);
        if (valid) {
            this.client.levelManager.updateMapRegion(regionX, regionY);
            this.queue.remove(uniqueKey);
            this.loaded.add(uniqueKey);
        } else {
            this.queue.add(0L, uniqueKey);
        }
        this.requested.remove(uniqueKey);
    }

    public int getRegionsLoadedCount() {
        return this.loaded.size();
    }

    public int getRegionsLoadQueueCount() {
        return this.queue.size();
    }

    public int getRegionsRequestedCount() {
        return this.requested.size();
    }

    public float getPercentPreloaded() {
        return Math.min((float)this.loaded.size() / (float)this.preloadElements, 1.0f);
    }

    public float getPercentPresumedPreloaded() {
        return Math.min((float)(this.loaded.size() + this.requested.size()) / (float)this.preloadElements, 1.0f);
    }

    private static class PriorityUniqueArrayList<E> {
        private final GameLinkedList<PriorityValue> queue = new GameLinkedList();
        private final HashMap<E, GameLinkedList.Element> values = new HashMap();

        private PriorityUniqueArrayList() {
        }

        public void add(long priority, E value) {
            if (this.values.containsKey(value)) {
                return;
            }
            GameLinkedList.Element element = GameUtils.insertSortedList(this.queue, new PriorityValue(priority, value), Comparator.comparingLong(e -> e.priority));
            this.values.put(value, element);
        }

        public boolean contains(E value) {
            return this.values.containsKey(value);
        }

        public E remove(E value) {
            GameLinkedList.Element element = this.values.remove(value);
            if (element != null) {
                element.remove();
                return ((PriorityValue)element.object).value;
            }
            return null;
        }

        public Iterable<E> getValues() {
            return this.values.keySet();
        }

        public Iterable<E> getPriorityValues() {
            return GameUtils.mapIterable(this.queue.iterator(), e -> e.value);
        }

        public HashSet<E> removeIfAndGetRemoved(Predicate<E> predicate) {
            HashSet removes = new HashSet();
            ListIterator<PriorityValue> it = this.queue.listIterator();
            while (it.hasNext()) {
                PriorityValue next = it.next();
                if (!predicate.test(next.value)) continue;
                removes.add(next.value);
                this.values.remove(next.value);
                it.remove();
            }
            return removes;
        }

        public long getFirstPriority() {
            return this.queue.getFirst().priority;
        }

        public E getFirst() {
            return this.queue.getFirst().value;
        }

        public E removeFirst() {
            Object value = this.queue.removeFirst().value;
            this.values.remove(value);
            return value;
        }

        public int size() {
            return this.values.size();
        }

        public boolean isEmpty() {
            return this.values.isEmpty();
        }

        private class PriorityValue {
            public long priority;
            public E value;

            public PriorityValue(long priority, E value) {
                this.value = value;
                this.priority = priority;
            }
        }
    }
}

