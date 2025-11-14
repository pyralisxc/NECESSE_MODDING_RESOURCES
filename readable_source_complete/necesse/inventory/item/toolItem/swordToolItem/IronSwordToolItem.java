/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class IronSwordToolItem
extends SwordToolItem {
    public IronSwordToolItem() {
        super(300, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(23.0f).setUpgradedValue(1.0f, 107.33336f);
        this.attackRange.setBaseValue(50);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
    }
}

