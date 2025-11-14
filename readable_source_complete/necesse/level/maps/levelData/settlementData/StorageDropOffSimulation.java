/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.StorageDropOff;

public class StorageDropOffSimulation {
    protected final LevelStorage storage;
    protected InventoryRange simulatedRange;
    protected GameLinkedList<StorageDropOff> dropOffs = new GameLinkedList();
    protected boolean isDirty;

    public StorageDropOffSimulation(LevelStorage storage) {
        this.storage = storage;
        this.isDirty = true;
    }

    protected void update() {
        InventoryRange range = this.storage.getInventoryRange();
        this.simulatedRange = range == null ? new InventoryRange(new Inventory(0)) : new InventoryRange(range.inventory.copy(), range.startSlot, range.endSlot);
        this.dropOffs.stream().filter(e -> !e.isReserved(this.storage.level.getWorldEntity())).forEach(StorageDropOff::remove);
        this.dropOffs.forEach(StorageDropOff::addItems);
        this.isDirty = false;
    }

    public StorageDropOff addFutureDropOff(LevelStorage inventory, Supplier<InventoryItem> itemSupplier) {
        StorageDropOff out = new StorageDropOff(inventory, this, itemSupplier);
        out.init(this.dropOffs.addLast(out), this.storage.level.getWorldEntity());
        out.addItems();
        return out;
    }

    public int canAddFutureDropOff(InventoryItem item) {
        if (this.isDirty) {
            this.update();
        }
        return Math.min(this.storage.getFilter().getAddAmount(this.storage.level, item, this.simulatedRange, false), Math.min(this.simulatedRange.inventory.canAddItem(this.storage.level, null, item, this.simulatedRange.startSlot, this.simulatedRange.endSlot, "hauljob"), item.getAmount()));
    }

    public int getItemCount(Predicate<InventoryItem> filter, int maxCount, boolean useSimulatedInventory) {
        return StorageDropOffSimulation.getItemCount(filter, maxCount, useSimulatedInventory ? this.simulatedRange : this.storage.getInventoryRange());
    }

    public static int getItemCount(Predicate<InventoryItem> filter, int maxCount, InventoryRange range) {
        int count = 0;
        if (range != null) {
            for (int i = range.startSlot; i <= range.endSlot; ++i) {
                InventoryItem item = range.inventory.getItem(i);
                if (item == null || !filter.test(item) || (count += item.getAmount()) < maxCount) continue;
                return maxCount;
            }
        }
        return count;
    }
}

