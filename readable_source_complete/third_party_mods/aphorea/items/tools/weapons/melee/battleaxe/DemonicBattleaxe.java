/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.melee.battleaxe;

import aphorea.items.tools.weapons.melee.battleaxe.AphBattleaxeToolItem;
import aphorea.utils.AphColors;
import necesse.inventory.item.Item;

public class DemonicBattleaxe
extends AphBattleaxeToolItem {
    public DemonicBattleaxe() {
        super(400, DemonicBattleaxe.getChargeLevel(2000, AphColors.demonic));
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(140.0f).setUpgradedValue(1.0f, 340.0f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(150);
    }
}

