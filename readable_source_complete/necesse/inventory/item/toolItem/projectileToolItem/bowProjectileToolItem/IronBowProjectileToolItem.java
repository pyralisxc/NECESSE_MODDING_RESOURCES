/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class IronBowProjectileToolItem
extends BowProjectileToolItem {
    public IronBowProjectileToolItem() {
        super(300, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 134.1667f);
        this.attackRange.setBaseValue(700);
        this.velocity.setBaseValue(140);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
        this.attackSpriteStretch = 5;
        this.canBeUsedForRaids = true;
    }
}

