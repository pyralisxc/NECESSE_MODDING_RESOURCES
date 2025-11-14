/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.container.Container;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeCraftedEvent;

public class ContainerRecipeCraftedEvent
extends RecipeCraftedEvent {
    public final Container container;

    public ContainerRecipeCraftedEvent(Recipe recipe, ArrayList<InventoryItemsRemoved> itemsUsed, Container container) {
        super(recipe, itemsUsed);
        this.container = container;
    }
}

