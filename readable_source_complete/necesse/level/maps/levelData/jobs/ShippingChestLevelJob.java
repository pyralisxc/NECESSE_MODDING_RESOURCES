/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.TradingMission;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.StartTradingMissionActiveJob;
import necesse.entity.objectEntity.ShippingChestObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.container.ShippingChestObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.MineObjectLevelJob;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;

public class ShippingChestLevelJob
extends MineObjectLevelJob {
    public ShippingChestLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public ShippingChestLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValidObject(LevelObject object) {
        return object.object instanceof ShippingChestObject;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public int getAfterPrioritizedPriority() {
        return 1000;
    }

    public static <T extends ShippingChestLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        ShippingChestObjectEntity objectEntity;
        Level level = worker.getLevel();
        ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), ((ShippingChestLevelJob)foundJob.job).tileX, ((ShippingChestLevelJob)foundJob.job).tileY);
        if (settlement != null && (objectEntity = ((ShippingChestLevelJob)foundJob.job).getObject().getCurrentObjectEntity(ShippingChestObjectEntity.class)) != null) {
            LevelStorage storage = settlement.storageManager.getStorage(((ShippingChestLevelJob)foundJob.job).tileX, ((ShippingChestLevelJob)foundJob.job).tileY);
            if (storage == null) {
                storage = objectEntity.nonSettlementStorage;
            }
            ArrayList<SettlementStoragePickupSlot> pickups = new ArrayList<SettlementStoragePickupSlot>();
            int addedStacks = 0;
            List slots = storage.findFutureUnreservedSlots().collect(Collectors.toList());
            for (SettlementStoragePickupFuture slot : slots) {
                pickups.add(slot.accept(slot.item.getAmount()));
                ++addedStacks;
            }
            if (addedStacks < objectEntity.startMissionWhenCarryingAtLeastStacks) {
                return null;
            }
            LocalMessage activityDescription = new LocalMessage("activities", "goingmission", "mission", TradingMission.getMissionName());
            GameLinkedListJobSequence sequence = new GameLinkedListJobSequence(activityDescription);
            ArrayList<AtomicReference<InventoryItem>> pickedUpItems = new ArrayList<AtomicReference<InventoryItem>>();
            for (SettlementStoragePickupSlot pickup : pickups) {
                AtomicReference<InventoryItem> itemRef = new AtomicReference<InventoryItem>();
                sequence.add(pickup.toPickupJob(worker, foundJob.priority, itemRef));
                pickedUpItems.add(itemRef);
            }
            sequence.add(new StartTradingMissionActiveJob(worker, foundJob.priority, pickedUpItems));
            worker.setPrioritizeNextJob(HasStorageLevelJob.class, false);
            return sequence;
        }
        return null;
    }
}

