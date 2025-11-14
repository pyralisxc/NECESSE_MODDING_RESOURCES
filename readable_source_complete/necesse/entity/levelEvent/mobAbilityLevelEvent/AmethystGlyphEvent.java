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
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.AmethystStaffGlyphCircleParticle;
import necesse.entity.particle.AmethystStaffGlyphParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class AmethystGlyphEvent
extends GroundEffectEvent {
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(100);
    protected float buffDuration = 5.0f;
    protected long startTime;
    protected boolean chargeUpCompleted;
    protected TimeTriggeredEvents events = new TimeTriggeredEvents();

    public AmethystGlyphEvent() {
    }

    public AmethystGlyphEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.getTime();
        if (this.level.isClient()) {
            SoundManager.playSound(SoundSettingsRegistry.crystalGlyph, this.owner.x, this.owner.y);
        }
        int chargeUpTime = 400;
        int lingerTime = 200;
        int circleSpawnTime = 50;
        this.level.entityManager.addParticle(new AmethystStaffGlyphCircleParticle(this.level, this.x, this.y, chargeUpTime + lingerTime), Particle.GType.CRITICAL);
        this.events.addEvent(circleSpawnTime, () -> {
            AmethystStaffGlyphParticle glyphParticle = new AmethystStaffGlyphParticle(this.level, this.x, this.y, chargeUpTime + lingerTime - circleSpawnTime);
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
            target.addBuff(new ActiveBuff(BuffRegistry.AMETHYST_GLYPH, target, this.buffDuration, (Attacker)target), true);
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
        int particleCount = 25;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            this.getLevel().entityManager.addParticle((float)this.x + dx * 2.0f, (float)this.y + dy * 2.0f, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(0.0f, -5.0f, 0.8f).color(ThemeColorRegistry.AMETHYST.getRandomColor()).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
        }
    }

    @Override
    protected Stream<Mob> streamTargets(Shape hitbox) {
        if (!this.chargeUpCompleted) {
            return Stream.empty();
        }
        return AmethystGlyphEvent.streamBuffableTargets(this.level, hitbox, this.owner);
    }

    public static Stream<Mob> streamBuffableTargets(Level level, Shape hitbox, Mob owner) {
        Stream<HumanMob> humanStream = level.entityManager.mobs.streamInRegionsShape(hitbox, 1).filter(m -> m.isHuman).map(m -> (HumanMob)m);
        if (owner != null) {
            humanStream = owner instanceof HumanMob ? humanStream.filter(hm -> hm == owner || hm.isFriendlyHuman((HumanMob)owner)) : (owner.isPlayer ? humanStream.filter(hm -> hm.isFriendlyClient(owner.getPvPOwner())) : humanStream.filter(hm -> !hm.canBeTargeted(owner, owner.getPvPOwner())));
        }
        return Stream.concat(humanStream, level.entityManager.players.streamInRegionsShape(hitbox, 1).filter(p -> owner == null || p == owner || !p.canBeTargeted(owner, owner.getPvPOwner())));
    }

    @Override
    protected boolean canHitLocalClient(ClientClient me) {
        if (!this.chargeUpCompleted) {
            return false;
        }
        return this.owner == null || !me.playerMob.canBeTargeted(this.owner, this.owner.getPvPOwner());
    }
}

