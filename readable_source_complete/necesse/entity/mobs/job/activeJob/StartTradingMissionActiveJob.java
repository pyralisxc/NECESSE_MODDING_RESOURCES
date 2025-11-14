/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.TradingMission;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.SimplePerformActiveJob;
import necesse.inventory.InventoryItem;

public class StartTradingMissionActiveJob
extends SimplePerformActiveJob {
    public ArrayList<AtomicReference<InventoryItem>> items;

    public StartTradingMissionActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, ArrayList<AtomicReference<InventoryItem>> items) {
        super(worker, priority);
        this.items = items;
    }

    @Override
    public ActiveJobResult perform() {
        if (!(this.worker instanceof HumanMob)) {
            return ActiveJobResult.FAILED;
        }
        WorkInventory workInventory = this.worker.getWorkInventory();
        if (workInventory == null) {
            return ActiveJobResult.FAILED;
        }
        ArrayList<InventoryItem> foundItems = new ArrayList<InventoryItem>();
        for (AtomicReference<InventoryItem> itemRef : this.items) {
            InventoryItem item = itemRef.get();
            if (item == null) continue;
            ListIterator<InventoryItem> li = workInventory.listIterator();
            boolean found = false;
            while (li.hasNext()) {
                InventoryItem workItem = li.next();
                if (workItem.getAmount() < item.getAmount() || !workItem.equals(this.worker.getLevel(), item, true, false, "equals")) continue;
                foundItems.add(workItem.copy(item.getAmount()));
                workItem.setAmount(workItem.getAmount() - item.getAmount());
                if (workItem.getAmount() <= 0) {
                    li.remove();
                }
                workInventory.markDirty();
                found = true;
                break;
            }
            if (found) continue;
            for (InventoryItem foundItem : foundItems) {
                workInventory.add(foundItem);
            }
            return ActiveJobResult.FAILED;
        }
        float totalBrokerValue = 0.0f;
        for (InventoryItem foundItem : foundItems) {
            totalBrokerValue += foundItem.getBrokerValue();
        }
        if (((HumanMob)this.worker).startMission(new TradingMission(foundItems, (int)totalBrokerValue, true))) {
            return ActiveJobResult.FINISHED;
        }
        for (InventoryItem foundItem : foundItems) {
            workInventory.add(foundItem);
        }
        return ActiveJobResult.FINISHED;
    }
}

