/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
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

public class GhostSkullExplosionEvent
extends ExplosionEvent
implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public GhostSkullExplosionEvent() {
        this(0.0f, 0.0f, new GameDamage(100.0f), null);
    }

    public GhostSkullExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 60, damage, false, 0.0f, owner);
        this.sendCustomData = false;
        this.sendOwnerData = true;
        this.knockback = 0;
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.fadedeath3, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(1.0f).pitch(1.5f));
        this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0f, 3.0f, true);
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        if (range <= (float)Math.max(this.range, 25)) {
            GameRandom random = GameRandom.globalRandom;
            float dx = dirX * (float)random.getIntBetween(30, 40);
            float dy = dirY * (float)random.getIntBetween(30, 40) * 0.8f;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).movesFriction(dx * 0.05f, dy * 0.05f, 1.8f).color(new Color(176 - random.getIntBetween(10, 50), 234 - random.getIntBetween(10, 50), 190 - random.getIntBetween(10, 50))).heightMoves(0.0f, 10.0f).alpha(0.5f).lifeTime(lifeTime);
        }
    }
}

