/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class DawnSwirlEvent
extends GroundEffectEvent {
    private float particleBuffer;
    private final float secondsDuration = 0.6f;
    private int tickCounter;
    private MobHitCooldowns hitCooldowns;
    private GameDamage damage;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public DawnSwirlEvent() {
    }

    public DawnSwirlEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public Shape getHitBox() {
        int width = 180;
        int height = 136;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
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
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE.getID(), target, 5000, (Attacker)this.owner), true);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if ((float)this.tickCounter > 12.0f) {
            this.over();
        } else {
            float particlesPerSecond = 80.0f;
            this.particleBuffer += particlesPerSecond / 20.0f;
            while (this.particleBuffer >= 1.0f) {
                if (this.isClient()) {
                    GameRandom random = GameRandom.globalRandom;
                    AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
                    float distance = 150.0f;
                    this.getLevel().entityManager.addParticle(this.owner.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), this.owner.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                        float distY = distance * lifePercent * 0.85f;
                        pos.x = this.owner.x + GameMath.sin(angle) * (distance * lifePercent);
                        pos.y = this.owner.y + GameMath.cos(angle) * distY * 0.85f;
                    }).color((options, lifeTime, timeAlive, lifePercent) -> {
                        float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                        options.color(new Color((int)(255.0f - 55.0f * clampedLifePercent), (int)(225.0f - 200.0f * clampedLifePercent), (int)(155.0f - 125.0f * clampedLifePercent)));
                    }).lifeTime(1000).sizeFades(50, 24);
                }
                this.particleBuffer -= 1.0f;
            }
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if ((float)this.tickCounter > 12.0f) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.x = (int)this.owner.x;
        this.y = (int)this.owner.y;
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }
}

