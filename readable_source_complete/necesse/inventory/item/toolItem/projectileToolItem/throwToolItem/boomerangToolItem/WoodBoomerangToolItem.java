/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;

public class WoodBoomerangToolItem
extends BoomerangToolItem {
    public WoodBoomerangToolItem() {
        super(100, ThrowWeaponsLootTable.throwWeapons, "woodboomerang");
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(15.0f).setUpgradedValue(1.0f, 105.00003f);
        this.attackRange.setBaseValue(200);
        this.velocity.setBaseValue(100);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
    }
}

