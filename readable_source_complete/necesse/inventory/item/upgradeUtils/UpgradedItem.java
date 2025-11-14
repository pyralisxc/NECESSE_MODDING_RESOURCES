/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.upgradeUtils;

import java.util.Objects;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;

public class UpgradedItem {
    public final InventoryItem lastItem;
    public final InventoryItem upgradedItem;
    public final Ingredient[] cost;

    public UpgradedItem(InventoryItem lastItem, InventoryItem upgradedItem, Ingredient[] cost) {
        Objects.requireNonNull(lastItem);
        Objects.requireNonNull(upgradedItem);
        Objects.requireNonNull(cost);
        this.lastItem = lastItem;
        this.upgradedItem = upgradedItem;
        this.cost = cost;
    }

    public boolean isSameUpgrade(UpgradedItem other, Level level) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!this.upgradedItem.equals(level, other.upgradedItem, false, false, "equals")) {
            return false;
        }
        if (this.cost.length != other.cost.length) {
            return false;
        }
        for (int i = 0; i < this.cost.length; ++i) {
            if (this.cost[i].equals(other.cost[i])) continue;
            return false;
        }
        return true;
    }

    public static int getEnchantmentMod(InventoryItem item) {
        int mod = 1;
        if (item.item.getUpgradeTier(item) >= 5.0f) {
            mod = 2;
        }
        return mod;
    }
}

