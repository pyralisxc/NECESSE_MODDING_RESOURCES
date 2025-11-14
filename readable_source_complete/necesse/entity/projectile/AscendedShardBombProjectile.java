/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.AscendedPylonDummyMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.AscendedShardProjectile;
import necesse.entity.projectile.BombProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;

public class AscendedShardBombProjectile
extends BombProjectile {
    public AscendedShardBombProjectile() {
    }

    public AscendedShardBombProjectile(float x, float y, float targetX, float targetY, int speed, int distance, GameDamage damage, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, owner);
    }

    @Override
    public void init() {
        super.init();
        this.height = this.startHeight = (float)AscendedPylonDummyMob.CHARGE_PARTICLE_HEIGHT;
        this.isSolid = false;
        this.throwHeight = (float)this.distance * 0.3f;
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
    protected SoundSettings getMoveSound() {
        return null;
    }

    @Override
    public void spawnFuseParticle(float x, float y, float startHeight) {
    }

    @Override
    public void onFuseEnded() {
        if (!this.isClient()) {
            int projectileCount = 8;
            GameRandom random = new GameRandom((long)this.getUniqueID() * 7L);
            float angleOffset = random.getFloatBetween(0.0f, 360.0f);
            for (int i = 0; i < projectileCount; ++i) {
                float angle = 360.0f / (float)projectileCount * (float)i + angleOffset;
                AscendedShardProjectile projectile = new AscendedShardProjectile(this.getLevel(), this.x, this.y, angle, 150.0f, this.getDamage(), this.getOwner());
                projectile.resetUniqueID(random);
                this.getLevel().entityManager.projectiles.add(projectile);
            }
        }
        if (!this.isServer()) {
            SoundManager.playSound(GameResources.shatter1, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(1.3f, 1.7f)));
        }
    }

    @Override
    public ExplosionEvent getExplosionEvent(float x, float y) {
        return null;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(255, 0, 231), 30.0f, 500, this.startHeight);
    }
}

