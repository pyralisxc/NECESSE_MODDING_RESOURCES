/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.SettlementRecipeCraftedEvent;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class CraftSettlementRecipeActiveJob
extends TileActiveJob {
    public SettlementWorkstation workstation;
    public SettlementWorkstationRecipe recipe;
    public float workSeconds;
    public GameObjectReservable reservable;
    public Supplier<Boolean> isRemovedCheck;
    public LinkedListJobSequence dropOffSequence;
    public float performedWork;

    public CraftSettlementRecipeActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int tileX, int tileY, SettlementWorkstation workstation, SettlementWorkstationRecipe recipe, float workSeconds, GameObjectReservable reservable, Supplier<Boolean> isRemovedCheck, LinkedListJobSequence dropOffSequence) {
        super(worker, priority, tileX, tileY);
        this.workstation = workstation;
        this.recipe = recipe;
        this.workSeconds = workSeconds;
        this.reservable = reservable;
        this.isRemovedCheck = isRemovedCheck;
        this.dropOffSequence = dropOffSequence;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return new JobMoveToTile(this.tileX, this.tileY, true);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        this.reservable.reserve(this.worker.getMobWorker());
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        if (this.isRemovedCheck != null && this.isRemovedCheck.get().booleanValue() || !this.reservable.isAvailable(this.worker.getMobWorker())) {
            return false;
        }
        SettlementWorkstationLevelObject workstationObject = this.workstation.getWorkstationObject();
        if (workstationObject == null || !workstationObject.canCurrentlyCraft(this.recipe.recipe)) {
            return false;
        }
        if (isCurrent) {
            for (Ingredient ingredient : this.recipe.recipe.ingredients) {
                int totalItems = 0;
                for (InventoryItem item : this.worker.getWorkInventory().items()) {
                    if (this.recipe.canUseItem(ingredient, item)) {
                        totalItems += item.getAmount();
                    }
                    if (totalItems < ingredient.getIngredientAmount()) continue;
                    break;
                }
                if (totalItems >= ingredient.getIngredientAmount()) continue;
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public ActiveJobResult perform() {
        SettlementWorkstationLevelObject workstationObject = this.workstation.getWorkstationObject();
        if (workstationObject != null) {
            workstationObject.tickCrafting(this.recipe.recipe);
        }
        float increase = 0.05f;
        this.performedWork += increase;
        if (this.performedWork < this.workSeconds) {
            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(Arrays.asList(this.recipe.recipe.ingredients));
            while (!ingredients.isEmpty()) {
                int index = GameRandom.globalRandom.nextInt(ingredients.size());
                Ingredient ingredient = ingredients.remove(index);
                for (InventoryItem item : this.worker.getWorkInventory().items()) {
                    if (!this.recipe.canUseItem(ingredient, item)) continue;
                    this.worker.showWorkAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, item.item, 1000);
                    return ActiveJobResult.PERFORMING;
                }
            }
            this.worker.showWorkAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, this.recipe.recipe.resultItem.item, 1000);
            return ActiveJobResult.PERFORMING;
        }
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        ArrayList<InventoryItemsRemoved> usedItems = new ArrayList<InventoryItemsRemoved>();
        block2: for (Ingredient ingredient : this.recipe.recipe.ingredients) {
            ListIterator<InventoryItem> li = this.worker.getWorkInventory().listIterator();
            int ingredientsNeeded = ingredient.getIngredientAmount();
            while (li.hasNext()) {
                final InventoryItem item = li.next();
                if (this.recipe.canUseItem(ingredient, item)) {
                    final int used = Math.min(ingredientsNeeded, item.getAmount());
                    ingredientsNeeded -= used;
                    item.setAmount(item.getAmount() - used);
                    usedItems.add(new InventoryItemsRemoved(null, -1, item, used){

                        @Override
                        public void revert() {
                            CraftSettlementRecipeActiveJob.this.worker.getWorkInventory().add(item.copy(used));
                        }
                    });
                    if (item.getAmount() <= 0) {
                        li.remove();
                    }
                }
                if (ingredientsNeeded > 0) continue;
                continue block2;
            }
        }
        SettlementRecipeCraftedEvent event = new SettlementRecipeCraftedEvent(this.recipe.recipe, usedItems, this);
        this.recipe.recipe.submitCraftedEvent(event);
        this.worker.getWorkInventory().add(event.resultItem);
        this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, event.resultItem.item, 250);
        this.recipe.onCrafted(this.workstation, 1);
        if (this.dropOffSequence != null) {
            ArrayList<HasStorageLevelJob.DropOffFind> dropOffLocations = HasStorageLevelJob.findDropOffLocation(this.worker, event.resultItem);
            for (HasStorageLevelJob.DropOffFind dropOffLocation : dropOffLocations) {
                this.dropOffSequence.addLast(dropOffLocation.getActiveJob(this.worker, this.priority, false));
            }
        }
        return ActiveJobResult.FINISHED;
    }
}

