/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class PlayerInventoryItemAttackSlot
implements ItemAttackSlot {
    public final PlayerMob player;
    public final PlayerInventorySlot slot;

    public PlayerInventoryItemAttackSlot(PlayerMob player, PlayerInventorySlot slot) {
        this.player = player;
        this.slot = slot;
    }

    @Override
    public void setItem(InventoryItem item) {
        this.slot.setItem(this.player.getInv(), item);
    }

    @Override
    public InventoryItem getItem() {
        return this.slot.getItem(this.player.getInv());
    }

    @Override
    public boolean isStillValid(ItemAttackerMob attackerMob, InventoryItem item) {
        ItemAttackSlot slot = attackerMob.getCurrentSelectedAttackSlot();
        if (slot instanceof PlayerInventoryItemAttackSlot) {
            PlayerInventoryItemAttackSlot playerSlot = (PlayerInventoryItemAttackSlot)slot;
            if (this.player.getUniqueID() != playerSlot.player.getUniqueID()) {
                return false;
            }
            if (!this.slot.equals(playerSlot.slot)) {
                return false;
            }
            InventoryItem currentItem = this.getItem();
            return currentItem != null && currentItem.item.getID() == item.item.getID();
        }
        return false;
    }

    @Override
    public boolean isSameSlot(ItemAttackSlot other) {
        if (other instanceof PlayerInventoryItemAttackSlot) {
            PlayerInventoryItemAttackSlot playerSlot = (PlayerInventoryItemAttackSlot)other;
            return this.player.getUniqueID() == playerSlot.player.getUniqueID() && this.slot.equals(playerSlot.slot);
        }
        return false;
    }

    @Override
    public void markDirty() {
        this.slot.markDirty(this.player.getInv());
    }

    public String toString() {
        return super.toString() + "{" + this.player.getDisplayName() + ", " + this.slot.inventoryID + ", " + this.slot.slot + "}";
    }
}

