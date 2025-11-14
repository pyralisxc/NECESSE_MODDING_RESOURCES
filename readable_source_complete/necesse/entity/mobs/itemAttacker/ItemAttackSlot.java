/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public interface ItemAttackSlot {
    public void setItem(InventoryItem var1);

    public InventoryItem getItem();

    public boolean isStillValid(ItemAttackerMob var1, InventoryItem var2);

    public boolean isSameSlot(ItemAttackSlot var1);

    public void markDirty();
}

