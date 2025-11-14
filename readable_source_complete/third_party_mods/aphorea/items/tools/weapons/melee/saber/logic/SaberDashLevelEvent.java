/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LineHitbox
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 */
package aphorea.items.tools.weapons.melee.saber.logic;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SaberDashLevelEvent
extends MobDashLevelEvent {
    public SaberDashLevelEvent() {
    }

    public SaberDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, seed, dirX, dirY, distance, animTime, damage);
    }

    public void init() {
        super.init();
        if (this.level != null && this.level.isClient() && this.owner != null) {
            float forceMod = Math.min((float)this.animTime / 700.0f, 1.0f);
            float forceX = this.dirX * this.distance * forceMod;
            float forceY = this.dirY * this.distance * forceMod;
            for (int i = 0; i < 30; ++i) {
                this.level.entityManager.addParticle(this.owner.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 5.0f, this.owner.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 5.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f).color(AphColors.lighter_gray).height(18.0f).lifeTime(700);
            }
            SoundManager.playSound((GameSound)GameResources.swoosh, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.owner).volume(0.2f).pitch(2.5f));
        }
        if (this.owner != null) {
            this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, this.owner, this.animTime, null), false);
            this.owner.addBuff(new ActiveBuff(AphBuffs.SABER_DASH_ACTIVE, this.owner, this.animTime, null), false);
        }
    }

    public Shape getHitBox() {
        Point2D.Float dir = this.owner.getDir() == 3 ? GameMath.getPerpendicularDir((float)(-this.dirX), (float)(-this.dirY)) : GameMath.getPerpendicularDir((float)this.dirX, (float)this.dirY);
        float width = 40.0f;
        float frontOffset = 20.0f;
        float range = 80.0f;
        float rangeOffset = -40.0f;
        return new LineHitbox(this.owner.x + dir.x * rangeOffset + this.dirX * frontOffset, this.owner.y + dir.y * rangeOffset + this.dirY * frontOffset, dir.x, dir.y, range, width);
    }
}

