/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.mobs.jumping.JumpingMobInterface;
import necesse.entity.mobs.jumping.JumpingMobStats;

public class CritterJumpingMob
extends CritterMob
implements JumpingMobInterface {
    protected JumpingMobStats jumpStats = new JumpingMobStats(this);

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        boolean inLiquid = this.inLiquid();
        if (inLiquid) {
            super.calcAcceleration(speed, friction, moveX, moveY, delta);
        } else {
            this.tickJump(moveX, moveY);
            super.calcAcceleration(speed, friction, 0.0f, 0.0f, delta);
        }
    }

    @Override
    public JumpingMobStats getJumpStats() {
        return this.jumpStats;
    }
}

