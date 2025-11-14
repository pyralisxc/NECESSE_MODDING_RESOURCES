/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.SlowlyConsumeItemActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageFoodQualityIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class ConsumeFoodLevelJob
extends LevelJob {
    public JobTypeHandler.SubHandler<?> handler;
    public ItemCategoriesFilter dietFilter;

    public ConsumeFoodLevelJob(int tileX, int tileY, JobTypeHandler.SubHandler<?> handler, ItemCategoriesFilter dietFilter) {
        super(tileX, tileY);
        this.handler = handler;
        this.dietFilter = dietFilter;
    }

    public ConsumeFoodLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isWithinRestrictZone(ZoneTester zone) {
        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isSameJob(LevelJob other) {
        return other.getID() == this.getID();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int getFirstPriority() {
        return Integer.MAX_VALUE;
    }

    public static <T extends ConsumeFoodLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob, HungerMob hungerMob, Set<Integer> recentlyEatenItemIDs) {
        Predicate<InventoryItem> itemFilter = item -> {
            if (((ConsumeFoodLevelJob)foundJob.job).dietFilter != null && !((ConsumeFoodLevelJob)foundJob.job).dietFilter.isItemAllowed(item.item)) {
                return false;
            }
            if (recentlyEatenItemIDs != null && recentlyEatenItemIDs.contains(item.item.getID())) {
                return false;
            }
            if (item.item.isFoodItem()) {
                FoodConsumableItem foodItem = (FoodConsumableItem)item.item;
                return foodItem.nutrition > 0 && foodItem.quality != null;
            }
            return false;
        };
        InventoryItem workerItem = worker.getWorkInventory().stream().filter(itemFilter).max(Comparator.comparingInt(i -> ((FoodConsumableItem)i.item).quality.happinessIncrease)).orElse(null);
        FoodConsumableItem bestWorkerItem = workerItem == null ? null : (FoodConsumableItem)workerItem.item;
        SettlementStorageRecords storageRecords = PickupSettlementStorageActiveJob.getStorageRecords(worker);
        if (storageRecords != null) {
            for (FoodQuality quality : Settler.foodQualities.descendingSet()) {
                SettlementStoragePickupSlot slot;
                if (bestWorkerItem != null && bestWorkerItem.quality.happinessIncrease >= quality.happinessIncrease) break;
                SettlementStorageRecordsRegionData data = storageRecords.getIndex(SettlementStorageFoodQualityIndex.class).getFoodQuality(quality);
                if (data == null || (slot = data.startFinder(worker).findFirstItemPickup(itemFilter)) == null) continue;
                LocalMessage activityDescription = new LocalMessage("activities", "consuming", "item", slot.item.getItemLocalization());
                LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
                AtomicReference<InventoryItem> pickedUpItem = new AtomicReference<InventoryItem>();
                sequence.add(slot.toPickupJob(worker, foundJob.priority, pickedUpItem));
                sequence.add(new SlowlyConsumeItemActiveJob(worker, foundJob.priority, pickedUpItem, hungerMob, 10000));
                hungerMob.removeHungryNotification();
                return sequence;
            }
        }
        if (workerItem != null) {
            LocalMessage activityDescription = new LocalMessage("activities", "consuming", "item", workerItem.copy(1).getItemLocalization());
            hungerMob.removeHungryNotification();
            return new SingleJobSequence(new SlowlyConsumeItemActiveJob(worker, foundJob.priority, workerItem.copy(1), hungerMob, 10000), activityDescription);
        }
        if (recentlyEatenItemIDs != null && !recentlyEatenItemIDs.isEmpty()) {
            return ConsumeFoodLevelJob.getJobSequence(worker, foundJob, hungerMob, null);
        }
        hungerMob.submitHungryNotification();
        return null;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends ConsumeFoodLevelJob> getJobStreamer(Supplier<ItemCategoriesFilter> dietFilterSupplier) {
        return (worker, handler) -> {
            ItemCategoriesFilter dietFilter = (ItemCategoriesFilter)dietFilterSupplier.get();
            if (dietFilter == null || !dietFilter.master.isAnyAllowed()) {
                return Stream.empty();
            }
            Mob mobWorker = worker.getMobWorker();
            return Stream.of(new ConsumeFoodLevelJob(mobWorker.getTileX(), mobWorker.getTileY(), handler, dietFilter));
        };
    }
}

