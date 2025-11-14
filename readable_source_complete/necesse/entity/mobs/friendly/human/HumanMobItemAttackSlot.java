/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;

public class HumanMobItemAttackSlot
implements ItemAttackSlot {
    public final HumanMob mob;

    public HumanMobItemAttackSlot(HumanMob mob) {
        this.mob = mob;
    }

    @Override
    public void setItem(InventoryItem item) {
        this.mob.equipmentInventory.setItem(6, item);
    }

    @Override
    public InventoryItem getItem() {
        return this.mob.equipmentInventory.getItem(6);
    }

    @Override
    public boolean isStillValid(ItemAttackerMob attackerMob, InventoryItem invItem) {
        ItemAttackSlot slot = attackerMob.getCurrentSelectedAttackSlot();
        if (slot instanceof HumanMobItemAttackSlot) {
            HumanMobItemAttackSlot humanSlot = (HumanMobItemAttackSlot)slot;
            if (humanSlot.mob.getUniqueID() != this.mob.getUniqueID()) {
                return false;
            }
            InventoryItem currentItem = this.getItem();
            return currentItem != null && currentItem.item.getID() == invItem.item.getID();
        }
        return false;
    }

    @Override
    public boolean isSameSlot(ItemAttackSlot other) {
        if (other instanceof HumanMobItemAttackSlot) {
            HumanMobItemAttackSlot humanSlot = (HumanMobItemAttackSlot)other;
            return humanSlot.mob.getUniqueID() == this.mob.getUniqueID();
        }
        return false;
    }

    @Override
    public void markDirty() {
        this.mob.equipmentInventory.markDirty(6);
    }

    public String toString() {
        return super.toString() + "{" + this.mob.getDisplayName() + "}";
    }
}

