/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;

public class SpiderBoomerangToolItem
extends BoomerangToolItem {
    public SpiderBoomerangToolItem() {
        super(350, ThrowWeaponsLootTable.throwWeapons, "spiderboomerang");
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(25.0f).setUpgradedValue(1.0f, 99.166695f);
        this.attackRange.setBaseValue(400);
        this.velocity.setBaseValue(125);
        this.itemAttackerProjectileCanHitWidth = 8.0f;
    }
}

