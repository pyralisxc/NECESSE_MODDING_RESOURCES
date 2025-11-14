/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMob
 *  necesse.gfx.gameTexture.GameTexture
 */
package aphorea.mobs.summon;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMob;
import necesse.gfx.gameTexture.GameTexture;

public class UndeadSkeleton
extends BabySkeletonMob {
    public int count;
    public static GameTexture texture;

    public void init() {
        super.init();
        this.count = 0;
    }

    public void serverTick() {
        super.serverTick();
        ++this.count;
        if (this.count >= 200) {
            if (this.isFollowing()) {
                ((ItemAttackerMob)this.getFollowingMob()).serverFollowersManager.removeFollower((Mob)this, false, false);
            }
            this.remove();
        }
    }
}

