/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.Objects;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public class StaticItemAttackSlot
implements ItemAttackSlot {
    public boolean allowSettingItem;
    protected InventoryItem invItem;

    public StaticItemAttackSlot(InventoryItem invItem, boolean allowSettingItem) {
        Objects.requireNonNull(invItem);
        this.invItem = invItem;
        this.allowSettingItem = allowSettingItem;
    }

    public StaticItemAttackSlot(InventoryItem invItem) {
        this(invItem, false);
    }

    @Override
    public void setItem(InventoryItem item) {
        if (!this.allowSettingItem) {
            return;
        }
        this.invItem = item;
    }

    @Override
    public InventoryItem getItem() {
        return this.invItem;
    }

    @Override
    public boolean isStillValid(ItemAttackerMob attackerMob, InventoryItem item) {
        return this.invItem.item.getID() == item.item.getID();
    }

    @Override
    public boolean isSameSlot(ItemAttackSlot other) {
        if (other instanceof StaticItemAttackSlot) {
            StaticItemAttackSlot staticSlot = (StaticItemAttackSlot)other;
            return this.invItem.item.getID() == staticSlot.invItem.item.getID();
        }
        return false;
    }

    @Override
    public void markDirty() {
    }
}

