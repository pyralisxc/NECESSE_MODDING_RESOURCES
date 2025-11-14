/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;

public class HookBoomerangToolItem
extends BoomerangToolItem {
    public HookBoomerangToolItem() {
        super(300, null, "hookboomerang");
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(35.0f).setUpgradedValue(1.0f, 93.33336f);
        this.attackRange.setBaseValue(400);
        this.velocity.setBaseValue(150);
        this.itemAttackerProjectileCanHitWidth = 8.0f;
    }
}

