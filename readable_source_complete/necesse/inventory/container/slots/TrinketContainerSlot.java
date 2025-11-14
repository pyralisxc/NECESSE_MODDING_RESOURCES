/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.trinketItem.TrinketItem;

public class TrinketContainerSlot
extends ContainerSlot {
    public TrinketContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item == null) {
            return null;
        }
        if (item.item.isTrinketItem()) {
            ContainerSlot slot;
            TrinketItem trinketItem = (TrinketItem)item.item;
            String invalidInSlotError = trinketItem.getInvalidInSlotError(this.getContainer(), this, item);
            if (invalidInSlotError != null) {
                return invalidInSlotError;
            }
            if (trinketItem.isAbilityTrinket(item) && (slot = this.getContainer().getSlot(this.getContainer().CLIENT_TRINKET_ABILITY_SLOT)).isClear()) {
                return Localization.translate("itemtooltip", "useabilityslot");
            }
            return null;
        }
        return "";
    }

    @Override
    public int getItemStackLimit(InventoryItem item) {
        return 1;
    }
}

