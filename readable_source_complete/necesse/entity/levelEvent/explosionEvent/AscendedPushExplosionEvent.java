/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AscendedPushExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public AscendedPushExplosionEvent() {
        this(0.0f, 0.0f, 150, 5000, null);
    }

    public AscendedPushExplosionEvent(float x, float y, int range, int knockback, Mob owner) {
        super(x, y, range, new GameDamage(1.0f), false, 0.0f, owner);
        this.knockback = knockback;
    }

    @Override
    protected GameDamage getTotalMobDamage(float mod) {
        return super.getTotalMobDamage(mod).modDamage(0.0f);
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(4000).volume(1.5f).pitch(1.5f));
        SoundManager.playSound(GameResources.magicroar, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(4000).volume(1.5f).pitch(2.5f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (range <= (float)Math.max(this.range - 30, 30)) {
            float dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            float dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).sizeFades(24, 48).movesFriction(dx * 0.05f, dy * 0.05f, 0.2f).heightMoves(0.0f, 10.0f).lifeTime(lifeTime / 2);
        }
    }
}

