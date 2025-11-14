/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.greatsword;

import aphorea.items.tools.weapons.melee.greatsword.AphGreatswordSecondarySpinToolItem;
import java.awt.Color;
import necesse.inventory.item.Item;

public class BabylonGreatsword
extends AphGreatswordSecondarySpinToolItem {
    public BabylonGreatsword() {
        super(1550, 300, BabylonGreatsword.getThreeChargeLevels((int)500, (int)600, (int)700), new Color(200, 100, 100));
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(120.0f).setUpgradedValue(1.0f, 150.0f);
        this.attackRange.setBaseValue(110);
        this.knockback.setBaseValue(50);
        this.width = 24.0f;
        this.attackXOffset = 18;
        this.attackYOffset = 20;
    }
}

