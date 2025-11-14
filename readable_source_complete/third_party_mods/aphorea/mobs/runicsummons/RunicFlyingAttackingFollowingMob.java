/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob
 */
package aphorea.mobs.runicsummons;

import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;

public class RunicFlyingAttackingFollowingMob
extends FlyingAttackingFollowingMob {
    public float effectNumber = 1.0f;

    public RunicFlyingAttackingFollowingMob(int health) {
        super(health);
    }

    public void updateEffectNumber(float effectNumber) {
        this.effectNumber = effectNumber;
    }
}

