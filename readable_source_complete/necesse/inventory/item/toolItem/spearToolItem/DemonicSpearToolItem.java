/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;

public class DemonicSpearToolItem
extends SpearToolItem {
    public DemonicSpearToolItem() {
        super(400, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(450);
        this.attackDamage.setBaseValue(24.0f).setUpgradedValue(1.0f, 70.000015f);
        this.attackRange.setBaseValue(130);
        this.knockback.setBaseValue(25);
        this.canBeUsedForRaids = true;
    }
}

