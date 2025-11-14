/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.rapier;

import aphorea.items.tools.weapons.melee.rapier.AphRapierToolItem;
import necesse.inventory.item.Item;

public class FossilRapier
extends AphRapierToolItem {
    public FossilRapier() {
        super(500);
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(6.0f).setUpgradedValue(1.0f, 20.0f);
        this.attackRange.setBaseValue(70);
        this.width = 10.0f;
        this.attackXOffset = 12;
        this.attackYOffset = 2;
    }
}

