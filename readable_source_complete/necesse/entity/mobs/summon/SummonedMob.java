/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.level.maps.CollisionFilter;

public class SummonedMob
extends AttackAnimMob {
    public SummonedMob(int health) {
        super(health);
        this.isHostile = false;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.SUMMONED_MOB_OPTIONS;
        }
        return null;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        if (!this.isMounted()) {
            return super.getLevelCollisionFilter().addFilter(tp -> !tp.object().object.isDoor).summonedMobCollision();
        }
        return super.getLevelCollisionFilter();
    }

    protected GameMessage getSummonLocalization() {
        return super.getLocalization();
    }

    @Override
    public GameMessage getLocalization() {
        if (this.getLevel() == null) {
            return super.getLocalization();
        }
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null) {
            return new LocalMessage("mob", "spawnedname", "player", followingMob.getDisplayName(), "mob", this.getSummonLocalization());
        }
        return super.getLocalization();
    }
}

