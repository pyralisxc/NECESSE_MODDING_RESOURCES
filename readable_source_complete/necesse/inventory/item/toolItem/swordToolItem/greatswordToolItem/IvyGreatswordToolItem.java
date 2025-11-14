/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;

public class IvyGreatswordToolItem
extends GreatswordToolItem {
    public IvyGreatswordToolItem() {
        super(850, GreatswordWeaponsLootTable.greatswordWeapons, IvyGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(100.0f).setUpgradedValue(1.0f, 179.66672f);
        this.attackRange.setBaseValue(126);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
    }
}

