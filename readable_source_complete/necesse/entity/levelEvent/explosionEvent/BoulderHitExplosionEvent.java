/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.SwampGuardianHead;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BoulderHitExplosionEvent
extends ExplosionEvent
implements Attacker {
    public BoulderHitExplosionEvent() {
        this(0.0f, 0.0f, null);
    }

    public BoulderHitExplosionEvent(float x, float y, Mob owner) {
        super(x, y, SwampGuardianHead.boulderExplosionRange, SwampGuardianHead.boulderExplosionDamage, false, 0.0f, owner);
        this.sendCustomData = false;
        this.sendOwnerData = true;
        this.hitsOwner = false;
        this.knockback = 50;
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(1.0f).volume(0.1f));
        SoundManager.playSound(GameResources.punch, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(1.0f).volume(0.4f));
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        this.level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX, dirY).color(new Color(50, 50, 50)).lifeTime(lifeTime);
    }
}

