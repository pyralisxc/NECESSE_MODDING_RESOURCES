/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.StorageDropOffSimulation;

public class StorageDropOff {
    public final LevelStorage storage;
    private final StorageDropOffSimulation simulation;
    private GameLinkedList.Element element;
    private InventoryItem lastItem;
    private int canAddAmount;
    protected Supplier<InventoryItem> itemSupplier;
    protected long reserveTick;

    protected StorageDropOff(LevelStorage inventory, StorageDropOffSimulation simulation, Supplier<InventoryItem> itemSupplier) {
        this.storage = inventory;
        this.simulation = simulation;
        this.itemSupplier = itemSupplier;
    }

    protected void init(GameLinkedList.Element element, WorldEntity worldEntity) {
        if (this.element != null) {
            throw new IllegalStateException("DropOff already initialized");
        }
        this.element = element;
        this.reserve(worldEntity);
    }

    public void reserve(WorldEntity worldEntity) {
        this.reserveTick = worldEntity.getGameTicks();
    }

    public void reserve(Entity entity) {
        this.reserve(entity.getWorldEntity());
    }

    public boolean isReserved(WorldEntity worldEntity) {
        return this.reserveTick >= worldEntity.getGameTicks() - 2L;
    }

    protected void addItems() {
        InventoryItem item;
        this.lastItem = item = this.itemSupplier.get();
        if (item != null && this.simulation.simulatedRange != null) {
            int addAmount;
            ItemCategoriesFilter filter = this.simulation.storage.getFilter();
            int n = addAmount = filter == null ? item.getAmount() : filter.getAddAmount(this.simulation.storage.level, item, this.simulation.simulatedRange, true);
            if (addAmount <= 0) {
                this.canAddAmount = 0;
                return;
            }
            InventoryItem copy = item.copy();
            this.simulation.simulatedRange.inventory.addItem(this.simulation.storage.level, null, copy, this.simulation.simulatedRange.startSlot, this.simulation.simulatedRange.endSlot, "hauljob", null);
            this.canAddAmount = item.getAmount() - copy.getAmount();
            return;
        }
        this.canAddAmount = -1;
    }

    public int canAddAmount() {
        if (this.element.isRemoved()) {
            return -1;
        }
        InventoryItem item = this.itemSupplier.get();
        if (this.simulation.isDirty) {
            this.simulation.update();
        } else if (!(this.lastItem == item || item != null && this.lastItem != null && item.getAmount() == this.lastItem.getAmount() && item.equals(this.storage.level, this.lastItem, true, false, "dropoffs"))) {
            this.simulation.update();
        }
        return this.canAddAmount;
    }

    public boolean canAddFullAmount() {
        int canAdd = this.canAddAmount();
        if (this.lastItem == null) {
            return false;
        }
        return canAdd >= this.lastItem.getAmount();
    }

    public void remove() {
        this.simulation.isDirty = true;
        if (!this.element.isRemoved()) {
            this.element.remove();
        }
    }

    public int addItem(InventoryItem item) {
        InventoryRange range = this.simulation.storage.getInventoryRange();
        ItemCategoriesFilter filter = this.simulation.storage.getFilter();
        if (range != null) {
            int addAmount;
            int n = addAmount = filter == null ? item.getAmount() : Math.min(filter.getAddAmount(this.simulation.storage.level, item, range, true), item.getAmount());
            if (addAmount > 0) {
                InventoryItem addItem = item.copy(addAmount);
                range.inventory.addItem(this.simulation.storage.level, null, addItem, range.startSlot, range.endSlot, "hauljob", null);
                this.remove();
                return addAmount - addItem.getAmount();
            }
        }
        return 0;
    }

    public InventoryItem getItem() {
        return this.itemSupplier.get();
    }
}

