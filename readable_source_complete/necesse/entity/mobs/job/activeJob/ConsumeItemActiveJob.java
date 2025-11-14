/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.UseItemActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;

public class ConsumeItemActiveJob
extends UseItemActiveJob {
    public HungerMob hungerMob;

    public ConsumeItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> item, HungerMob hungerMob) {
        super(worker, priority, item);
        this.hungerMob = hungerMob;
    }

    public ConsumeItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, InventoryItem item, HungerMob hungerMob) {
        super(worker, priority, item);
        this.hungerMob = hungerMob;
    }

    @Override
    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
        if (item.item.isFoodItem()) {
            FoodConsumableItem foodItem = (FoodConsumableItem)item.item;
            Mob mob = this.worker.getMobWorker();
            this.worker.showPickupAnimation(this.worker.getMobWorker().getDir() == 3 ? mob.getX() - 10 : mob.getX() + 10, mob.getY(), foodItem, 200);
            this.hungerMob.useFoodItem(foodItem, true);
            if (foodItem.isSingleUse(null)) {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) {
                    li.remove();
                    this.worker.getWorkInventory().markDirty();
                }
            }
            return true;
        }
        return false;
    }
}

