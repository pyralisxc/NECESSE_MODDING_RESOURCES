/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;

public class ArmorStandObjectEntity
extends InventoryObjectEntity {
    public ArmorStandObjectEntity(Level level, int x, int y) {
        super(level, x, y, 3);
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        ArmorItem armorItem;
        if (item != null && item.item.isArmorItem() && (armorItem = (ArmorItem)item.item).canMobEquip(null, item)) {
            if (slot == 0) {
                return armorItem.armorType == ArmorItem.ArmorType.HEAD;
            }
            if (slot == 1) {
                return armorItem.armorType == ArmorItem.ArmorType.CHEST;
            }
            if (slot == 2) {
                return armorItem.armorType == ArmorItem.ArmorType.FEET;
            }
        }
        return true;
    }

    @Override
    public boolean isSettlementStorageItemDisabled(Item item) {
        return !item.isArmorItem();
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }
}

