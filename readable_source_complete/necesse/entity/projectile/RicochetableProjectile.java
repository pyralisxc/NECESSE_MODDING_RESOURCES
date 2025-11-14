/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;

public interface RicochetableProjectile {
    default public Projectile getRicochetProjectile(float startX, float startY, float targetX, float targetY, Mob potentialTarget) {
        Projectile projectile = (Projectile)((Object)this);
        if (this.shouldRicochetPredictTarget() && potentialTarget != null) {
            projectile.setTargetPrediction(potentialTarget, -20.0f);
            projectile.moveDist(20.0);
        }
        int piercingLeft = projectile.piercing - projectile.amountHit();
        if (!this.shouldRicochetIfPiercing() && piercingLeft > 1) {
            return null;
        }
        return ProjectileRegistry.getProjectile(projectile.getID(), potentialTarget.getLevel(), startX, startY, targetX, targetY, projectile.speed, projectile.distance, projectile.getDamage(), projectile.knockback, projectile.getOwner());
    }

    default public boolean shouldRicochetIfPiercing() {
        return false;
    }

    default public boolean shouldRicochetPredictTarget() {
        return true;
    }
}

