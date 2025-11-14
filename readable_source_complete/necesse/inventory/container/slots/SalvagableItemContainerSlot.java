/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.upgradeUtils.SalvageableItem;

public class SalvagableItemContainerSlot
extends ContainerSlot {
    public SalvagableItemContainerSlot(Inventory inventory, int inventorySlot) {
        super(inventory, inventorySlot);
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item == null) {
            return null;
        }
        if (item.item instanceof SalvageableItem) {
            return ((SalvageableItem)((Object)item.item)).getCanBeSalvagedError(item);
        }
        return Localization.translate("ui", "itemnotsalvageable");
    }
}

