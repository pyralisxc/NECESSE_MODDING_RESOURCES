/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeCraftedEvent;

public class ProcessingTechRecipeCraftedEvent
extends RecipeCraftedEvent {
    public final ProcessingTechInventoryObjectEntity entity;

    public ProcessingTechRecipeCraftedEvent(Recipe recipe, ArrayList<InventoryItemsRemoved> itemsUsed, ProcessingTechInventoryObjectEntity entity) {
        super(recipe, itemsUsed);
        this.entity = entity;
    }
}

