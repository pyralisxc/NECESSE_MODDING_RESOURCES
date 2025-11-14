/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class UseItemActiveJob
extends ActiveJob {
    AtomicReference<InventoryItem> item;

    public UseItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> item) {
        super(worker, priority);
        this.item = item;
    }

    public UseItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, InventoryItem item) {
        this(worker, priority, new AtomicReference<InventoryItem>(item));
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return null;
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        return true;
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        if (this.item == null) {
            return false;
        }
        if (isCurrent) {
            InventoryItem item = this.item.get();
            if (item == null) {
                return false;
            }
            return this.worker.getWorkInventory().stream().anyMatch(i -> i.equals(this.getLevel(), item, true, false, "equals") && i.getAmount() >= item.getAmount());
        }
        return true;
    }

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        InventoryItem item = this.item.get();
        if (item == null) {
            return ActiveJobResult.FAILED;
        }
        ListIterator<InventoryItem> li = this.worker.getWorkInventory().listIterator();
        while (li.hasNext()) {
            InventoryItem next = li.next();
            if (!item.equals(this.getLevel(), next, true, false, "equals") || next.getAmount() < item.getAmount() || !this.useItem(next, li)) continue;
            return ActiveJobResult.FINISHED;
        }
        return ActiveJobResult.FAILED;
    }

    public abstract boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2);
}

