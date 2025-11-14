/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class GoldSwordToolItem
extends SwordToolItem {
    public GoldSwordToolItem() {
        super(350, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 105.00003f);
        this.attackRange.setBaseValue(55);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
    }
}

