/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class BrokenKora
extends AphSwordToolItem {
    public BrokenKora() {
        super(1000);
        this.rarity = Item.Rarity.NORMAL;
        this.attackDamage.setBaseValue(50.0f);
        this.knockback.setBaseValue(50);
        this.attackRange.setBaseValue(40);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
    }

    public boolean isEnchantable(InventoryItem item) {
        return false;
    }
}

