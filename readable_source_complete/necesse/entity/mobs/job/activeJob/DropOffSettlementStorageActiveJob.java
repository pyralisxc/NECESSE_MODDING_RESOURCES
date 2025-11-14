/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.StorageDropOff;

public class DropOffSettlementStorageActiveJob
extends TileActiveJob {
    public final StorageDropOff dropOff;
    public final GameObjectReservable reservable;
    public Supplier<Boolean> isRemovedCheck;
    public boolean requireFullAmount;
    public long lastHoldTickTime;
    public int holdTickCooldown = 2000;

    public DropOffSettlementStorageActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, LevelStorage storage, GameObjectReservable reservable, Supplier<Boolean> isRemovedCheck, boolean requireFullAmount, Supplier<InventoryItem> dropOffItem) {
        this(worker, priority, reservable, isRemovedCheck, requireFullAmount, storage.addFutureDropOff(dropOffItem));
    }

    public DropOffSettlementStorageActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, GameObjectReservable reservable, Supplier<Boolean> isRemovedCheck, boolean requireFullAmount, StorageDropOff dropOff) {
        super(worker, priority, dropOff.storage.tileX, dropOff.storage.tileY);
        this.reservable = reservable;
        this.isRemovedCheck = isRemovedCheck;
        this.requireFullAmount = requireFullAmount;
        this.dropOff = dropOff;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return new JobMoveToTile(this.tileX, this.tileY, true);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        long currentTime;
        long timeSinceIdleTick;
        if (this.reservable != null) {
            this.reservable.reserve(this.worker.getMobWorker());
        }
        this.dropOff.reserve(this.worker.getMobWorker());
        if (isCurrent && (timeSinceIdleTick = (currentTime = this.getLevel().getTime()) - this.lastHoldTickTime) >= (long)this.holdTickCooldown) {
            InventoryItem item = this.dropOff.getItem();
            if (item != null) {
                this.worker.showHoldAnimation(item.item, this.holdTickCooldown + 500);
            }
            this.lastHoldTickTime = currentTime;
        }
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        if (this.isRemovedCheck != null && this.isRemovedCheck.get().booleanValue()) {
            return false;
        }
        if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
            return false;
        }
        if (this.requireFullAmount) {
            return this.dropOff.canAddFullAmount();
        }
        return this.dropOff.canAddAmount() > 0;
    }

    @Override
    public void onCancelled(boolean becauseOfInvalid, boolean isCurrent, boolean isMovingTo) {
        super.onCancelled(becauseOfInvalid, isCurrent, isMovingTo);
        this.dropOff.remove();
    }

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        this.worker.clearHoldAnimation();
        InventoryItem dropOffItem = this.dropOff.getItem();
        ListIterator<InventoryItem> li = this.worker.getWorkInventory().listIterator();
        while (li.hasNext()) {
            int added;
            InventoryItem item = li.next();
            if (!item.equals(this.getLevel(), dropOffItem, true, false, "equals") || (added = this.dropOff.addItem(dropOffItem)) <= 0) continue;
            item.setAmount(item.getAmount() - added);
            if (item.getAmount() <= 0) {
                li.remove();
            }
            this.worker.getWorkInventory().markDirty();
            this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, item.item, 250);
            return ActiveJobResult.FINISHED;
        }
        return ActiveJobResult.FAILED;
    }
}

