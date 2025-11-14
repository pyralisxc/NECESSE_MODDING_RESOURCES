/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.entity.particle.TheCrimsonSkyParticle;
import necesse.entity.trails.Trail;
import necesse.level.maps.LevelObjectHit;

public class TheCrimsonSkyEvent
extends GroundEffectEvent {
    protected int tickCounter;
    protected int hitCounter;
    protected MobHitCooldowns hitCooldowns;
    protected GameDamage damage;
    protected float resilienceGain;
    protected Trail trail;
    private TheCrimsonSkyParticle particle;

    public TheCrimsonSkyEvent() {
    }

    public TheCrimsonSkyEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage, float resilienceGain) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.resilienceGain = resilienceGain;
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            this.particle = new TheCrimsonSkyParticle(this.level, this.x, this.y, 2000L);
            this.level.entityManager.addParticle(this.particle, Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 95;
        int height = 80;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
        ++this.hitCounter;
        if (this.hitCounter >= 9) {
            this.over();
        }
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage.modFinalMultiplier(0.25f), 0.0f, 0.0f, 0.0f, this.owner);
            if (this.owner != null && target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
                this.owner.addResilience(this.resilienceGain);
                this.resilienceGain = 0.0f;
            }
            this.hitCooldowns.startCooldown(target);
            ++this.hitCounter;
            if (this.hitCounter >= 9) {
                this.over();
            }
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
        if (this.tickCounter > 40) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 40) {
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

