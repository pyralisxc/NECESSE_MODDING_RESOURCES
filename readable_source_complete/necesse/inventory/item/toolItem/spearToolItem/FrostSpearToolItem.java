/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;

public class FrostSpearToolItem
extends SpearToolItem {
    public FrostSpearToolItem() {
        super(500, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 72.33335f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(25);
        this.canBeUsedForRaids = true;
    }
}

