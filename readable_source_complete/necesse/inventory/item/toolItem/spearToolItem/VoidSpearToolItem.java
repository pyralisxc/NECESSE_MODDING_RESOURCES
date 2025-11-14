/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;

public class VoidSpearToolItem
extends SpearToolItem {
    public VoidSpearToolItem() {
        super(650, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(28.0f).setUpgradedValue(1.0f, 70.000015f);
        this.attackRange.setBaseValue(140);
        this.knockback.setBaseValue(25);
        this.canBeUsedForRaids = true;
        this.useForRaidsOnlyIfObtained = true;
    }
}

