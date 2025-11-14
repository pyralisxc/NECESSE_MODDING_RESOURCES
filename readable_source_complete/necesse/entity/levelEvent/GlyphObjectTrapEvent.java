/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TimeTriggeredEvents;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.GlyphTrapParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.LevelObjectHit;

public abstract class GlyphObjectTrapEvent
extends GroundEffectEvent {
    public TimeTriggeredEvents events = new TimeTriggeredEvents();
    protected boolean canHitMobs = false;
    protected long spawnTime = 0L;

    public GlyphObjectTrapEvent() {
        this.allowNullOwner = true;
    }

    public GlyphObjectTrapEvent(int x, int y, GameRandom uniqueIDRandom) {
        super(null, x * 32 + 16, y * 32 + 16, uniqueIDRandom);
        this.allowNullOwner = true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getTime();
        if (this.isClient()) {
            this.events.addEvent(0, () -> {
                this.level.entityManager.addParticle(new GlyphTrapParticle(this.level, (float)this.x, (float)this.y, this.getParticleTexture(), this.getParticleColor(), this.getParticleColorHue()), Particle.GType.CRITICAL);
                SoundManager.playSound(GameResources.glyphTrapCharge, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(1.75f, 1.8f)).volume(0.25f));
                this.playGlyphSound();
            });
        }
        if (this.isServer()) {
            this.events.addEvent(200, () -> {
                this.canHitMobs = true;
            });
        }
        this.events.addEvent(400, this::over);
    }

    @Override
    public Shape getHitBox() {
        int size = 80;
        return new Rectangle(this.x - size / 2, this.y - size / 2, size, size);
    }

    @Override
    public void clientHit(Mob target) {
    }

    @Override
    public boolean canHit(Mob mob) {
        if (!mob.canLevelInteract() || mob.isBoss() || mob.isOnGenericCooldown("glyphtrap")) {
            return false;
        }
        return this.canHitMobs;
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        target.startGenericCooldown("glyphtrap", 200L);
        this.applyGlyphServer(target);
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.events.tickEvents(this.getTime() - this.spawnTime);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.events.tickEvents(this.getTime() - this.spawnTime);
    }

    public abstract GameTexture getParticleTexture();

    public abstract Color getParticleColor();

    public abstract float getParticleColorHue();

    public abstract void playGlyphSound();

    public abstract void applyGlyphServer(Mob var1);
}

