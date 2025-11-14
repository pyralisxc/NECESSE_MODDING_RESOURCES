/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class WoodBowProjectileToolItem
extends BowProjectileToolItem {
    public WoodBowProjectileToolItem() {
        super(100, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(12.0f).setUpgradedValue(1.0f, 151.6667f);
        this.attackRange.setBaseValue(600);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
        this.attackSpriteStretch = 4;
        this.canBeUsedForRaids = true;
    }
}

