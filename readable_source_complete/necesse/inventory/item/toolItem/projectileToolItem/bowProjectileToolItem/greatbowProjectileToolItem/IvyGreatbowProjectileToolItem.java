/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;

public class IvyGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public IvyGreatbowProjectileToolItem() {
        super(850, GreatbowWeaponsLootTable.greatbowWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(85.0f).setUpgradedValue(1.0f, 172.66672f);
        this.attackRange.setBaseValue(1100);
        this.velocity.setBaseValue(375);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = new Color(91, 130, 36);
        this.canBeUsedForRaids = true;
    }
}

