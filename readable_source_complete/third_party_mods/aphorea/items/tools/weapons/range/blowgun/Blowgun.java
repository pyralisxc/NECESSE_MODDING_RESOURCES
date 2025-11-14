/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.range.blowgun;

import aphorea.items.tools.weapons.range.blowgun.AphBlowgunToolItem;
import necesse.inventory.item.Item;

public class Blowgun
extends AphBlowgunToolItem {
    public Blowgun() {
        super(100);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(10.0f).setUpgradedValue(1.0f, 60.0f);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.velocity.setBaseValue(100);
        this.attackRange.setBaseValue(500);
        this.resilienceGain.setBaseValue(0.0f);
    }
}

