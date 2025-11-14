/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public class AncestorWandToolItem
extends SwordToolItem {
    public AncestorWandToolItem() {
        super(1750, null);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(56.0f).setUpgradedValue(1.0f, 80.0f);
        this.attackRange.setBaseValue(80);
        this.knockback.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
        this.canBeUsedForRaids = false;
    }
}

