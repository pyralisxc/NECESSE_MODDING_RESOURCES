/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class CopperBowProjectileToolItem
extends BowProjectileToolItem {
    public CopperBowProjectileToolItem() {
        super(200, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(17.0f).setUpgradedValue(1.0f, 145.83337f);
        this.attackRange.setBaseValue(650);
        this.velocity.setBaseValue(120);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
        this.canBeUsedForRaids = true;
    }
}

