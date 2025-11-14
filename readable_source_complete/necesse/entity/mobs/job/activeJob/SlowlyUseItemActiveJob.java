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

public abstract class SlowlyUseItemActiveJob
extends ActiveJob {
    protected int idleTime;
    protected int tickIdleCooldown;
    protected long startPerformingTime;
    protected long lastTickIdleTime;
    AtomicReference<InventoryItem> item;
    protected int idleTimePostUsage;
    protected long startPostUsageTime;

    public SlowlyUseItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> item, int idleTime) {
        super(worker, priority);
        this.item = item;
        this.idleTime = idleTime;
        this.tickIdleCooldown = 2000;
    }

    public SlowlyUseItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, InventoryItem item, int idleTime) {
        this(worker, priority, new AtomicReference<InventoryItem>(item), idleTime);
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
        if (this.startPostUsageTime != 0L) {
            return true;
        }
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
        long timeSinceStart;
        long currentTime = this.getLevel().getTime();
        if (this.startPostUsageTime != 0L) {
            long timeSinceUsage = currentTime - this.startPostUsageTime;
            if (timeSinceUsage < (long)this.idleTimePostUsage) {
                long timeSinceIdleTick = currentTime - this.lastTickIdleTime;
                if (timeSinceIdleTick >= (long)this.tickIdleCooldown) {
                    this.tickIdleTime(null);
                    this.lastTickIdleTime = currentTime;
                }
                return ActiveJobResult.PERFORMING;
            }
            return ActiveJobResult.FINISHED;
        }
        InventoryItem item = this.item.get();
        if (item == null) {
            return ActiveJobResult.FAILED;
        }
        if (this.startPerformingTime == 0L) {
            this.startPerformingTime = currentTime;
        }
        if ((timeSinceStart = currentTime - this.startPerformingTime) < (long)this.idleTime) {
            long timeSinceIdleTick = currentTime - this.lastTickIdleTime;
            if (timeSinceIdleTick >= (long)this.tickIdleCooldown) {
                this.tickIdleTime(item);
                this.lastTickIdleTime = currentTime;
            }
            return ActiveJobResult.PERFORMING;
        }
        ListIterator<InventoryItem> li = this.worker.getWorkInventory().listIterator();
        while (li.hasNext()) {
            InventoryItem next = li.next();
            if (!item.equals(this.getLevel(), next, true, false, "equals") || next.getAmount() < item.getAmount() || !this.useItem(next, li)) continue;
            if (this.idleTimePostUsage > 0) {
                this.startPostUsageTime = currentTime;
                return ActiveJobResult.PERFORMING;
            }
            return ActiveJobResult.FINISHED;
        }
        return ActiveJobResult.FAILED;
    }

    public abstract void tickIdleTime(InventoryItem var1);

    public abstract boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2);
}

