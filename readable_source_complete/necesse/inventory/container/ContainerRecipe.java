/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import necesse.inventory.recipe.Recipe;

public class ContainerRecipe {
    public final int id;
    public final Recipe recipe;
    public final boolean isInventory;

    public ContainerRecipe(int id, Recipe recipe, boolean isInventory) {
        this.recipe = recipe;
        this.id = id;
        this.isInventory = isInventory;
    }
}

