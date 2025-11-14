/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Recipe;

public class RecipeCraftedEvent {
    public final Recipe recipe;
    public InventoryItem resultItem;
    public ArrayList<InventoryItemsRemoved> itemsUsed;

    public RecipeCraftedEvent(Recipe recipe, ArrayList<InventoryItemsRemoved> itemsUsed) {
        this.recipe = recipe;
        this.resultItem = recipe.resultItem.copy(recipe.resultAmount);
        this.itemsUsed = itemsUsed;
    }
}

