/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import necesse.entity.projectile.Projectile;

public abstract class SpinningProjectile
extends Projectile {
    protected int soundTimer;

    @Override
    public float getAngle() {
        return this.getSpinningSpeed() * (float)(this.getWorldEntity().getTime() - this.spawnTime);
    }

    protected float getSpinningSpeed() {
        return 1.0f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        --this.soundTimer;
        if (this.soundTimer <= 0) {
            this.soundTimer = 5;
            this.playMoveSound();
        }
    }
}

