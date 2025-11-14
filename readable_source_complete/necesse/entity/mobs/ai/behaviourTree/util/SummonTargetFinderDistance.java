/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;

public class SummonTargetFinderDistance<T extends Mob>
extends TargetFinderDistance<T> {
    public SummonTargetFinderDistance(int searchDistance) {
        super(searchDistance);
    }

    @Override
    public float getSearchDistanceMod(T mob, Mob target) {
        float ownerMod = ((Mob)mob).buffManager.getModifier(BuffModifiers.CHASER_RANGE).floatValue();
        float targetMod = target == null ? 1.0f : target.buffManager.getModifier(BuffModifiers.TARGET_RANGE).floatValue();
        Mob following = ((Mob)mob).getFollowingMob();
        float followingMod = following == null ? 1.0f : following.buffManager.getModifier(BuffModifiers.SUMMONS_TARGET_RANGE).floatValue();
        return targetMod * followingMod * ownerMod;
    }
}

