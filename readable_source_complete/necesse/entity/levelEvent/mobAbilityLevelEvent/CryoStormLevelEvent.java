/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.level.maps.LevelObjectHit;

public class CryoStormLevelEvent
extends GroundEffectEvent {
    private final float chargeDurationSeconds = 2.0f;
    private final float stormDurationSeconds = 2.5f;
    private final float falloutDurationSeconds = 3.0f;
    private final float secondsDuration = 7.5f;
    private int stormPhase = 0;
    private final int stormWidth = 1024;
    private MobHitCooldowns hitCooldowns;
    private int tickCounter;
    private GameDamage damage;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    private final GameRandom random = GameRandom.globalRandom;

    public CryoStormLevelEvent() {
    }

    public CryoStormLevelEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public Shape getHitBox() {
        int height = 768;
        return new Rectangle(this.x - 512, this.y - height / 2, 1024, height);
    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns(500);
        this.startChargingPhase();
    }

    protected void hitTargets(int range, boolean freeze) {
        if (this.isClient()) {
            return;
        }
        int rangeY = (int)((float)range * 0.75f);
        Rectangle rectangle = new Rectangle(this.x - range, this.y - range, range * 2, rangeY * 2);
        GameUtils.streamTargets(this.owner, rectangle).filter(this::canHit).filter(m -> {
            float dx = m.x - (float)this.x;
            float dy = (m.y - (float)this.y) / 0.75f;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            return distance <= (float)range;
        }).forEach(m -> this.serverApplyHitBuffs((Mob)m, freeze));
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !mob.isBoss() && this.hitCooldowns.canHit(mob);
    }

    public void serverApplyHitBuffs(Mob target, boolean freeze) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
        if (!freeze) {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.GENERIC_ICE_SLOW, target, 2.8f, null), true);
        } else {
            ActiveBuff freezeBuff = new ActiveBuff(BuffRegistry.FROZEN_MOB, target, 3.0f, null);
            target.buffManager.addBuff(freezeBuff, true);
        }
    }

    protected void runWaveHit(int ticksWhenStarting, boolean freeze) {
        if (this.tickCounter == ticksWhenStarting) {
            this.hitTargets(128, freeze);
        }
        if (this.tickCounter == ticksWhenStarting + 4) {
            this.hitTargets(204, freeze);
        }
        if (this.tickCounter == ticksWhenStarting + 7) {
            this.hitTargets(341, freeze);
        }
        if (this.tickCounter == ticksWhenStarting + 10) {
            this.hitTargets(512, freeze);
        }
    }

    @Override
    public void clientHit(Mob target) {
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
    }

    @Override
    public void serverTick() {
        this.tick();
    }

    @Override
    public void clientTick() {
        if (!this.isClient()) {
            return;
        }
        this.tick();
    }

    protected void tick() {
        ++this.tickCounter;
        if ((float)this.tickCounter > 150.0f) {
            this.over();
        }
        if (this.stormPhase == 0) {
            this.runChargingPhase();
            if ((float)this.tickCounter > 40.0f) {
                this.stormPhase = 1;
                this.startStormPhase();
            }
        }
        if (this.stormPhase == 1) {
            this.runStormPhase();
            if ((float)this.tickCounter >= 90.0f) {
                this.stormPhase = 2;
                this.endStormPhase();
            }
        }
        if (this.stormPhase == 2) {
            this.runFalloutPhase();
        }
    }

    protected void startChargingPhase() {
        this.stormPhase = 0;
        this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.CRYO_IMMUNTY, this.owner, 4.5f, null), false);
    }

    protected void runChargingPhase() {
        if (!this.isClient()) {
            return;
        }
        this.getLevel().entityManager.addParticle((float)this.x + this.random.getFloatBetween(-5.0f, 5.0f), (float)this.y + this.random.getFloatBetween(-5.0f, 5.0f), this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(this.random.getIntBetween(0, 4), 0, 12)).height(0.0f).movesConstant(this.random.getFloatBetween(-22.0f, 22.0f), this.random.getFloatBetween(-32.0f, -52.0f)).color(ThemeColorRegistry.ICE.getRandomColor()).lifeTime(500).sizeFades(50, 24);
    }

    public Point2D.Float randomPointInUnitCircle() {
        double angle = Math.PI * 2 * this.random.nextDouble();
        double radius = Math.sqrt(this.random.nextDouble());
        float x = (float)(radius * Math.cos(angle));
        float y = (float)(radius * Math.sin(angle));
        return new Point2D.Float(x, y);
    }

    protected void startStormPhase() {
        int i;
        if (!this.isClient()) {
            this.hitTargets(512, false);
            return;
        }
        for (i = 0; i < 30; ++i) {
            this.spawnSnowRing(() -> ThemeColorRegistry.ICE.getRandomColor(), 35, 8, 1000, (float)i * 0.05f);
        }
        for (i = 0; i < 800; ++i) {
            float exponentialFallOff = 1.0f - (float)Math.pow(GameRandom.globalRandom.getFloatBetween(0.0f, 1.0f), 4.0);
            float rX = 512.0f * exponentialFallOff;
            float rY = rX * 0.75f;
            Point2D.Float rp = this.randomPointInUnitCircle();
            this.level.entityManager.addParticle((float)this.x + rp.x * rX, (float)this.y + rp.y * rY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).height(0.0f).dontRotate().fadesAlpha(0.4f, 0.6f).sizeFades(1, (int)(100.0f * exponentialFallOff)).lifeTime(7000);
        }
    }

    protected void runStormPhase() {
        if (!this.isClient()) {
            this.runWaveHit(40, false);
            return;
        }
        if (this.tickCounter % 3 == 0) {
            this.spawnSnowRing(() -> ThemeColorRegistry.ICE.getRandomColor(), 22, 8, 2500 - this.tickCounter * 10, 1.0f);
        }
        int snowParticles = 20;
        for (int i = 0; i < snowParticles; ++i) {
            float startX = this.x - 500;
            float startY = this.y + 400;
            float endX = startX + 300.0f;
            float endY = startY + 400.0f;
            float XdistPerParticle = (endX - startX) / (float)snowParticles;
            float YdistPerParticle = (endY - startY) / (float)snowParticles;
            float sin = (float)Math.sin(Math.PI * (double)i / (double)snowParticles);
            this.getLevel().entityManager.addParticle(startX - XdistPerParticle * (float)i, startY - YdistPerParticle * (float)i, this.particleTypeSwitcher.next()).sprite(GameResources.debrisParticles.sprite(this.random.getIntBetween(0, 5), 0, 10)).height(10.0f).movesConstant(240.0f + 24.0f * sin, -80.0f - 8.0f * sin).color(ThemeColorRegistry.ICE.getRandomColor()).lifeTime(1300).fadesAlpha(0.0f, 0.5f * sin).sizeFadesInAndOut(12, 24, 0.5f);
        }
    }

    protected void endStormPhase() {
        if (this.isClient()) {
            for (int i = 0; i < 30; ++i) {
                this.spawnSnowRing(() -> ThemeColorRegistry.BLUE.getRandomColor(), 15, 48, 1000, (float)i * 0.05f);
            }
            SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.25f).pitch(1.3f));
        } else if (this.owner.buffManager.hasBuff(BuffRegistry.CRYO_IMMUNTY)) {
            this.owner.buffManager.removeBuff(BuffRegistry.CRYO_IMMUNTY, true);
        }
    }

    protected void runFalloutPhase() {
        if (!this.isClient()) {
            this.runWaveHit(90, true);
        }
    }

    protected void spawnSnowRing(Supplier<Color> color, int startSize, int endSize, int lifetime, float distanceMultiplier) {
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(this.random.nextFloat() * 360.0f));
        for (int i = 0; i < 25; ++i) {
            float distance = distanceMultiplier * 1024.0f * 0.6f - (float)i;
            this.getLevel().entityManager.addParticle((float)this.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)this.random.getIntBetween(-50, 50), (float)this.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)this.random.getIntBetween(-50, 50) * 0.75f, this.particleTypeSwitcher.next()).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float spinFactor = 443.0f;
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * spinFactor / 250.0f), Float::sum).floatValue();
                float distY = distance * 0.75f;
                pos.x = (float)this.x + GameMath.sin(angle) * (distance * lifePercent);
                pos.y = (float)this.y + GameMath.cos(angle) * (distY * lifePercent);
            }).color(color.get()).lifeTime(lifetime).sizeFades(startSize, endSize);
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
}

