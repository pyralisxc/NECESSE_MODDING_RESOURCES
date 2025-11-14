/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.AscendedFractureParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AscendedFractureGroundEvent
extends GroundEffectEvent {
    private GameDamage damage;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(1000);
    protected int tickCounter;
    private AscendedFractureParticle particle;
    private long lifetime;

    public AscendedFractureGroundEvent() {
    }

    public AscendedFractureGroundEvent(Mob owner, int x, int y, GameDamage damage, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public void init() {
        super.init();
        this.lifetime = 1000L;
        this.tickCounter = 0;
        if (this.isClient() && this.lifetime != 0L) {
            this.particle = new AscendedFractureParticle(this.level, this.x, this.y, this.lifetime + 250L);
            this.level.entityManager.addParticle(this.particle, true, Particle.GType.CRITICAL);
            SoundManager.playSound(GameRandom.globalRandom.getOneOf(GameResources.iceCrack1, GameResources.iceCrack2, GameResources.iceCrack3), (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 1.0f)));
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 32;
        int height = 32;
        return new Rectangle(this.x - width / 2 + 16, this.y - height / 2 + 16, width, height);
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
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 20L * (this.lifetime / 1000L)) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 20L * (this.lifetime / 1000L)) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
    }
}

