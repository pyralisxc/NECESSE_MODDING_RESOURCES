/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.particle.Particle;

public class VoidWizardExplosionEvent
extends ExplosionEvent
implements Attacker {
    public VoidWizardExplosionEvent() {
        this(0.0f, 0.0f, null);
    }

    public VoidWizardExplosionEvent(float x, float y, Mob owner) {
        super(x, y, VoidWizard.homingExplosionRange, VoidWizard.homingExplosion, false, 0.0f, owner);
        this.sendCustomData = false;
        this.sendOwnerData = true;
        this.hitsOwner = false;
        this.knockback = 50;
    }

    @Override
    protected void playExplosionEffects() {
    }

    @Override
    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        this.level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX, dirY).color(new Color(50, 0, 102)).height(10.0f).givesLight(270.0f, 0.5f).lifeTime(lifeTime);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidwiz", 4);
    }
}

