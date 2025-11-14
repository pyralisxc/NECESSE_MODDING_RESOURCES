/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.objectEntity.FueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeCraftedEvent;

public class FueledProcessingTechRecipeCraftedEvent
extends RecipeCraftedEvent {
    public final FueledProcessingTechInventoryObjectEntity entity;

    public FueledProcessingTechRecipeCraftedEvent(Recipe recipe, ArrayList<InventoryItemsRemoved> itemsUsed, FueledProcessingTechInventoryObjectEntity entity) {
        super(recipe, itemsUsed);
        this.entity = entity;
    }
}

