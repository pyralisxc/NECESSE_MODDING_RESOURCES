/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;

public class FrostGreatswordToolItem
extends GreatswordToolItem {
    public FrostGreatswordToolItem() {
        super(500, GreatswordWeaponsLootTable.greatswordWeapons, FrostGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(75.0f).setUpgradedValue(1.0f, 184.33339f);
        this.attackRange.setBaseValue(114);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
    }
}

