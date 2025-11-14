/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class FrostSwordToolItem
extends SwordToolItem {
    public FrostSwordToolItem() {
        super(500, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 101.50003f);
        this.attackRange.setBaseValue(60);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
    }
}

