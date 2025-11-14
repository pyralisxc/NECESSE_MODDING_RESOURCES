/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class DemonicBowProjectileToolItem
extends BowProjectileToolItem {
    public DemonicBowProjectileToolItem() {
        super(400, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 122.50003f);
        this.attackRange.setBaseValue(750);
        this.velocity.setBaseValue(180);
        this.attackXOffset = 8;
        this.attackYOffset = 26;
        this.canBeUsedForRaids = true;
    }
}

