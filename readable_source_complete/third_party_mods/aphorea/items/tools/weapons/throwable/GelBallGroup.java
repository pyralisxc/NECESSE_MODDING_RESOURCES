/*
 * Decompiled with CFR 0.152.
 */
package aphorea.items.tools.weapons.throwable;

import aphorea.items.tools.weapons.throwable.GelBall;

public class GelBallGroup
extends GelBall {
    public GelBallGroup() {
        this.enchantCost.setBaseValue(200).setUpgradedValue(1.0f, 2000);
        this.attackDamage.setBaseValue(15.0f).setUpgradedValue(1.0f, 80.0f);
        this.infinity = true;
        this.stackSize = 1;
        this.dropsAsMatDeathPenalty = false;
        this.attackDamage.setBaseValue(8.0f).setUpgradedValue(1.0f, 50.0f);
    }
}

