/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class DemonicSwordToolItem
extends SwordToolItem {
    public DemonicSwordToolItem() {
        super(400, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(34.0f).setUpgradedValue(1.0f, 110.83337f);
        this.attackRange.setBaseValue(65);
        this.knockback.setBaseValue(80);
        this.canBeUsedForRaids = true;
    }
}

