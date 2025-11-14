/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.UseItemActiveJob;
import necesse.inventory.InventoryItem;

public class EquipItemActiveJob
extends UseItemActiveJob {
    public int inventorySlot;
    public HumanMob humanMob;

    public EquipItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> item, int inventorySlot, HumanMob humanMob) {
        super(worker, priority, item);
        this.inventorySlot = inventorySlot;
        this.humanMob = humanMob;
    }

    public EquipItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, InventoryItem item, int inventorySlot, HumanMob humanMob) {
        super(worker, priority, item);
        this.inventorySlot = inventorySlot;
        this.humanMob = humanMob;
    }

    @Override
    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
        Mob mob = this.worker.getMobWorker();
        this.worker.showPickupAnimation(this.worker.getMobWorker().getDir() == 3 ? mob.getX() - 10 : mob.getX() + 10, mob.getY(), item.item, 200);
        InventoryItem lastItem = this.humanMob.equipmentInventory.getItem(this.inventorySlot);
        this.humanMob.equipmentInventory.setItem(this.inventorySlot, item.copy(1));
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) {
            li.remove();
            this.worker.getWorkInventory().markDirty();
        }
        if (lastItem != null) {
            li.add(lastItem);
            this.worker.getWorkInventory().markDirty();
        }
        return true;
    }
}

