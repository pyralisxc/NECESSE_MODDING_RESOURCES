/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.stream.Stream;
import necesse.engine.network.client.ClientClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TimeTriggeredEvents;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AmethystGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.TopazStaffGlyphCircleParticle;
import necesse.entity.particle.TopazStaffGlyphParticle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.LevelObjectHit;

public class TopazGlyphEvent
extends GroundEffectEvent {
    public FloatUpgradeValue maxHealthPercentHeal = new FloatUpgradeValue(0.0f, 0.0f).setBaseValue(0.1f).setUpgradedValue(1.0f, 0.15f);
    public FloatUpgradeValue maxHealthRegen = new FloatUpgradeValue(0.0f, 0.0f).setBaseValue(0.05f).setUpgradedValue(1.0f, 0.1f);
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(1000);
    protected float buffDuration = 10.0f;
    protected int upgradeLevel;
    protected long startTime;
    protected boolean chargeUpCompleted;
    protected TimeTriggeredEvents events = new TimeTriggeredEvents();

    public TopazGlyphEvent() {
    }

    public TopazGlyphEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, int upgradeLevel) {
        super(owner, x, y, uniqueIDRandom);
        this.upgradeLevel = upgradeLevel;
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.getTime();
        int chargeUpTime = 400;
        int lingerTime = 250;
        int circleSpawnTime = 50;
        this.level.entityManager.addParticle(new TopazStaffGlyphCircleParticle(this.level, this.x, this.y, chargeUpTime + lingerTime), Particle.GType.CRITICAL);
        this.events.addEvent(circleSpawnTime, () -> {
            TopazStaffGlyphParticle glyphParticle = new TopazStaffGlyphParticle(this.level, this.x, this.y, chargeUpTime + lingerTime - circleSpawnTime);
            this.level.entityManager.addParticle(glyphParticle, Particle.GType.CRITICAL);
        });
        this.events.addEvent(chargeUpTime, () -> {
            this.chargeUpCompleted = true;
            if (this.isClient()) {
                this.triggerFinishedEffects();
            }
        });
        this.events.addEvent(chargeUpTime + lingerTime, this::over);
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
            ActiveBuff topazBuff = new ActiveBuff(BuffRegistry.TOPAZ_GLYPH, target, this.buffDuration, (Attacker)target);
            target.buffManager.addBuff(topazBuff, true, false, true);
            target.addResilience(10.0f);
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
        SoundManager.playSound(GameResources.teleport, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(1.0f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
        int particleCount = 35;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            this.getLevel().entityManager.addParticle((float)this.x + dx * 2.0f, (float)this.y + dy * 2.0f, typeSwitcher.next()).sizeFades(8, 14).movesFriction(0.0f, -5.0f, 0.8f).color(ThemeColorRegistry.TOPAZ.getRandomColor()).givesLight(55.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
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

