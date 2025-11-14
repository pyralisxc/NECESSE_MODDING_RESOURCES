/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class UnequipItemActiveJob
extends ActiveJob {
    public int inventorySlot;
    public HumanMob humanMob;

    public UnequipItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int inventorySlot, HumanMob humanMob) {
        super(worker, priority);
        this.inventorySlot = inventorySlot;
        this.humanMob = humanMob;
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
        if (isCurrent) {
            return !this.humanMob.getInventory().isSlotClear(this.inventorySlot);
        }
        return true;
    }

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        InventoryItem item = this.humanMob.getInventory().getItem(this.inventorySlot);
        if (item == null) {
            return ActiveJobResult.FAILED;
        }
        this.worker.showPlaceAnimation(this.worker.getMobWorker().getDir() == 3 ? this.humanMob.getX() - 10 : this.humanMob.getX() + 10, this.humanMob.getY(), item.item, 200);
        this.humanMob.getInventory().setItem(this.inventorySlot, null);
        this.worker.getWorkInventory().add(item.copy());
        this.worker.getWorkInventory().markDirty();
        return ActiveJobResult.FINISHED;
    }
}

