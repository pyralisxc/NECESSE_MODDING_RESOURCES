/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.trails.LightningTrail
 *  necesse.entity.trails.Trail
 *  necesse.entity.trails.TrailVector
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import aphorea.particles.RuneOfThunderParticle;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.trails.LightningTrail;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphRuneOfThunderEvent
extends HitboxEffectEvent
implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet();
    public int targetX;
    public int targetY;
    public float effectNumber;
    private boolean showedLightning = false;

    public AphRuneOfThunderEvent() {
    }

    public AphRuneOfThunderEvent(Mob owner, int targetX, int targetY, float effectNumber) {
        super(owner, new GameRandom());
        this.targetX = targetX;
        this.targetY = targetY;
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.effectNumber = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet();
        if (this.isClient()) {
            this.level.entityManager.addParticle((Particle)new RuneOfThunderParticle(this.level, this.targetX, this.targetY, this.getWorldEntity().getTime()), Particle.GType.CRITICAL);
        }
        this.showedLightning = false;
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2100) {
            this.over();
        } else if (this.lifeTime >= 2000 && !this.showedLightning) {
            this.showedLightning = true;
            SoundManager.playSound((GameSound)GameResources.electricExplosion, (SoundEffect)SoundEffect.effect((float)this.targetX, (float)this.targetY).volume(1.2f).pitch(0.8f));
            float initialMoveX = GameRandom.globalRandom.getIntBetween(-20, 20);
            float initialMoveY = GameRandom.globalRandom.getIntBetween(-20, 20);
            for (int i = 0; i < 6; ++i) {
                int j;
                float finalMoveY;
                float finalMoveX;
                if (i == 0) {
                    finalMoveX = 0.0f;
                    finalMoveY = 0.0f;
                } else {
                    finalMoveX = GameRandom.globalRandom.getIntBetween(50, 80) * (GameRandom.globalRandom.getChance(0.5f) ? -1 : 1);
                    finalMoveY = GameRandom.globalRandom.getIntBetween(50, 80) * (GameRandom.globalRandom.getChance(0.5f) ? -1 : 1);
                }
                float prevX = this.targetX;
                float prevY = this.targetY;
                LightningTrail trail = new LightningTrail(new TrailVector(prevX, prevY, 0.0f, 0.0f, i == 0 ? 20.0f : GameRandom.globalRandom.getFloatBetween(10.0f, 15.0f), 0.0f), this.level, this.level.isCave ? AphColors.dark_magic : AphColors.lighting);
                this.level.entityManager.addTrail((Trail)trail);
                int n = j = i == 0 ? 1 : i + 2;
                while (j < 10) {
                    float progression = (float)j / 10.0f;
                    float height = 500.0f * progression;
                    float newX = (float)(this.targetX + GameRandom.globalRandom.getIntBetween(-5, 5)) + finalMoveY * (1.0f - progression) + initialMoveX * progression;
                    float newY = (float)(this.targetY + GameRandom.globalRandom.getIntBetween(-5, 5)) + finalMoveX * (1.0f - progression) + initialMoveY * progression;
                    trail.addNewPoint(new TrailVector(newX, newY, newX - prevX, newY - prevY, trail.thickness, height));
                    prevX = newX;
                    prevY = newY;
                    ++j;
                }
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        }
    }

    public Shape getHitBox() {
        if (this.lifeTime >= 2000) {
            int size = 100;
            return new Rectangle(this.targetX - size / 2, this.targetY - size / 2, size, size);
        }
        return new Rectangle();
    }

    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                float knockback = 10.0f / modifier;
                float damagePercent = this.effectNumber;
                if (target.isBoss()) {
                    damagePercent /= 50.0f;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5.0f;
                }
                target.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() * damagePercent), target.x - this.owner.x, target.y - this.owner.y, knockback, (Attacker)this.owner);
                target.addBuff(new ActiveBuff(AphBuffs.STUN, target, 2000, (Attacker)this), true);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }
}

