/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;

public class DresserObjectEntity
extends InventoryObjectEntity {
    public static final int SETS = 10;

    public DresserObjectEntity(Level level, int x, int y) {
        super(level, x, y, 30);
    }

    public static ArmorItem.ArmorType getArmorType(int slot) {
        int slotDiv = slot / 10;
        if (slotDiv == 0) {
            return ArmorItem.ArmorType.HEAD;
        }
        if (slotDiv == 1) {
            return ArmorItem.ArmorType.CHEST;
        }
        if (slotDiv == 2) {
            return ArmorItem.ArmorType.FEET;
        }
        return null;
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        ArmorItem.ArmorType armorType;
        ArmorItem armorItem;
        if (item != null && item.item.isArmorItem() && (armorItem = (ArmorItem)item.item).canMobEquip(null, item) && (armorType = DresserObjectEntity.getArmorType(slot)) != null) {
            return armorItem.armorType == armorType;
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

