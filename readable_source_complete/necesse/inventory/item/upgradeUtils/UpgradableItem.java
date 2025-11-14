/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.upgradeUtils;

import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.UpgradedItem;

public interface UpgradableItem {
    default public String getCanBeUpgradedError(InventoryItem item) {
        return null;
    }

    public void addUpgradeStatTips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, ItemAttackerMob var4, ItemAttackerMob var5);

    public UpgradedItem getUpgradedItem(InventoryItem var1);
}

