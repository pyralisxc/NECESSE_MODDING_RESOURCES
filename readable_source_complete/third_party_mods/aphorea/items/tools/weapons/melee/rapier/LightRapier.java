/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.rapier;

import aphorea.items.tools.weapons.melee.rapier.AphRapierToolItem;
import necesse.inventory.item.Item;

public class LightRapier
extends AphRapierToolItem {
    public LightRapier() {
        super(1300);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(50);
        this.knockback.setBaseValue(10);
        this.attackDamage.setBaseValue(10.0f).setUpgradedValue(1.0f, 20.0f);
        this.dashAnimTime.setBaseValue(400);
        this.attackRange.setBaseValue(85);
        this.width = 8.0f;
        this.attackXOffset = 16;
        this.attackYOffset = 14;
    }
}

