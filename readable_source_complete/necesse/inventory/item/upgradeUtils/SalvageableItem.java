/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.upgradeUtils;

import java.util.Collection;
import necesse.engine.localization.Localization;
import necesse.inventory.InventoryItem;

public interface SalvageableItem {
    default public String getCanBeSalvagedError(InventoryItem item) {
        if (item.item.getUpgradeTier(item) < 1.0f) {
            return Localization.translate("ui", "itemnotsalvageable");
        }
        return null;
    }

    public Collection<InventoryItem> getSalvageRewards(InventoryItem var1);
}

