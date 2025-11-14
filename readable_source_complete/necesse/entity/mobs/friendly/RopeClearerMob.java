/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import necesse.entity.mobs.friendly.FriendlyRopableMob;

public interface RopeClearerMob {
    public boolean shouldClearAfterTime(long var1);

    default public void clearRopeFromMob(FriendlyRopableMob mob) {
        mob.removeRope(false);
        if (mob.buyPrice != null) {
            mob.shouldEscape = true;
            mob.canDespawn = true;
        }
    }
}

