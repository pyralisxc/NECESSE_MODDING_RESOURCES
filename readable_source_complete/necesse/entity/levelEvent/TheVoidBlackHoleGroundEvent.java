/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Shape;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.CirclePolygon;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.LevelObjectHit;

public class TheVoidBlackHoleGroundEvent
extends GroundEffectEvent {
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(50);
    protected int tickCounter;
    private long startTime;
    private GameDamage damage;
    private int lifetime = 10000;
    private int beforeActiveTime = 1000;
    private int range = 150;
    protected SoundPlayer windSound;

    public TheVoidBlackHoleGroundEvent() {
    }

    public TheVoidBlackHoleGroundEvent(Mob owner, int x, int y, GameDamage damage, int beforeActiveTime, int lifetime, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.beforeActiveTime = beforeActiveTime;
        this.lifetime = lifetime;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.beforeActiveTime = reader.getNextInt();
        this.lifetime = reader.getNextInt();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.beforeActiveTime);
        writer.putNextInt(this.lifetime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.getTime();
        this.tickCounter = 0;
        if (this.isClient() && this.lifetime != 0) {
            PostProcessingEffects.addShockwaveEffect(new PostProcessingEffects.AbstractShockwaveEffect(){

                @Override
                public int getDrawX(GameCamera camera) {
                    return camera.getDrawX(TheVoidBlackHoleGroundEvent.this.x);
                }

                @Override
                public int getDrawY(GameCamera camera) {
                    return camera.getDrawY(TheVoidBlackHoleGroundEvent.this.y);
                }

                @Override
                public float getCurrentDistance() {
                    return 5.0f;
                }

                @Override
                public float getSize() {
                    return TheVoidBlackHoleGroundEvent.this.getBlackHoleSize();
                }

                @Override
                public float getEasingScale() {
                    return 1.75f;
                }

                @Override
                public float getEasingPower() {
                    return 1.5f;
                }

                @Override
                public boolean shouldRemove() {
                    return TheVoidBlackHoleGroundEvent.this.isOver();
                }
            });
        }
    }

    private float getBlackHoleSize() {
        float lifetimePercentage = (float)(this.getTime() - this.startTime) / (float)this.lifetime;
        if ((double)lifetimePercentage < 0.9) {
            return (float)this.range / 2.0f * lifetimePercentage;
        }
        if (lifetimePercentage > 0.9f) {
            return (float)this.range / 2.0f * ((0.1f - (lifetimePercentage - 0.9f)) * 10.0f);
        }
        return 50.0f;
    }

    @Override
    public Shape getHitBox() {
        int range = this.range - 10;
        if (range <= 0) {
            return null;
        }
        return new CirclePolygon(this.x, this.y, range, (int)((float)range * 0.75f), 8);
    }

    @Override
    public void clientHit(Mob target) {
        if ((long)this.beforeActiveTime > this.getTime() - this.startTime || (float)(this.getTime() - this.startTime) > (float)this.lifetime * 0.9f) {
            return;
        }
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if ((long)this.beforeActiveTime > this.getTime() - this.startTime) {
            return;
        }
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(new GameDamage(1.0f), this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        if (this.windSound == null || this.windSound.isDone()) {
            this.windSound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this.x, this.y).falloffDistance(700).volume(0.0f));
            if (this.windSound != null) {
                this.windSound.fadeIn(2.0f);
                this.windSound.effect.volume(0.4f);
            }
        }
        if (this.windSound != null) {
            this.windSound.refreshLooping(1.0f);
        }
        if (this.owner instanceof TheVoidMob && ((TheVoidMob)this.owner).isInDeathAnimation()) {
            this.over();
        }
        ++this.tickCounter;
        ++this.range;
        if (this.tickCounter > 20 * (this.lifetime / 1000)) {
            this.over();
        } else {
            super.clientTick();
        }
        this.addTickParticles();
    }

    @Override
    public void serverTick() {
        if (this.owner instanceof TheVoidMob && ((TheVoidMob)this.owner).isInDeathAnimation()) {
            this.over();
        }
        ++this.tickCounter;
        ++this.range;
        if (this.tickCounter > 20 * (this.lifetime / 1000)) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    private void addTickParticles() {
        AtomicReference<Float> currentAngleOuterCircle;
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 65.0f;
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), (float)this.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
                float distY = distance * lifePercent * 0.85f;
                pos.x = (float)this.x + GameMath.sin(angle) * (distance * lifePercent);
                pos.y = (float)this.y + GameMath.cos(angle) * distY * 0.85f;
            }).color((options, lifeTime, timeAlive, lifePercent) -> {
                float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                options.color(new Color((int)(0.0f + 255.0f * clampedLifePercent), 0, (int)(0.0f + 231.0f * clampedLifePercent)));
            }).ignoreLight(true).lifeTime(1000).sizeFades(50, 24);
        }
        float lifetimePercentage = (float)(this.getTime() - this.startTime) / (float)this.lifetime;
        if ((long)this.beforeActiveTime < this.getTime() - this.startTime && !((float)(this.getTime() - this.startTime) > (float)this.lifetime * 0.9f)) {
            currentAngleOuterCircle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngleOuterCircle.get().floatValue()) * (float)this.range, (float)this.y + GameMath.cos(currentAngleOuterCircle.get().floatValue()) * (float)this.range * 0.75f, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(255, 0, 231)).ignoreLight(true).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngleOuterCircle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)this.x + GameMath.sin(angle) * (float)this.range * (1.0f - lifePercent);
                pos.y = (float)this.y + GameMath.cos(angle) * (float)this.range * 0.75f * (1.0f - lifePercent);
            }).lifeTime(1000).sizeFades(16, 24);
            for (int i = 0; i < 9; ++i) {
                this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngleOuterCircle.get().floatValue()) * (float)this.range, (float)this.y + GameMath.cos(currentAngleOuterCircle.get().floatValue()) * (float)this.range * 0.75f, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(255, 0, 231)).ignoreLight(true).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngleOuterCircle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                    pos.x = (float)this.x + GameMath.sin(angle) * (float)this.range;
                    pos.y = (float)this.y + GameMath.cos(angle) * (float)this.range * 0.75f;
                }).lifeTime(1000).sizeFades(16, 24);
            }
        }
        if (lifetimePercentage < 0.9f) {
            currentAngleOuterCircle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngleOuterCircle.get().floatValue()) * 55.0f, (float)this.y + GameMath.cos(currentAngleOuterCircle.get().floatValue()) * 55.0f, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(255, 0, 231)).ignoreLight(true).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngleOuterCircle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)this.x + GameMath.sin(angle) * ((float)this.range / 2.0f * lifetimePercentage) * 1.1f;
                pos.y = (float)this.y + GameMath.cos(angle) * ((float)this.range / 2.0f * lifetimePercentage) * 1.1f;
            }).lifeTime(1000).sizeFades(16, 24);
        }
    }
}

