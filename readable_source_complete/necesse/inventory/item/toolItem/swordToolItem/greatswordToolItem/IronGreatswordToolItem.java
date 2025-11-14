/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;

public class IronGreatswordToolItem
extends GreatswordToolItem {
    public IronGreatswordToolItem() {
        super(300, GreatswordWeaponsLootTable.greatswordWeapons, IronGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(50.0f).setUpgradedValue(1.0f, 186.66672f);
        this.attackRange.setBaseValue(110);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
    }
}

