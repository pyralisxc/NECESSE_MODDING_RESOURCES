/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.CraftSettlementRecipeActiveJob;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.SimplePerformActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;
import necesse.level.maps.levelData.settlementData.StorageDropOff;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class UseWorkstationLevelJob
extends LevelJob {
    public SettlementWorkstation workstation;
    private Supplier<Boolean> validCheck;

    public UseWorkstationLevelJob(SettlementWorkstation workstation, Supplier<Boolean> validCheck) {
        super(workstation.tileX, workstation.tileY);
        this.workstation = workstation;
        this.validCheck = validCheck;
    }

    public UseWorkstationLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isValid() {
        GameObject object = this.getLevel().getObject(this.tileX, this.tileY);
        return object instanceof SettlementWorkstationObject && this.validCheck.get() != false;
    }

    public static <T extends UseWorkstationLevelJob> JobSequence getJobSequence(EntityJobWorker worker, final FoundJob<T> foundJob) {
        SettlementWorkstationLevelObject workstationObject = ((UseWorkstationLevelJob)foundJob.job).workstation.getWorkstationObject();
        if (workstationObject != null) {
            for (final SettlementWorkstationRecipe recipe : ((UseWorkstationLevelJob)foundJob.job).workstation.recipes) {
                if (!workstationObject.canCurrentlyCraft(recipe.recipe)) continue;
                int countToCraft = 0;
                switch (recipe.mode) {
                    case DO_COUNT: {
                        countToCraft = recipe.modeCount;
                        break;
                    }
                    case DO_UNTIL: {
                        if (recipe.modeCount == 0) break;
                        int itemsClose = HasStorageLevelJob.getItemCount(worker, item -> item.equals(workstationObject.level, recipe.recipe.resultItem, true, true, "equals"), recipe.modeCount, true, true);
                        countToCraft = recipe.modeCount - itemsClose;
                        break;
                    }
                    case DO_FOREVER: {
                        countToCraft = Integer.MAX_VALUE;
                    }
                }
                if (countToCraft <= 0) continue;
                int maxCrafts = Math.min(workstationObject.getMaxCraftsAtOnce(recipe.recipe), countToCraft);
                int totalCrafts = 0;
                boolean isProcessingWorkstation = ((UseWorkstationLevelJob)foundJob.job).workstation.isProcessingWorkstation();
                SettlementInventory dropOffInventory = null;
                if (isProcessingWorkstation && (dropOffInventory = ((UseWorkstationLevelJob)foundJob.job).workstation.getProcessingInputInventory()) == null) continue;
                ArrayList<SettlementStoragePickupSlot> pickups = new ArrayList<SettlementStoragePickupSlot>();
                ArrayList<StorageDropOff> dropOffs = new ArrayList<StorageDropOff>();
                ArrayList<InventoryItem> tempWorkInventory = new ArrayList<InventoryItem>(worker.getWorkInventory().getTotalItemStacks());
                for (InventoryItem item2 : worker.getWorkInventory().items()) {
                    tempWorkInventory.add(item2.copy());
                }
                for (int i = 0; i < maxCrafts; ++i) {
                    boolean valid = true;
                    for (Ingredient ingredient : recipe.recipe.ingredients) {
                        ArrayList<InventoryItem> hasIngredientsItems = new ArrayList<InventoryItem>();
                        int hasIngredients = 0;
                        ListIterator li = tempWorkInventory.listIterator();
                        while (li.hasNext()) {
                            InventoryItem item3 = (InventoryItem)li.next();
                            if (!recipe.canUseItem(ingredient, item3)) continue;
                            int required = ingredient.getIngredientAmount() - hasIngredients;
                            InventoryItem taken = item3.copy(Math.min(required, item3.getAmount()));
                            hasIngredientsItems.add(taken);
                            hasIngredients += taken.getAmount();
                            item3.setAmount(item3.getAmount() - taken.getAmount());
                            if (item3.getAmount() <= 0) {
                                li.remove();
                            }
                            if (hasIngredients < ingredient.getIngredientAmount()) continue;
                            break;
                        }
                        if (hasIngredients < ingredient.getIngredientAmount()) {
                            SettlementStorageRecords storageRecords = PickupSettlementStorageActiveJob.getStorageRecords(worker);
                            if (storageRecords != null) {
                                int required = ingredient.getIngredientAmount() - hasIngredients;
                                LinkedList<SettlementStoragePickupSlot> slots = ingredient.isGlobalIngredient() ? storageRecords.getIndex(SettlementStorageGlobalIngredientIDIndex.class).findPickupSlots(ingredient.getIngredientID(), worker, item -> recipe.ingredientFilter.isItemAllowed(item.item), required, required) : storageRecords.getIndex(SettlementStorageItemIDIndex.class).findPickupSlots(ingredient.getIngredientID(), worker, null, required, required);
                                if (slots != null) {
                                    if (isProcessingWorkstation) {
                                        int added;
                                        int totalAdded = 0;
                                        for (InventoryItem hasItem : hasIngredientsItems) {
                                            added = dropOffInventory.canAddFutureDropOff(hasItem);
                                            if (added > 0) {
                                                totalAdded += added;
                                                dropOffs.add(dropOffInventory.addFutureDropOff(() -> hasItem));
                                                continue;
                                            }
                                            valid = false;
                                            break;
                                        }
                                        for (SettlementStoragePickupSlot pickup : slots) {
                                            added = dropOffInventory.canAddFutureDropOff(pickup.item);
                                            if (added > 0) {
                                                dropOffs.add(dropOffInventory.addFutureDropOff(() -> pickup.item));
                                                if ((totalAdded += added) < ingredient.getIngredientAmount()) continue;
                                                break;
                                            }
                                            valid = false;
                                            break;
                                        }
                                        if (!valid || totalAdded < ingredient.getIngredientAmount()) {
                                            valid = false;
                                            break;
                                        }
                                    }
                                    pickups.addAll(slots);
                                    continue;
                                }
                                valid = false;
                                break;
                            }
                            valid = false;
                            break;
                        }
                        if (!isProcessingWorkstation) continue;
                        int totalAdded = 0;
                        for (InventoryItem hasItem : hasIngredientsItems) {
                            int added = dropOffInventory.canAddFutureDropOff(hasItem);
                            if (added > 0) {
                                totalAdded += added;
                                dropOffs.add(dropOffInventory.addFutureDropOff(() -> hasItem));
                                continue;
                            }
                            valid = false;
                            break;
                        }
                        if (totalAdded >= ingredient.getIngredientAmount()) continue;
                        valid = false;
                        break;
                    }
                    if (!valid) break;
                    ++totalCrafts;
                }
                if (totalCrafts > 0) {
                    GameMessage targetDescription = workstationObject.object.getLocalization();
                    GameMessage itemDescription = recipe.name != null && !recipe.name.isEmpty() ? new StaticMessage(recipe.name) : recipe.recipe.resultItem.getItemLocalization();
                    LocalMessage activityDescription = new LocalMessage("activities", "crafting", "item", itemDescription, "target", targetDescription);
                    LinkedListJobSequence sequence = new LinkedListJobSequence(activityDescription);
                    for (SettlementStoragePickupSlot slot : pickups) {
                        sequence.add(slot.toPickupJob(worker, foundJob.priority));
                    }
                    if (isProcessingWorkstation) {
                        for (StorageDropOff dropOff : dropOffs) {
                            sequence.add(new DropOffSettlementStorageActiveJob(worker, foundJob.priority, ((UseWorkstationLevelJob)foundJob.job).reservable, ((UseWorkstationLevelJob)foundJob.job)::isRemoved, true, dropOff));
                        }
                        sequence.add(new SimplePerformActiveJob(worker, null){

                            @Override
                            public ActiveJobResult perform() {
                                recipe.onCrafted(((UseWorkstationLevelJob)foundJob.job).workstation, 1);
                                return ActiveJobResult.FINISHED;
                            }
                        });
                    } else {
                        for (int i = 0; i < totalCrafts; ++i) {
                            sequence.add(((UseWorkstationLevelJob)foundJob.job).getCraftActiveJob(worker, foundJob.priority, recipe, sequence));
                        }
                    }
                    return sequence;
                }
                pickups.forEach(SettlementStoragePickupSlot::remove);
                dropOffs.forEach(StorageDropOff::remove);
            }
        }
        return null;
    }

    public ActiveJob getCraftActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, SettlementWorkstationRecipe recipe, LinkedListJobSequence dropOffSequence) {
        float secondsToComplete = (float)Math.pow(recipe.recipe.ingredients.length, 0.7f) + 1.0f;
        return new CraftSettlementRecipeActiveJob(worker, priority, this.tileX, this.tileY, this.workstation, recipe, secondsToComplete, this.reservable, this::isRemoved, dropOffSequence);
    }
}

