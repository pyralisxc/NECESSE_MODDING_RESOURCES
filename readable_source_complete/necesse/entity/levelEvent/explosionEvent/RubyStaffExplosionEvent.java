/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;

public class RubyStaffExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected AtomicInteger givesLifeEssence;
    protected Mob ignoreLifeEssenceOnTarget;

    public RubyStaffExplosionEvent() {
        this(0.0f, 0.0f, 40, new GameDamage(80.0f), true, 0.0f, null, new AtomicInteger(), null);
    }

    public RubyStaffExplosionEvent(float x, float y, int range, GameDamage damage, boolean destructive, float toolTier, Mob owner, AtomicInteger givesLifeEssence, Mob ignoreLifeEssenceOnTarget) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.targetRangeMod = 0.0f;
        this.knockback = 100;
        this.givesLifeEssence = givesLifeEssence;
        this.ignoreLifeEssenceOnTarget = ignoreLifeEssenceOnTarget;
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        float dy;
        float dx;
        if (range <= (float)Math.max(this.range - 50, 25) && GameRandom.globalRandom.getChance(0.8f)) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(20, 70);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(10, 60) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(20, 40).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).color(ThemeColorRegistry.RUBY.getRandomColor().darker().darker()).heightMoves(0.0f, 70.0f).lifeTime(lifeTime * 4);
        }
        if (range <= (float)Math.max(this.range - 50, 25) && GameRandom.globalRandom.getChance(0.8f)) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 22)).sizeFades(11, 22).movesFriction(dx * 0.05f, dy * 0.05f, 2.0f).color(ThemeColorRegistry.RUBY.getRandomColor()).heightMoves(0.0f, 10.0f).lifeTime(lifeTime * 3);
        }
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && target != this.ownerMob;
    }

    @Override
    protected void onMobWasHit(Mob mob, float distance) {
        super.onMobWasHit(mob, distance);
        if (this.ownerMob != null && this.givesLifeEssence.get() > 0) {
            this.ownerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.LIFE_ESSENCE, this.ownerMob, 60.0f, null), true);
            this.givesLifeEssence.decrementAndGet();
        }
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionLight, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.65f).pitch(1.5f));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 2.0f, 2.0f, true);
    }
}

