/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;

public class CraftingStationUpgrade {
    public final GameObject upgradeObject;
    public final Ingredient[] cost;

    public CraftingStationUpgrade(GameObject upgradeObject, Ingredient ... cost) {
        this.upgradeObject = upgradeObject;
        this.cost = cost;
    }
}

