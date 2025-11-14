/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.ExpeditionMission;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.SimplePerformActiveJob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;

public class StartExpeditionActiveJob
extends SimplePerformActiveJob {
    public SettlementMissionBoardMission mission;
    public int cost;

    public StartExpeditionActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, SettlementMissionBoardMission mission, int cost) {
        super(worker, priority);
        this.mission = mission;
        this.cost = cost;
    }

    @Override
    public ActiveJobResult perform() {
        if (!(this.worker instanceof HumanMob)) {
            return ActiveJobResult.FAILED;
        }
        int removedAmount = 0;
        if (this.cost > 0) {
            WorkInventory inventory = this.worker.getWorkInventory();
            ListIterator<InventoryItem> li = inventory.listIterator();
            int coinID = ItemRegistry.getItemID("coin");
            while (li.hasNext()) {
                InventoryItem item = li.next();
                if (item.item.getID() != coinID) continue;
                int missingAmount = this.cost - removedAmount;
                int itemAmount = item.getAmount();
                int toRemove = Math.min(itemAmount, missingAmount);
                removedAmount += toRemove;
                item.setAmount(itemAmount - toRemove);
                if (item.getAmount() > 0) break;
                li.remove();
                break;
            }
        }
        if (removedAmount >= this.cost && ((HumanMob)this.worker).startMission(new ExpeditionMission(this.mission.expedition, removedAmount, this.mission.expedition.getSuccessChance(this.mission.data), true))) {
            this.mission.condition.onJobPerformed();
            return ActiveJobResult.FINISHED;
        }
        if (removedAmount > 0) {
            this.worker.getWorkInventory().add(new InventoryItem("coin", removedAmount));
        }
        return ActiveJobResult.FAILED;
    }
}

