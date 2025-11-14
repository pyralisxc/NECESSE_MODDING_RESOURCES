/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.ItemAttackerWeaponItem;

public class SettlerWeaponContainerSlot
extends ContainerSlot {
    public HumanMob mob;

    public SettlerWeaponContainerSlot(Inventory inventory, int inventorySlot, HumanMob mob) {
        super(inventory, inventorySlot);
        this.mob = mob;
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (item != null) {
            if (item.item instanceof ItemAttackerWeaponItem) {
                GameMessage settlerCanUseError = ((ItemAttackerWeaponItem)((Object)item.item)).getItemAttackerCanUseError(this.mob, item);
                return settlerCanUseError == null ? null : settlerCanUseError.translate();
            }
            return Localization.translate("ui", "settlercantuseitem");
        }
        return null;
    }
}

