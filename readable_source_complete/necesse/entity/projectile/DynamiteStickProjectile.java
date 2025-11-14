/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import necesse.entity.levelEvent.explosionEvent.DynamiteExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;

public class DynamiteStickProjectile
extends BombProjectile {
    public DynamiteStickProjectile() {
    }

    public DynamiteStickProjectile(float x, float y, float targetX, float targetY, int speed, int distance, GameDamage damage, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, owner);
    }

    @Override
    public void init() {
        super.init();
        this.stopsRotatingOnStationary = true;
    }

    @Override
    public int getFuseTime() {
        return 4000;
    }

    @Override
    public float getParticleAngle() {
        return 220.0f;
    }

    @Override
    public float getParticleDistance() {
        return 14.0f;
    }

    @Override
    public ExplosionEvent getExplosionEvent(float x, float y) {
        float toolTier = Math.max(6.0f, this.getOwnerToolTier() + 1.0f);
        return new DynamiteExplosionEvent(x, y, 230, new GameDamage(400.0f, 1000.0f), true, false, toolTier, this.getOwner());
    }
}

