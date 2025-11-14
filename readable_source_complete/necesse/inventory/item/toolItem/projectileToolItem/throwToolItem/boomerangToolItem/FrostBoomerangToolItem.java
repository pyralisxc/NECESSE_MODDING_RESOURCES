/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;

public class FrostBoomerangToolItem
extends BoomerangToolItem {
    public FrostBoomerangToolItem() {
        super(500, ThrowWeaponsLootTable.throwWeapons, "frostboomerang");
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(24.0f).setUpgradedValue(1.0f, 93.33336f);
        this.attackRange.setBaseValue(400);
        this.velocity.setBaseValue(140);
        this.itemAttackerProjectileCanHitWidth = 8.0f;
    }
}

