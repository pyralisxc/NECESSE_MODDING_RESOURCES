/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.EnchantableSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;

public class EnchantableSpecificSlot
extends EnchantableSlot {
    public ItemEnchantment enchantment;

    public EnchantableSpecificSlot(Inventory inventory, int inventorySlot, ItemEnchantment enchantment) {
        super(inventory, inventorySlot);
        this.enchantment = enchantment;
    }

    @Override
    public String getItemInvalidError(InventoryItem item) {
        if (this.enchantment == null) {
            return "";
        }
        String superInvalid = super.getItemInvalidError(item);
        if (superInvalid != null) {
            return superInvalid;
        }
        if (item == null || ((Enchantable)((Object)item.item)).isValidEnchantment(item, this.enchantment)) {
            return null;
        }
        return Localization.translate("ui", "enchantingscrollwrongtype", "item", ItemRegistry.getLocalization(item.item.getID()), "enchantment", this.enchantment.getLocalization());
    }
}

