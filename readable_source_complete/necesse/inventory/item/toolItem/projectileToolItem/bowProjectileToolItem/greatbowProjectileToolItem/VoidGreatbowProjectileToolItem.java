/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;

public class VoidGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public VoidGreatbowProjectileToolItem() {
        super(650, GreatbowWeaponsLootTable.greatbowWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(65.0f).setUpgradedValue(1.0f, 175.00005f);
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(350);
        this.attackXOffset = 10;
        this.attackYOffset = 36;
        this.particleColor = new Color(50, 0, 102);
        this.canBeUsedForRaids = true;
    }
}

