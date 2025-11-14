/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class ItemFinder {
    private SettlementStorageRecordsRegionData regionData;
    private Point currentTile;
    private GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> records;

    public ItemFinder(SettlementStorageRecordsRegionData regionData, Point startTile, GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> records, Predicate<InventoryItem> indexFilter) {
        this.regionData = regionData;
        this.currentTile = startTile;
        this.records = records;
    }

    public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int minAmount, int maxAmount, Predicate<InventoryItem> itemFilter) {
        return Performance.record((PerformanceTimerManager)this.regionData.index.level.tickManager(), "findItems", () -> {
            GameLinkedList.Element best;
            int foundAmount = 0;
            Point startTile = this.currentTile;
            HashSet<Point> beenAt = new HashSet<Point>();
            LinkedList<SettlementStoragePickupFuture> foundSlots = new LinkedList<SettlementStoragePickupFuture>();
            while (foundAmount < maxAmount && !this.records.isEmpty() && (best = (GameLinkedList.Element)this.records.streamElements().filter(e -> !beenAt.contains(((Map.Entry)e.object).getKey())).min(Comparator.comparingDouble(e -> this.currentTile.distance((Point2D)((Map.Entry)e.object).getKey()))).orElse(null)) != null) {
                beenAt.add((Point)((Map.Entry)best.object).getKey());
                Point tile = (Point)((Map.Entry)best.object).getKey();
                GameLinkedList slots = (GameLinkedList)((Map.Entry)best.object).getValue();
                GameLinkedList.Element currentElement = slots.getLastElement();
                boolean found = false;
                while (currentElement != null) {
                    GameLinkedList.Element nextElement = currentElement.prev();
                    try {
                        GameLinkedList.Element finalCurrentElement;
                        int queryAmount;
                        SettlementStoragePickupFuture futureReserve;
                        InventoryItem item = this.regionData.validateItem(currentElement);
                        if (item == null) continue;
                        SettlementStorageRecord record = (SettlementStorageRecord)currentElement.object;
                        found = true;
                        if (itemFilter != null && !itemFilter.test(item) || (futureReserve = record.storage.getFutureReserve(record.inventorySlot, item, queryAmount = Math.min(record.itemAmount, maxAmount - foundAmount), arg_0 -> ItemFinder.lambda$findPickupSlots$2(record, finalCurrentElement = currentElement, arg_0))) == null) continue;
                        foundSlots.add(futureReserve);
                        this.currentTile = tile;
                        if ((foundAmount += futureReserve.item.getAmount()) < maxAmount) continue;
                        break;
                    }
                    finally {
                        currentElement = nextElement;
                    }
                }
                if (found) continue;
                best.remove();
            }
            if (foundAmount >= minAmount) {
                LinkedList<SettlementStoragePickupSlot> slots = new LinkedList<SettlementStoragePickupSlot>();
                for (SettlementStoragePickupFuture foundSlot : foundSlots) {
                    slots.add(foundSlot.accept(foundSlot.item.getAmount()));
                }
                return slots;
            }
            this.currentTile = startTile;
            return null;
        });
    }

    public SettlementStoragePickupSlot findFirstItemPickup(Predicate<InventoryItem> itemFilter) {
        return Performance.record((PerformanceTimerManager)this.regionData.index.level.tickManager(), "findItems", () -> {
            GameLinkedList.Element best;
            HashSet<Point> beenAt = new HashSet<Point>();
            while (!this.records.isEmpty() && (best = (GameLinkedList.Element)this.records.streamElements().filter(e -> !beenAt.contains(((Map.Entry)e.object).getKey())).min(Comparator.comparingDouble(e -> this.currentTile.distance((Point2D)((Map.Entry)e.object).getKey()))).orElse(null)) != null) {
                beenAt.add((Point)((Map.Entry)best.object).getKey());
                Point tile = (Point)((Map.Entry)best.object).getKey();
                GameLinkedList slots = (GameLinkedList)((Map.Entry)best.object).getValue();
                GameLinkedList.Element currentElement = slots.getLastElement();
                boolean found = false;
                while (currentElement != null) {
                    GameLinkedList.Element nextElement = currentElement.prev();
                    try {
                        InventoryItem item = this.regionData.validateItem(currentElement);
                        if (item == null) continue;
                        SettlementStorageRecord record = (SettlementStorageRecord)currentElement.object;
                        if (itemFilter != null && !itemFilter.test(item)) continue;
                        found = true;
                        SettlementStoragePickupSlot reserve = record.storage.reserve(record.inventorySlot, item, 1);
                        if (reserve == null) continue;
                        this.currentTile = tile;
                        --record.itemAmount;
                        if (record.itemAmount <= 0 && !currentElement.isRemoved()) {
                            currentElement.remove();
                        }
                        SettlementStoragePickupSlot settlementStoragePickupSlot = reserve;
                        return settlementStoragePickupSlot;
                    }
                    finally {
                        currentElement = nextElement;
                    }
                }
                if (found) continue;
                best.remove();
            }
            return null;
        });
    }

    public SettlementStoragePickupSlot findBestItem(Predicate<InventoryItem> itemFilter, Comparator<SettlementStoragePickupFuture> comparator) {
        return Performance.record((PerformanceTimerManager)this.regionData.index.level.tickManager(), "findItems", () -> {
            GameLinkedList.Element best;
            HashSet<Point> beenAt = new HashSet<Point>();
            SettlementStoragePickupFuture bestSlot = null;
            while (!this.records.isEmpty() && (best = (GameLinkedList.Element)this.records.streamElements().filter(e -> !beenAt.contains(((Map.Entry)e.object).getKey())).min(Comparator.comparingDouble(e -> this.currentTile.distance((Point2D)((Map.Entry)e.object).getKey()))).orElse(null)) != null) {
                beenAt.add((Point)((Map.Entry)best.object).getKey());
                GameLinkedList slots = (GameLinkedList)((Map.Entry)best.object).getValue();
                GameLinkedList.Element currentElement = slots.getLastElement();
                while (currentElement != null) {
                    GameLinkedList.Element nextElement = currentElement.prev();
                    try {
                        InventoryItem item = this.regionData.validateItem(currentElement);
                        if (item == null) continue;
                        SettlementStorageRecord record = (SettlementStorageRecord)currentElement.object;
                        if (itemFilter != null && !itemFilter.test(item)) continue;
                        GameLinkedList.Element finalCurrentElement = currentElement;
                        SettlementStoragePickupFuture nextSlot = record.storage.getFutureReserve(record.inventorySlot, item, 1, acceptedSlot -> {
                            record.itemAmount -= acceptedSlot.item.getAmount();
                            if (record.itemAmount <= 0 && !finalCurrentElement.isRemoved()) {
                                finalCurrentElement.remove();
                            }
                        });
                        if (bestSlot != null && comparator.compare(bestSlot, nextSlot) >= 0) continue;
                        bestSlot = nextSlot;
                    }
                    finally {
                        currentElement = nextElement;
                    }
                }
            }
            if (bestSlot != null) {
                this.currentTile = new Point(bestSlot.storage.tileX, bestSlot.storage.tileY);
                return bestSlot.accept(1);
            }
            return null;
        });
    }

    private static /* synthetic */ void lambda$findPickupSlots$2(SettlementStorageRecord record, GameLinkedList.Element finalCurrentElement, SettlementStoragePickupSlot acceptedSlot) {
        record.itemAmount -= acceptedSlot.item.getAmount();
        if (record.itemAmount <= 0 && !finalCurrentElement.isRemoved()) {
            finalCurrentElement.remove();
        }
    }
}

