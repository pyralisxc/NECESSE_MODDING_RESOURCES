/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.jumping;

import java.awt.geom.Point2D;
import java.util.function.BiConsumer;
import necesse.engine.network.packet.PacketMobJump;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.jumping.JumpingMobStats;

public interface JumpingMobInterface {
    public JumpingMobStats getJumpStats();

    default public void tickJump(float moveX, float moveY) {
        JumpingMobStats stats = this.getJumpStats();
        if (stats.mob.isServer()) {
            this.tickJump(moveX, moveY, (dx, dy) -> {
                ((JumpingMobInterface)((Object)stats.mob)).runJump(dx.floatValue(), dy.floatValue());
                Server server = stats.mob.getLevel().getServer();
                server.network.sendToClientsWithEntity(new PacketMobJump(stats.mob, dx.floatValue(), dy.floatValue()), stats.mob);
            });
        }
    }

    default public void tickJump(float moveX, float moveY, BiConsumer<Float, Float> onJump) {
        JumpingMobStats stats = this.getJumpStats();
        if ((moveX != 0.0f || moveY != 0.0f) && stats.lastJumpTime + (long)stats.lastJumpAnimationTime + (long)stats.getJumpCooldown() < stats.mob.getWorldEntity().getLocalTime()) {
            Point2D.Float normalize = GameMath.normalize(moveX, moveY);
            float jumpStrength = stats.getJumpStrength();
            onJump.accept(Float.valueOf(normalize.x * jumpStrength), Float.valueOf(normalize.y * jumpStrength));
        }
    }

    default public int getJumpAnimationFrame(int frames) {
        JumpingMobStats stats = this.getJumpStats();
        long jumpTime = Math.max(0L, stats.mob.getWorldEntity().getLocalTime() - stats.lastJumpTime);
        if (jumpTime < (long)stats.lastJumpAnimationTime) {
            return GameUtils.getAnim(jumpTime, frames, stats.lastJumpAnimationTime);
        }
        return 0;
    }

    default public void runJump(float dx, float dy) {
        JumpingMobStats stats = this.getJumpStats();
        stats.mob.dx += dx;
        stats.mob.dy += dy;
        if (!stats.mob.isAttacking) {
            stats.mob.setFacingDir(dx, dy);
        }
        stats.lastJumpTime = stats.mob.getWorldEntity().getLocalTime();
        stats.lastJumpAnimationTime = stats.getJumpAnimationTime();
        this.onJump();
    }

    default public void onJump() {
    }

    default public void setJumpAnimationTime(int milliseconds) {
        this.getJumpStats().setJumpAnimationTime(milliseconds);
    }

    default public void setJumpCooldown(int milliseconds) {
        this.getJumpStats().setJumpCooldown(milliseconds);
    }

    default public void setJumpStrength(float strength) {
        this.getJumpStats().setJumpStrength(strength);
    }
}

