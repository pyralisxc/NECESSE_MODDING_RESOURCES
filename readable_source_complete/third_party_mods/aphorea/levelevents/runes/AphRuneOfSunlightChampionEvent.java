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
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 */
package aphorea.levelevents.runes;

import aphorea.levelevents.runes.AphRuneOfSunlightChampionExplosionEvent;
import aphorea.utils.AphColors;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AphRuneOfSunlightChampionEvent
extends MobAbilityLevelEvent
implements Attacker {
    private int lifeTime = 0;
    private int range;

    public AphRuneOfSunlightChampionEvent() {
    }

    public AphRuneOfSunlightChampionEvent(int range, Mob owner) {
        super(owner, new GameRandom());
        this.range = range;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.range);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.range = reader.getNextInt();
    }

    public void init() {
        super.init();
        if (this.isClient()) {
            this.getClient().startCameraShake(null, 2500, 60, 3.0f, 3.0f, false);
        }
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 3000) {
            SoundManager.playSound((GameSound)GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect((float)this.owner.x, (float)this.owner.y).volume(2.5f).pitch(1.5f));
            this.owner.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfSunlightChampionExplosionEvent(this.owner.x, this.owner.y, this.range, 0, this.owner));
            this.over();
        } else if (this.lifeTime <= 2000) {
            GameRandom random = GameRandom.globalRandom;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            float distance = 5.0f + 70.0f * (1.0f - (float)this.lifeTime / 2000.0f);
            for (int i = 0; i < 4; ++i) {
                this.owner.getLevel().entityManager.addParticle(this.owner.x + GameMath.sin((float)currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), this.owner.y + GameMath.cos((float)currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 30.0f / 250.0f), Float::sum).floatValue();
                    float distY = (distance - 20.0f) * 0.85f;
                    pos.x = this.owner.x + GameMath.sin((float)angle) * (distance - distance / 2.0f * lifePercent);
                    pos.y = this.owner.y + GameMath.cos((float)angle) * distY - 20.0f * lifePercent;
                }).color((options, lifeTime, timeAlive, lifePercent) -> {
                    options.color(AphColors.fire);
                    if (lifePercent > 0.5f) {
                        options.alpha(2.0f * (1.0f - lifePercent));
                    }
                }).size((options, lifeTime, timeAlive, lifePercent) -> options.size(22, 22)).lifeTime(1000);
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 3000) {
            this.owner.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfSunlightChampionExplosionEvent(this.owner.x, this.owner.y, this.range, 0, this.owner));
            this.over();
        }
    }
}

