/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HaulFromLevelJob
extends LevelJob {
    public LevelStorage storage;
    public boolean onlyAcceptSpecificAmount = false;
    public InventoryItem item;
    public LinkedList<HaulPosition> dropOffPositions = new LinkedList();

    public HaulFromLevelJob(LevelStorage storage, InventoryItem item) {
        super(storage.tileX, storage.tileY);
        this.storage = storage;
        this.item = item;
    }

    public HaulFromLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isSameJob(LevelJob other) {
        if (this.isSameTile(other) && other.getID() == this.getID()) {
            HaulFromLevelJob otherHaul = (HaulFromLevelJob)other;
            return this.item.equals(this.getLevel(), otherHaul.item, true, false, "equals");
        }
        return false;
    }

    @Override
    public boolean isValid() {
        if (this.item == null || this.item.getAmount() <= 0 || this.dropOffPositions.isEmpty()) {
            return false;
        }
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            int amount = 0;
            for (int i = range.startSlot; i <= range.endSlot; ++i) {
                if (range.inventory.isSlotClear(i)) continue;
                InventoryItem invItem = range.inventory.getItem(i);
                if (!this.item.equals(this.getLevel(), invItem, true, false, "dropoff")) continue;
                amount += invItem.getAmount();
                if (!(this.onlyAcceptSpecificAmount ? amount >= this.item.getAmount() : amount > 0)) continue;
                return true;
            }
        }
        return false;
    }

    public InventoryRange getInventoryRange() {
        return this.storage.getInventoryRange();
    }

    public static <T extends HaulFromLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        return ((HaulFromLevelJob)foundJob.job).getJobSequence(worker, foundJob.priority);
    }

    public JobSequence getJobSequence(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        ZoneTester restrictZone = worker.getJobRestrictZone();
        InventoryRange range = this.getInventoryRange();
        if (range != null) {
            LinkedList<HaulPosition> validHaulPositions = new LinkedList<HaulPosition>();
            ListIterator dropOffLI = this.dropOffPositions.listIterator();
            while (dropOffLI.hasNext()) {
                InventoryRange dropOffRange;
                HaulPosition pos = (HaulPosition)dropOffLI.next();
                if (!restrictZone.containsTile(pos.storage.tileX, pos.storage.tileY) || (dropOffRange = pos.getInventoryRange()) == null) continue;
                int addAmount = pos.storage.getFilter().getAddAmount(this.getLevel(), this.item, dropOffRange, false);
                if (addAmount > 0 && dropOffRange.inventory.canAddItem(this.getLevel(), null, this.item, dropOffRange.startSlot, dropOffRange.endSlot, "hauljob") > 0) {
                    if (!worker.estimateCanMoveTo(pos.storage.tileX, pos.storage.tileY, true)) continue;
                    validHaulPositions.add(pos);
                    continue;
                }
                dropOffLI.remove();
            }
            if (!validHaulPositions.isEmpty()) {
                Comparator<ComputedObjectValue> comparator = Comparator.comparingInt(p -> -((HaulPosition)p.object).priority);
                comparator = comparator.thenComparingDouble(ComputedValue::get);
                HaulPosition pos = validHaulPositions.stream().map(p -> new ComputedObjectValue<HaulPosition, Double>((HaulPosition)p, () -> new Point(this.tileX * 32 + 16, this.tileY * 32 + 16).distance(p.storage.tileX * 32 + 16, p.storage.tileY * 32 + 16))).min(comparator).map(p -> (HaulPosition)p.object).orElse(null);
                if (pos != null) {
                    LinkedList<SettlementStoragePickupSlot> slots;
                    int amount = Math.min(pos.storage.canAddFutureDropOff(this.item), Math.min(pos.amount, this.item.itemStackSize()));
                    if (amount > 0 && (slots = this.storage.findUnreservedSlots(this.item.copy(), amount, amount)) != null && !slots.isEmpty()) {
                        GameMessage targetDescription = this.storage.getInventoryName();
                        GameMessage itemDescription = slots.getFirst().item.getItemLocalization();
                        LocalMessage activityDescription = new LocalMessage("activities", "hauling", "item", itemDescription, "target", targetDescription);
                        LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
                        for (SettlementStoragePickupSlot slot : slots) {
                            AtomicReference<InventoryItem> pickedUpItemRef = new AtomicReference<InventoryItem>();
                            sequence.addFirst(new PickupSettlementStorageActiveJob(worker, priority, this.tileX, this.tileY, slot, pickedUpItemRef));
                            sequence.addLast(new DropOffSettlementStorageActiveJob(worker, priority, pos.storage, null, null, false, () -> {
                                if (slot.isRemoved()) {
                                    return (InventoryItem)pickedUpItemRef.get();
                                }
                                return slot.item;
                            }));
                            this.item.setAmount(this.item.getAmount() - amount);
                        }
                        return sequence;
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public static class HaulPosition {
        public final LevelStorage storage;
        public final int priority;
        public final int amount;

        public HaulPosition(LevelStorage storage, int priority, int amount) {
            this.storage = storage;
            this.priority = priority;
            this.amount = amount;
        }

        public InventoryRange getInventoryRange() {
            return this.storage.getInventoryRange();
        }
    }
}

