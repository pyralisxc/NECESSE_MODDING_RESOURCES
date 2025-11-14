/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class VoidWizardGooEvent
extends GroundEffectEvent {
    public static final int GOO_SIZE = 200;
    public static final int GOO_PARTICLES = 8;
    private int tickCounter;
    protected MobHitCooldowns hitCooldowns;
    private Rectangle hitBox;
    private GameRandom random;
    private GameDamage damage;

    public VoidWizardGooEvent() {
    }

    public VoidWizardGooEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        this.hitBox = new Rectangle(this.x - 100, this.y - 100, 200, 200);
        this.random = new GameRandom(this.ownerID);
        this.damage = new GameDamage(30.0f, 10.0f);
    }

    @Override
    public Shape getHitBox() {
        return this.hitBox;
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
        if (this.tickCounter > 200) {
            this.over();
        } else {
            super.clientTick();
        }
        if (this.tickCounter < 60) {
            for (int i = 0; i < 8; ++i) {
                int x = this.hitBox.x + 5 + this.random.nextInt(this.hitBox.width - 10);
                int y = this.hitBox.y + 5 + this.random.nextInt(this.hitBox.height - 10);
                Color c = new Color(49, 39, 39);
                int red = (int)((float)c.getRed() + (this.random.nextFloat() - 0.5f) * 10.0f);
                int green = (int)((float)c.getGreen() + (this.random.nextFloat() - 0.5f) * 10.0f);
                int blue = (int)((float)c.getBlue() + (this.random.nextFloat() - 0.5f) * 10.0f);
                c = new Color(Math.max(0, Math.min(255, red)), Math.max(0, Math.min(255, green)), Math.max(0, Math.min(255, blue)));
                Particle.GType type = i <= 2 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
                this.level.entityManager.addParticle(x, y, type).color(c).givesLight(270.0f, 0.5f);
            }
        } else {
            for (int i = 0; i < 8; ++i) {
                int x = this.hitBox.x + 5 + this.random.nextInt(this.hitBox.width - 10);
                int y = this.hitBox.y + 5 + this.random.nextInt(this.hitBox.height - 10);
                Color c = new Color(50, 0, 102);
                int red = (int)((float)c.getRed() + (this.random.nextFloat() - 0.5f) * 10.0f);
                int green = (int)((float)c.getGreen() + (this.random.nextFloat() - 0.5f) * 10.0f);
                int blue = (int)((float)c.getBlue() + (this.random.nextFloat() - 0.5f) * 10.0f);
                c = new Color(Math.max(0, Math.min(255, red)), Math.max(0, Math.min(255, green)), Math.max(0, Math.min(255, blue)));
                Particle.GType type = i <= 2 ? Particle.GType.CRITICAL : Particle.GType.IMPORTANT_COSMETIC;
                this.level.entityManager.addParticle(x, y, type).color(c).givesLight(270.0f, 0.5f);
            }
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.owner == null || this.owner.removed() || this.tickCounter > 200) {
            this.over();
        } else {
            super.serverTick();
        }
    }
}

