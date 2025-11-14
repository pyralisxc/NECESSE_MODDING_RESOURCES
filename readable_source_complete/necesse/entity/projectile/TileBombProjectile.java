/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;

public class TileBombProjectile
extends BombProjectile {
    public TileBombProjectile() {
    }

    public TileBombProjectile(float x, float y, float targetX, float targetY, int speed, int distance, GameDamage damage, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, owner);
    }

    @Override
    public int getFuseTime() {
        return 3000;
    }

    @Override
    public float getParticleAngle() {
        return 290.0f;
    }

    @Override
    public float getParticleDistance() {
        return 12.0f;
    }

    @Override
    public ExplosionEvent getExplosionEvent(float x, float y) {
        float toolTier = Math.max(2.0f, this.getOwnerToolTier());
        return new BombExplosionEvent(x, y, 140, new GameDamage(200.0f, 1000.0f), false, true, toolTier, this.getOwner());
    }
}

