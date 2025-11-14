/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.StorageDropOff;
import necesse.level.maps.levelData.settlementData.StorageDropOffSimulation;

public abstract class LevelStorage {
    public final Level level;
    public final int tileX;
    public final int tileY;
    protected LinkedList<HaulFromLevelJob> haulFromLevelJobs = new LinkedList();
    protected StorageDropOffSimulation dropOffSimulation = new StorageDropOffSimulation(this);
    protected HashMapGameLinkedList<Integer, SettlementStoragePickupSlot> pickupSlots = new HashMapGameLinkedList();

    public LevelStorage(Level level, int tileX, int tileY) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public abstract InventoryRange getInventoryRange();

    public abstract ItemCategoriesFilter getFilter();

    public abstract GameMessage getInventoryName();

    public boolean isValid() {
        return this.getInventoryRange() != null;
    }

    public void removeInvalidPickups() {
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            Iterator iterator = this.pickupSlots.keySet().iterator();
            while (iterator.hasNext()) {
                int slot = (Integer)iterator.next();
                boolean valid = slot >= range.startSlot && slot <= range.endSlot;
                InventoryItem item = range.inventory.getItem(slot);
                GameLinkedList slots = (GameLinkedList)this.pickupSlots.get(slot);
                slots.removeIf(e -> !valid || item == null || !item.equals(this.level, e.item, true, false, "pickups") || !e.isReserved(this.level.getWorldEntity()));
            }
        } else {
            Iterator iterator = this.pickupSlots.keySet().iterator();
            while (iterator.hasNext()) {
                int slot = (Integer)iterator.next();
                ((GameLinkedList)this.pickupSlots.get(slot)).elements().forEach(GameLinkedList.Element::remove);
            }
        }
    }

    public SettlementStoragePickupSlot reserve(int slot, InventoryItem currentItem, int amount) {
        GameLinkedList storageSlots = (GameLinkedList)this.pickupSlots.get(slot);
        int reservedAmount = storageSlots.stream().filter(e -> e.isReserved(this.level.getWorldEntity())).mapToInt(e -> e.item.getAmount()).sum();
        if (reservedAmount >= currentItem.getAmount()) {
            return null;
        }
        int amountLeft = currentItem.getAmount() - reservedAmount;
        amount = Math.min(amount, amountLeft);
        SettlementStoragePickupSlot pickup = new SettlementStoragePickupSlot(this, slot, currentItem.copy(amount), reservedAmount);
        pickup.init(storageSlots.addFirst(pickup), this.level.getWorldEntity());
        return pickup;
    }

    public SettlementStoragePickupFuture getFutureReserve(final int slot, final InventoryItem currentItem, final int maxAmount, final Consumer<SettlementStoragePickupSlot> accepted) {
        final GameLinkedList storageSlots = (GameLinkedList)this.pickupSlots.get(slot);
        final int reservedAmount = storageSlots.stream().filter(e -> e.isReserved(this.level.getWorldEntity())).mapToInt(e -> e.item.getAmount()).sum();
        if (reservedAmount >= currentItem.getAmount()) {
            return null;
        }
        int amountLeft = currentItem.getAmount() - reservedAmount;
        return new SettlementStoragePickupFuture(this, currentItem.copy(Math.min(amountLeft, maxAmount))){

            @Override
            public SettlementStoragePickupSlot accept(int amount) {
                InventoryItem pickedUpItems = currentItem.copy(Math.min(amount, maxAmount));
                SettlementStoragePickupSlot pickupSlot = new SettlementStoragePickupSlot(this.storage, slot, pickedUpItems, reservedAmount);
                pickupSlot.init(storageSlots.addFirst(pickupSlot), LevelStorage.this.level.getWorldEntity());
                LevelStorage.this.haulFromLevelJobs.stream().filter(e -> e.item.equals(LevelStorage.this.level, pickedUpItems, true, false, "pickups")).findFirst().ifPresent(job -> job.item.setAmount(job.item.getAmount() - pickedUpItems.getAmount()));
                accepted.accept(pickupSlot);
                return pickupSlot;
            }
        };
    }

    public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(Predicate<InventoryItem> itemPredicate, int minAmount, int maxAmount) {
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            LinkedList<SettlementStoragePickupSlot> slots = new LinkedList<SettlementStoragePickupSlot>();
            int pickedUpAmount = 0;
            for (int i = range.endSlot; i >= range.startSlot; --i) {
                int amount;
                GameLinkedList storageSlots;
                int reservedAmount;
                InventoryItem invItem = range.inventory.getItem(i);
                if (invItem == null || !itemPredicate.test(invItem) || (reservedAmount = (storageSlots = (GameLinkedList)this.pickupSlots.get(i)).stream().filter(e -> e.isReserved(this.level.getWorldEntity())).mapToInt(e -> e.item.getAmount()).sum()) >= invItem.getAmount() || (amount = Math.min(maxAmount - pickedUpAmount, invItem.getAmount())) <= 0) continue;
                SettlementStoragePickupSlot slot = new SettlementStoragePickupSlot(this, i, invItem.copy(amount), reservedAmount);
                slot.init(storageSlots.addFirst(slot), this.level.getWorldEntity());
                slots.addFirst(slot);
                if ((pickedUpAmount += amount) >= maxAmount) break;
            }
            if (pickedUpAmount < minAmount) {
                slots.forEach(SettlementStoragePickupSlot::remove);
                return null;
            }
            if (slots.isEmpty()) {
                return null;
            }
            return slots;
        }
        return new LinkedList<SettlementStoragePickupSlot>();
    }

    public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(ItemFilter filter, int minAmount, int maxAmount) {
        return this.findUnreservedSlots(filter::matchesItem, minAmount, maxAmount);
    }

    public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(InventoryItem item, int minAmount, int maxAmount) {
        return this.findUnreservedSlots((InventoryItem invItem) -> invItem.canCombine(this.level, null, item, "hauljob"), minAmount, maxAmount);
    }

    public Stream<SettlementStoragePickupFuture> findFutureUnreservedSlots() {
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            Stream.Builder<2> builder = Stream.builder();
            for (int i = range.endSlot; i >= range.startSlot; --i) {
                GameLinkedList storageSlots;
                int reservedAmount;
                final int slot = i;
                final InventoryItem invItem = range.inventory.getItem(slot);
                if (invItem == null || (reservedAmount = (storageSlots = (GameLinkedList)this.pickupSlots.get(slot)).stream().filter(e -> e.isReserved(this.level.getWorldEntity())).mapToInt(e -> e.item.getAmount()).sum()) >= invItem.getAmount()) continue;
                final int maxAmount = invItem.getAmount() - reservedAmount;
                builder.add(new SettlementStoragePickupFuture(this, invItem.copy(maxAmount)){

                    @Override
                    public SettlementStoragePickupSlot accept(int amount) {
                        InventoryItem pickedUpItems = invItem.copy(Math.min(amount, maxAmount));
                        SettlementStoragePickupSlot pickupSlot = new SettlementStoragePickupSlot(this.storage, slot, pickedUpItems, reservedAmount);
                        pickupSlot.init(storageSlots.addFirst(pickupSlot), LevelStorage.this.level.getWorldEntity());
                        LevelStorage.this.haulFromLevelJobs.stream().filter(e -> e.item.equals(LevelStorage.this.level, pickedUpItems, true, false, "pickups")).findFirst().ifPresent(job -> job.item.setAmount(job.item.getAmount() - pickedUpItems.getAmount()));
                        return pickupSlot;
                    }
                });
            }
            return builder.build();
        }
        return Stream.empty();
    }

    public int canAddFutureDropOff(InventoryItem item) {
        return this.dropOffSimulation.canAddFutureDropOff(item);
    }

    public StorageDropOff addFutureDropOff(Supplier<InventoryItem> itemSupplier) {
        return this.dropOffSimulation.addFutureDropOff(this, itemSupplier);
    }

    public int getItemCount(Predicate<InventoryItem> filter, int maxCount, boolean useSimulatedInventory) {
        return this.dropOffSimulation.getItemCount(filter, maxCount, useSimulatedInventory);
    }
}

