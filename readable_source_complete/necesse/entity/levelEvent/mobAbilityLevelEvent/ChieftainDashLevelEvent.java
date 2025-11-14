/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ChieftainMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class ChieftainDashLevelEvent
extends MobDashLevelEvent {
    public ChieftainDashLevelEvent() {
    }

    public ChieftainDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, seed, dirX, dirY, distance, animTime, damage);
    }

    @Override
    public void init() {
        super.init();
        if (this.level != null && this.owner != null) {
            if (this.level.isClient()) {
                float forceMod = Math.min((float)this.animTime / 2000.0f, 1.0f);
                float forceX = this.dirX * this.distance * forceMod;
                float forceY = this.dirY * this.distance * forceMod;
                for (int i = 0; i < 30; ++i) {
                    float singleForceMod = GameRandom.globalRandom.getFloatBetween(0.5f, 1.5f);
                    this.level.entityManager.addParticle(this.owner.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 5.0f, this.owner.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 5.0f, Particle.GType.IMPORTANT_COSMETIC).movesFriction(forceX * singleForceMod * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f, forceY * singleForceMod * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f, 0.8f).smokeColor(20.0f).height(18.0f).lifeTime(2000);
                }
                SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect(this.owner).volume(0.5f).pitch(1.5f));
                SoundManager.playSound(GameResources.warcryshort, (SoundEffect)SoundEffect.effect(this.owner).volume(3.0f).falloffDistance(2000));
            }
            if (this.owner instanceof ChieftainMob) {
                ((ChieftainMob)this.owner).showChargeAttack((int)(this.owner.x + this.dirX * 100.0f), (int)(this.owner.y + this.dirY * 100.0f), this.animTime);
            }
        }
    }

    @Override
    protected double getMoveCurve(double x) {
        return Math.cos(x * Math.PI + Math.PI) * 0.5 + 0.5;
    }

    @Override
    public Shape getHitBox() {
        return new Rectangle(this.owner.getX() - 25, this.owner.getY() - 25, 50, 50);
    }
}

