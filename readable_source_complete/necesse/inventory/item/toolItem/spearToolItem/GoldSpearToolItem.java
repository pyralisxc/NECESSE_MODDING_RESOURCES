/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;

public class GoldSpearToolItem
extends SpearToolItem {
    public GoldSpearToolItem() {
        super(350, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(23.0f).setUpgradedValue(1.0f, 74.66669f);
        this.attackRange.setBaseValue(110);
        this.knockback.setBaseValue(25);
        this.canBeUsedForRaids = true;
    }
}

