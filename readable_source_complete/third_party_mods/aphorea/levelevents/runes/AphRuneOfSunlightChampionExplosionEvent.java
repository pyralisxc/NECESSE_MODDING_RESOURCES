/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.levelEvent.explosionEvent.ExplosionEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 */
package aphorea.levelevents.runes;

import java.awt.Color;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AphRuneOfSunlightChampionExplosionEvent
extends ExplosionEvent
implements Attacker {
    private int particleBuffer;
    protected ParticleTypeSwitcher explosionTypeSwitcher;

    public AphRuneOfSunlightChampionExplosionEvent() {
        super(0.0f, 0.0f, 0, new GameDamage(0.0f), false, 0.0f);
    }

    public AphRuneOfSunlightChampionExplosionEvent(float x, float y, int range, int toolTier, Mob owner) {
        super(x, y, range, new GameDamage(0.0f), false, (float)toolTier, owner);
        this.explosionTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
        this.targetRangeMod = 0.0f;
        this.hitsOwner = false;
    }

    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0f);
    }

    protected void playExplosionEffects() {
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0f, 3.0f, true);
    }

    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5f;
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (this.particleBuffer < 10) {
            ++this.particleBuffer;
        } else {
            this.particleBuffer = 0;
            if (range <= (float)Math.max(this.range - 125, 25)) {
                float dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
                float dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8f;
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 100).givesLight(53.0f, 1.0f).movesFriction(dx * 0.05f, dy * 0.05f, 0.8f).color((options, lifeTime1, timeAlive, lifePercent) -> {
                    float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                    options.color(new Color((int)(255.0f - 55.0f * clampedLifePercent), (int)(225.0f - 200.0f * clampedLifePercent), (int)(155.0f - 125.0f * clampedLifePercent)));
                }).heightMoves(0.0f, 10.0f).lifeTime(lifeTime * 3);
            }
        }
    }

    protected void onMobWasHit(Mob mob, float distance) {
        float mod = this.getDistanceMod(distance);
        float damagePercent = 2.0f;
        if (mob.isBoss()) {
            damagePercent /= 50.0f;
        } else if (mob.isPlayer || mob.isHuman) {
            damagePercent /= 5.0f;
        }
        GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, (float)mob.getMaxHealth() * damagePercent * mod);
        float knockback = (float)this.knockback * mod;
        mob.isServerHit(damage, (float)mob.getX() - this.x, (float)mob.getY() - this.y, knockback, (Attacker)this);
    }
}

