/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import necesse.inventory.item.Item;

public class Broom
extends AphSwordToolItem {
    public Broom() {
        super(200);
        this.rarity = Item.Rarity.NORMAL;
        this.attackDamage.setBaseValue(16.0f).setUpgradedValue(1.0f, 80.0f);
        this.attackRange.setBaseValue(120);
        this.attackAnimTime.setBaseValue(400);
        this.knockback.setBaseValue(200);
    }
}

