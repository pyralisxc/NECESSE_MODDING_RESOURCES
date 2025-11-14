/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.CaveSpiderWebParticle;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderWebEvent
extends GroundEffectEvent {
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(750);

    public CaveSpiderWebEvent() {
    }

    public CaveSpiderWebEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        if (this.isClient()) {
            this.level.entityManager.addParticle(new CaveSpiderWebParticle(this.level, this.x, this.y, 5000L), Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 80;
        int height = 40;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, target, 1.0f, (Attacker)this.owner), true);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
            this.over();
        } else {
            super.clientTick();
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
}

