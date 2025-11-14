/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;

public class GoldGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public GoldGreatbowProjectileToolItem() {
        super(350, GreatbowWeaponsLootTable.greatbowWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(52.0f).setUpgradedValue(1.0f, 180.83339f);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = new Color(228, 176, 77);
        this.canBeUsedForRaids = true;
    }
}

