/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.jumping;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;

public class JumpingMobStats {
    public final Mob mob;
    private int jumpAnimationTime = 500;
    private int jumpCooldown = 250;
    private float jumpStrength = 60.0f;
    public boolean jumpAnimationUseSpeedMod = true;
    public boolean jumpCooldownUseSpeedMod = true;
    public boolean jumpStrengthUseSpeedMod = true;
    long lastJumpTime;
    int lastJumpAnimationTime;

    public JumpingMobStats(Mob mob) {
        if (!(mob instanceof JumpingMobInterface)) {
            throw new IllegalArgumentException("Mob must implement JumpingMobInterface");
        }
        this.mob = mob;
    }

    public void setJumpAnimationTime(int milliseconds) {
        this.jumpAnimationTime = milliseconds;
    }

    public void setJumpCooldown(int milliseconds) {
        this.jumpCooldown = milliseconds;
    }

    public void setJumpStrength(float strength) {
        this.jumpStrength = strength;
    }

    private float getTimeModifier() {
        return 1.0f / this.mob.getSpeedModifier();
    }

    public int getJumpAnimationTime() {
        if (this.jumpAnimationUseSpeedMod) {
            return (int)((float)this.jumpAnimationTime * this.getTimeModifier());
        }
        return this.jumpAnimationTime;
    }

    public int getJumpCooldown() {
        if (this.jumpCooldownUseSpeedMod) {
            return (int)((float)this.jumpCooldown * this.getTimeModifier());
        }
        return this.jumpCooldown;
    }

    public float getJumpStrength() {
        if (this.jumpStrengthUseSpeedMod) {
            return (int)(this.jumpStrength * this.mob.getSpeedModifier());
        }
        return this.jumpStrength;
    }
}

