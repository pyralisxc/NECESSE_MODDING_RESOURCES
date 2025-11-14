/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class SunlightOrbEvent
extends GroundEffectEvent {
    private long startDelay = 1000L;
    private MobHitCooldowns hitCooldowns;
    private GameDamage damage = new GameDamage(90.0f);
    private float resilienceGain;
    private int tickCounter;
    private long startTime;
    private boolean isActive;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public SunlightOrbEvent() {
    }

    public SunlightOrbEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage, long startDelay) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.startDelay = startDelay;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.startDelay);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startDelay = reader.getNextLong();
    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
        this.startTime = this.level.getWorldEntity().getTime();
        this.isActive = false;
        for (int i = 0; i < 20; ++i) {
            int currentAngle = (int)(18.0f * (float)i);
            float distance = 100.0f;
            if (!this.isClient()) continue;
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngle) * distance, (float)this.y + GameMath.cos(currentAngle) * distance, Particle.GType.CRITICAL).color(new Color(249, 155, 78)).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).height(0.0f).movesFriction(-GameMath.sin(currentAngle) * 100.0f, -GameMath.cos(currentAngle) * 100.0f, 0.8f).lifeTime((int)this.startDelay + 250).sizeFades(50, 50);
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 180;
        int height = 136;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
            if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
                this.owner.addResilience(this.resilienceGain);
                this.resilienceGain = 0.0f;
            }
        }
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 200) {
            this.over();
        } else {
            super.clientTick();
        }
        if (this.level.getWorldEntity().getTime() - this.startTime > this.startDelay) {
            int i;
            if (!this.isActive) {
                SoundManager.playSound(GameResources.firespell1, SoundEffect.globalEffect().volume(0.5f).pitch(1.0f));
                this.isActive = true;
            }
            for (i = 0; i < 4; ++i) {
                this.getLevel().entityManager.addTopParticle(this.x, this.y, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().movesFrictionAngle(this.tickCounter * 10 + i * 90, 85.0f, 0.8f).color((options, lifeTime1, timeAlive, lifePercent) -> {
                    float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                    options.color(new Color((int)(255.0f - 55.0f * clampedLifePercent), (int)(225.0f - 200.0f * clampedLifePercent), (int)(155.0f - 125.0f * clampedLifePercent)));
                }).givesLight(50.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(1000);
            }
            for (i = 0; i < 4; ++i) {
                this.getLevel().entityManager.addTopParticle(this.x, this.y, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(20, 30).rotates().movesFrictionAngle(this.tickCounter * 15 - i * 90, 35.0f, 0.8f).color(new Color(255, 233, 73)).givesLight(50.0f, 1.0f).fadesAlphaTime(100, 50).lifeTime(500);
            }
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 200) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        if (!this.canDamageAnythingYet()) {
            return false;
        }
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public boolean canHit(LevelObjectHit hit) {
        if (!this.canDamageAnythingYet()) {
            return false;
        }
        return super.canHit(hit);
    }

    public boolean canDamageAnythingYet() {
        return this.level.getWorldEntity().getTime() >= this.startTime + this.startDelay + 100L;
    }

    @Override
    public void over() {
        super.over();
    }
}

