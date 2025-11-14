/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.stream.Stream;
import necesse.engine.network.client.ClientClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TimeTriggeredEvents;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AmethystGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.EmeraldStaffGlyphCircleParticle;
import necesse.entity.particle.EmeraldStaffGlyphParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.level.maps.LevelObjectHit;

public class EmeraldGlyphEvent
extends GroundEffectEvent {
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(50);
    protected float buffDuration = 0.2f;
    protected long startTime;
    protected boolean chargeUpCompleted;
    protected TimeTriggeredEvents events = new TimeTriggeredEvents();

    public EmeraldGlyphEvent() {
    }

    public EmeraldGlyphEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.getTime();
        if (this.level.isClient()) {
            SoundManager.playSound(SoundSettingsRegistry.crystalGlyph, this.owner.x, this.owner.y);
        }
        int chargeUpTime = 500;
        int lingerTime = 10000;
        int circleSpawnTime = 500;
        this.level.entityManager.addParticle(new EmeraldStaffGlyphCircleParticle(this.level, this.x, this.y, chargeUpTime + lingerTime), Particle.GType.CRITICAL);
        this.events.addEvent(circleSpawnTime, () -> {
            EmeraldStaffGlyphParticle glyphParticle = new EmeraldStaffGlyphParticle(this.level, this.x, this.y, chargeUpTime + lingerTime - circleSpawnTime);
            this.level.entityManager.addParticle(glyphParticle, Particle.GType.CRITICAL);
        });
        this.events.addEvent(chargeUpTime, () -> {
            this.chargeUpCompleted = true;
        });
        this.events.addEvent(chargeUpTime + lingerTime, () -> {
            this.over();
            if (this.isClient()) {
                this.triggerFinishedEffects();
            }
        });
    }

    public long getTimeSinceStart() {
        return this.getTime() - this.startTime;
    }

    @Override
    public Shape getHitBox() {
        int width = 185;
        int height = 185;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        if (this.hitCooldowns.canHit(target)) {
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (this.hitCooldowns.canHit(target)) {
            target.addBuff(new ActiveBuff(BuffRegistry.EMERALD_GLYPH, target, this.buffDuration, (Attacker)target), true);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public boolean canHit(Mob mob) {
        return this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        if (this.events.tickEvents(this.getTimeSinceStart())) {
            if (this.chargeUpCompleted) {
                int particleCount = 1;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                for (int i = 0; i < particleCount; ++i) {
                    int angle = GameRandom.globalRandom.nextInt(360);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
                    float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
                    this.getLevel().entityManager.addParticle((float)this.x + dx * 2.0f, (float)this.y + dy * 2.0f, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(10, 18).movesFriction(0.0f, -5.0f, 0.8f).color(ThemeColorRegistry.EMERALD.getRandomColor()).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
                }
            }
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        if (this.events.tickEvents(this.getTimeSinceStart())) {
            super.serverTick();
        }
    }

    private void triggerFinishedEffects() {
        int particleCount = 20;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            this.getLevel().entityManager.addParticle((float)this.x + dx * 2.0f, (float)this.y + dy * 2.0f, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(10, 18).movesFriction(0.0f, -5.0f, 0.8f).color(ThemeColorRegistry.EMERALD.getRandomColor()).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
        }
    }

    @Override
    protected Stream<Mob> streamTargets(Shape hitbox) {
        if (!this.chargeUpCompleted) {
            return Stream.empty();
        }
        return AmethystGlyphEvent.streamBuffableTargets(this.level, hitbox, this.owner);
    }

    @Override
    protected boolean canHitLocalClient(ClientClient me) {
        if (!this.chargeUpCompleted) {
            return false;
        }
        return this.owner == null || !me.playerMob.canBeTargeted(this.owner, this.owner.getPvPOwner());
    }
}

