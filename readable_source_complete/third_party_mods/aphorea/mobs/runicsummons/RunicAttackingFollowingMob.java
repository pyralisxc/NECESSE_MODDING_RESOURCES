/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 */
package aphorea.mobs.runicsummons;

import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;

public class RunicAttackingFollowingMob
extends AttackingFollowingMob {
    public float effectNumber = 1.0f;

    public RunicAttackingFollowingMob(int health) {
        super(health);
    }

    public void updateEffectNumber(float effectNumber) {
        this.effectNumber = effectNumber;
    }
}

