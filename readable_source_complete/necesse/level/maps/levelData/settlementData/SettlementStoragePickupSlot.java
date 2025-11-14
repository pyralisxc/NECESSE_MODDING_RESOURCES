/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.LevelStorage;

public class SettlementStoragePickupSlot {
    private GameLinkedList.Element element;
    public final LevelStorage storage;
    public final int slot;
    public final InventoryItem item;
    protected int prevAmountReserved;
    protected long reserveTick;

    protected SettlementStoragePickupSlot(LevelStorage storage, int slot, InventoryItem item, int prevAmountReserved) {
        this.storage = storage;
        this.slot = slot;
        this.prevAmountReserved = prevAmountReserved;
        this.item = item;
    }

    protected void init(GameLinkedList.Element element, WorldEntity worldEntity) {
        if (this.element != null) {
            throw new IllegalStateException("Storage slot already initialized");
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

    protected void removeUnsafe() {
        this.element.remove();
    }

    public void remove() {
        GameLinkedList.Element next = this.element;
        while (next.hasPrev()) {
            next = next.prev();
            ((SettlementStoragePickupSlot)next.object).prevAmountReserved -= this.item.getAmount();
        }
        this.element.remove();
    }

    public boolean isValid(Inventory inventory) {
        InventoryItem item;
        if (this.element.isRemoved()) {
            return false;
        }
        InventoryItem inventoryItem = item = inventory == null ? null : inventory.getItem(this.slot);
        if (item == null || !item.equals(this.storage.level, this.item, true, false, "pickups") || item.getAmount() < this.item.getAmount()) {
            this.remove();
            return false;
        }
        return true;
    }

    public InventoryItem pickupItem(Inventory inventory) {
        InventoryItem item;
        if (this.element.isRemoved()) {
            return null;
        }
        InventoryItem inventoryItem = item = inventory == null ? null : inventory.getItem(this.slot);
        if (item == null || !item.equals(this.storage.level, this.item, true, false, "pickups") || item.getAmount() < this.item.getAmount()) {
            this.remove();
            return null;
        }
        InventoryItem pickedUp = item.copy(this.item.getAmount());
        inventory.setAmount(this.slot, item.getAmount() - pickedUp.getAmount());
        this.remove();
        return pickedUp;
    }

    public boolean isRemoved() {
        return this.element.isRemoved();
    }

    public PickupSettlementStorageActiveJob toPickupJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> pickedUpItemRef) {
        return new PickupSettlementStorageActiveJob(worker, priority, this.storage.tileX, this.storage.tileY, this, pickedUpItemRef);
    }

    public PickupSettlementStorageActiveJob toPickupJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        return this.toPickupJob(worker, priority, new AtomicReference<InventoryItem>());
    }
}

