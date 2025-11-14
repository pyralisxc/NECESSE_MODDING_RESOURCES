/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.entity.particle.WebWeaverWebParticle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class WebWeaverWebEvent
extends GroundEffectEvent {
    private long startDelay = 1000L;
    private MobHitCooldowns hitCooldowns;
    private WebWeaverWebParticle particle;
    private GameDamage damage;
    private float resilienceGain;
    private int tickCounter;
    private long startTime;
    protected SoundPlayer glyphSoundPlayer;

    public WebWeaverWebEvent() {
    }

    public WebWeaverWebEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage, float resilienceGain, long startDelay) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.resilienceGain = resilienceGain;
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
        if (this.isClient()) {
            this.particle = new WebWeaverWebParticle(this.level, this.x, this.y, this.startDelay + 5000L, this.startDelay);
            this.level.entityManager.addParticle(this.particle, true, Particle.GType.CRITICAL);
            this.glyphSoundPlayer = SoundManager.playSound(GameResources.crystalGlyph, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.4f).pitch(GameRandom.globalRandom.getFloatBetween(0.18f, 0.22f)).falloffDistance(1000));
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
        if (this.tickCounter > 100) {
            this.over();
        } else {
            super.clientTick();
        }
        if (this.glyphSoundPlayer != null) {
            this.glyphSoundPlayer.refreshLooping(0.5f);
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
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
        return this.level.getWorldEntity().getTime() >= this.startTime + this.startDelay - 200L;
    }

    @Override
    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
        if (this.glyphSoundPlayer != null) {
            this.glyphSoundPlayer.stop();
        }
    }

    @Override
    public boolean removed() {
        if (this.glyphSoundPlayer != null) {
            this.glyphSoundPlayer.stop();
        }
        return super.removed();
    }
}

