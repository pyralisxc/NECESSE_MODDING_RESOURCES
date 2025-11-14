/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.AttackAnimMob;
import necesse.level.maps.CollisionFilter;

public class FlyingTargetMob
extends AttackAnimMob {
    public FlyingTargetMob(int health) {
        super(health);
    }

    @Override
    public int getFlyingHeight() {
        return 50;
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }
}

