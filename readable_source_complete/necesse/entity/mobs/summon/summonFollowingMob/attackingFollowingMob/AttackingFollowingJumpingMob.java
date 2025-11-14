/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import necesse.engine.network.packet.PacketMountMobJump;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;
import necesse.entity.mobs.jumping.JumpingMobStats;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;

public class AttackingFollowingJumpingMob
extends AttackingFollowingMob
implements JumpingMobInterface {
    protected JumpingMobStats jumpStats = new JumpingMobStats(this);

    public AttackingFollowingJumpingMob(int health) {
        super(health);
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        boolean inLiquid = this.inLiquid();
        if (inLiquid) {
            super.calcAcceleration(speed, friction, moveX, moveY, delta);
        } else {
            boolean clientControlled;
            Mob mounter = this.getRider();
            boolean bl = clientControlled = mounter != null && mounter.isPlayer;
            if (!clientControlled) {
                this.tickJump(moveX, moveY);
            } else if (this.isClient() && this.getLevel().getClient().getPlayer() == mounter) {
                this.tickJump(moveX, moveY, (dx, dy) -> {
                    this.runJump(dx.floatValue(), dy.floatValue());
                    this.getLevel().getClient().network.sendPacket(new PacketMountMobJump(this, dx.floatValue(), dy.floatValue()));
                });
            }
            super.calcAcceleration(speed, friction, 0.0f, 0.0f, delta);
        }
    }

    @Override
    public JumpingMobStats getJumpStats() {
        return this.jumpStats;
    }
}

