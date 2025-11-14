/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphAbysmalRuneEvent
extends HitboxEffectEvent
implements Attacker {
    private int lifeTime = 0;
    public int targetX;
    public int targetY;

    public AphAbysmalRuneEvent() {
    }

    public AphAbysmalRuneEvent(Mob owner, int targetX, int targetY) {
        super(owner, new GameRandom());
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        if (this.owner != null) {
            SoundManager.playSound((GameSound)GameResources.magicbolt1, (SoundEffect)SoundEffect.effect((float)this.targetX, (float)this.targetY).volume(1.0f).pitch(0.8f));
        }
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        } else {
            float maxDist = 128.0f;
            int lifeTime = 1000;
            int minHeight = 0;
            int maxHeight = 30;
            int particles = 10;
            for (int i = 0; i < particles; ++i) {
                float height = (float)minHeight + (float)(maxHeight - minHeight) * (float)i / (float)particles;
                AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                float outDistance = GameRandom.globalRandom.getFloatBetween(60.0f, maxDist + 32.0f);
                boolean counterclockwise = GameRandom.globalRandom.nextBoolean();
                this.owner.getLevel().entityManager.addParticle((float)this.targetX + GameRandom.globalRandom.getFloatBetween(0.0f, GameMath.sin((float)currentAngle.get().floatValue()) * maxDist), (float)this.targetY + GameRandom.globalRandom.getFloatBetween(0.0f, GameMath.cos((float)currentAngle.get().floatValue()) * maxDist * 0.75f), Particle.GType.CRITICAL).color((Color)GameRandom.globalRandom.getOneOf((Object[])AphColors.paletteBlackHole)).height(height).moves((pos, delta, cLifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                    if (counterclockwise) {
                        angle = -angle;
                    }
                    float linearDown = GameMath.lerpExp((float)lifePercent, (float)0.525f, (float)0.0f, (float)1.0f);
                    pos.x = (float)this.targetX + outDistance * GameMath.sin((float)angle) * (1.0f - linearDown);
                    pos.y = (float)this.targetY + outDistance * GameMath.cos((float)angle) * (1.0f - linearDown) * 0.75f;
                }).lifeTime(lifeTime).sizeFades(14, 18);
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
        int size = 150;
        return new Rectangle(this.targetX - size / 2, this.targetY - size / 2, size, size);
    }

    public void clientHit(Mob target) {
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        float modifier = target.getKnockbackModifier();
        if (modifier != 0.0f) {
            float knockback = 5.0f / modifier;
            target.isServerHit(new GameDamage(0.0f), (float)this.targetX - target.x, (float)this.targetY - target.y, knockback, (Attacker)this.owner);
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }
}

