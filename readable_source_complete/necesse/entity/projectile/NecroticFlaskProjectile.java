/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.entity.levelEvent.explosionEvent.splashEvent.NecroPoisonSplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.NecroticPoisonBuff;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.FlaskProjectile;
import necesse.level.maps.Level;

public class NecroticFlaskProjectile
extends FlaskProjectile {
    public NecroticFlaskProjectile() {
    }

    public NecroticFlaskProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback);
    }

    public NecroticFlaskProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public Color getParticleColor() {
        return NecroticPoisonBuff.getNecroticParticleColor();
    }

    @Override
    protected void spawnSplashEvent() {
        this.getLevel().entityManager.addLevelEvent(new NecroPoisonSplashEvent(this.x, this.y, 96, this.getDamage(), 0.0f, this.getOwner()));
    }

    @Override
    protected void spawnDeathParticles() {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.necroticflaskdebris, i, 0, 32, this.x, this.y, this.height, this.dx * 5.0f, this.dy * 5.0f), Particle.GType.IMPORTANT_COSMETIC);
        }
    }
}

