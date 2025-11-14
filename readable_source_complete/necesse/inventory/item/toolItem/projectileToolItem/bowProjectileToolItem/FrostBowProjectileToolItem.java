/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;

public class FrostBowProjectileToolItem
extends BowProjectileToolItem {
    public FrostBowProjectileToolItem() {
        super(500, BowWeaponsLootTable.bowWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(550);
        this.attackDamage.setBaseValue(22.0f).setUpgradedValue(1.0f, 128.33337f);
        this.attackRange.setBaseValue(700);
        this.velocity.setBaseValue(160);
        this.attackXOffset = 8;
        this.attackYOffset = 22;
        this.canBeUsedForRaids = true;
    }
}

