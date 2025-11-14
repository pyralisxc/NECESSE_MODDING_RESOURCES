/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.mobs.job.activeJob.CraftSettlementRecipeActiveJob;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeCraftedEvent;

public class SettlementRecipeCraftedEvent
extends RecipeCraftedEvent {
    public final CraftSettlementRecipeActiveJob job;

    public SettlementRecipeCraftedEvent(Recipe recipe, ArrayList<InventoryItemsRemoved> itemsUsed, CraftSettlementRecipeActiveJob job) {
        super(recipe, itemsUsed);
        this.job = job;
    }
}

