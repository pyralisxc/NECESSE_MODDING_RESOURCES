/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.awt.Point;
import java.awt.Shape;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.engine.util.PointHashMap;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.storage.ItemFinder;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;

public class SettlementStorageRecordsRegionData {
    protected SettlementStorageIndex index;
    protected PointHashMap<HashMapGameLinkedList<Point, SettlementStorageRecord>> records;
    protected Predicate<InventoryItem> indexFilter;
    protected int totalItems;

    public SettlementStorageRecordsRegionData(SettlementStorageIndex index, Predicate<InventoryItem> indexFilter) {
        this.index = index;
        this.records = new PointHashMap();
        this.indexFilter = indexFilter;
    }

    public HashMapGameLinkedList<Point, SettlementStorageRecord> getRecordsInRegion(int regionX, int regionY) {
        HashMapGameLinkedList<Point, SettlementStorageRecord> list = this.records.get(regionX, regionY);
        if (list == null) {
            list = new HashMapGameLinkedList();
            this.records.put(regionX, regionY, list);
        }
        return list;
    }

    public Stream<SettlementStorageRecord> streamRecordsInTile(int tileX, int tileY) {
        int regionX = this.index.level.regionManager.getRegionCoordByTile(tileX);
        int regionY = this.index.level.regionManager.getRegionCoordByTile(tileY);
        return this.getRecordsInRegion(regionX, regionY).stream(new Point(tileX, tileY));
    }

    public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamRecordsInRegionsShape(Shape shape, int extraRegionRange) {
        return new LevelRegionsSpliterator(this.index.level, shape, extraRegionRange).stream().map(rp -> this.getRecordsInRegion(rp.x, rp.y));
    }

    public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamInRegionsInRange(float x, float y, int range) {
        return this.streamRecordsInRegionsShape(GameUtils.rangeBounds(x, y, range), 0);
    }

    public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamInRegionsInTileRange(int x, int y, int tileRange) {
        return this.streamRecordsInRegionsShape(GameUtils.rangeTileBounds(x, y, tileRange), 0);
    }

    public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamAllRecords() {
        Stream.Builder<HashMapGameLinkedList<Point, SettlementStorageRecord>> builder = Stream.builder();
        for (HashMapGameLinkedList<Point, SettlementStorageRecord> record : this.records.values()) {
            if (record == null) continue;
            builder.add(record);
        }
        return builder.build();
    }

    public void add(SettlementStorageRecord record) {
        int regionX = this.index.level.regionManager.getRegionCoordByTile(record.storage.tileX);
        int regionY = this.index.level.regionManager.getRegionCoordByTile(record.storage.tileY);
        HashMapGameLinkedList<Point, SettlementStorageRecord> map = this.getRecordsInRegion(regionX, regionY);
        map.add(new Point(record.storage.tileX, record.storage.tileY), record);
        this.totalItems += record.itemAmount;
    }

    public InventoryItem validateItem(SettlementStorageRecord record, Runnable onShouldRemove) {
        if (onShouldRemove == null) {
            onShouldRemove = () -> {};
        }
        int slot = record.inventorySlot;
        InventoryRange range = record.storage.getInventoryRange();
        if (range == null) {
            onShouldRemove.run();
            return null;
        }
        if (slot < range.startSlot || slot > range.endSlot) {
            onShouldRemove.run();
            return null;
        }
        InventoryItem item = range.inventory.getItem(slot);
        if (item == null || !this.indexFilter.test(item)) {
            onShouldRemove.run();
            return null;
        }
        record.itemAmount = item.getAmount();
        return item;
    }

    public InventoryItem validateItem(GameLinkedList.Element element) {
        return this.validateItem((SettlementStorageRecord)element.object, element::remove);
    }

    public GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> getRecords(EntityJobWorker worker) {
        ZoneTester restrictZone = worker.getJobRestrictZone();
        return this.getRecords(worker, t -> restrictZone.containsTile(t.x, t.y) && worker.estimateCanMoveTo(t.x, t.y, true));
    }

    public GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> getRecords(EntityJobWorker worker, Predicate<Point> isValidPickupTile) {
        return this.streamRecordsInRegionsShape(worker.getJobSearchBounds(), 0).flatMap(list -> list.entrySet().stream()).filter(e -> isValidPickupTile.test((Point)e.getKey())).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
    }

    public ItemFinder startFinder(EntityJobWorker worker) {
        ZoneTester restrictZone = worker.getJobRestrictZone();
        return this.startFinder(worker, t -> restrictZone.containsTile(t.x, t.y) && worker.estimateCanMoveTo(t.x, t.y, true));
    }

    public ItemFinder startFinder(EntityJobWorker worker, Predicate<Point> isValidPickupTile) {
        return Performance.record((PerformanceTimerManager)this.index.level.tickManager(), "findItems", () -> new ItemFinder(this, worker.getJobSearchTile(), this.getRecords(worker, isValidPickupTile), this.indexFilter));
    }

    public int countItems(Point baseTile, GameTileRange range, Predicate<Point> isValidTile, Predicate<InventoryItem> itemFilter) {
        Level level = this.index.level;
        return Performance.record((PerformanceTimerManager)level.tickManager(), "countItems", () -> this.streamRecordsInRegionsShape(range.getRangeBounds(baseTile), 0).flatMap(list -> list.entrySet().stream()).filter(e -> range.isWithinRange(baseTile, (Point)e.getKey())).filter(e -> isValidTile.test((Point)e.getKey())).reduce(0, (last, e) -> {
            GameLinkedList value = (GameLinkedList)e.getValue();
            int amount = last;
            for (SettlementStorageRecord record : value) {
                InventoryItem item;
                InventoryRange inventoryRange = record.storage.getInventoryRange();
                if (inventoryRange == null || (item = inventoryRange.inventory.getItem(record.inventorySlot)) == null || itemFilter != null && !itemFilter.test(item)) continue;
                amount += item.getAmount();
            }
            return amount;
        }, Integer::sum));
    }

    public int getTotalItems() {
        return this.totalItems;
    }
}

