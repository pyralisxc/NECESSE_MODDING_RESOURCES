/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.hostile.bosses.MoonlightDancerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class CrushingDarknessBuff
extends Buff {
    public CrushingDarknessBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            GameRandom random = GameRandom.globalRandom;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            float distance = 75.0f;
            for (int i = 0; i < 4; ++i) {
                owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), owner.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 30.0f / 250.0f), Float::sum).floatValue();
                    float distY = (distance - 20.0f) * 0.85f;
                    pos.x = owner.x + GameMath.sin(angle) * (distance - distance / 2.0f * lifePercent);
                    pos.y = owner.y + GameMath.cos(angle) * distY - 20.0f * lifePercent;
                }).color((options, lifeTime, timeAlive, lifePercent) -> {
                    options.color(new Color(0, 0, 0));
                    if (lifePercent > 0.5f) {
                        options.alpha(2.0f * (1.0f - lifePercent));
                    }
                }).size((options, lifeTime, timeAlive, lifePercent) -> options.size(22, 22)).lifeTime(1000);
            }
        }
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        this.inflictDamage(buff);
    }

    private void inflictDamage(ActiveBuff buff) {
        int stacks = 0;
        if (buff.owner.buffManager.hasBuff(BuffRegistry.STAR_BARRIER_BUFF)) {
            if (buff.owner.isClient()) {
                this.spawnBrokenBarrierParticles(buff);
                SoundManager.playSound(GameResources.shatter2, (SoundEffect)SoundEffect.effect(buff.owner).volume(3.0f).pitch(0.8f));
            } else {
                stacks = buff.owner.buffManager.getStacks(BuffRegistry.STAR_BARRIER_BUFF);
                buff.owner.buffManager.removeBuff(BuffRegistry.STAR_BARRIER_BUFF, true);
            }
        }
        if (buff.owner.isClient()) {
            SoundManager.playSound(GameResources.magicroar, (SoundEffect)SoundEffect.effect(buff.owner).volume(5.0f).pitch(1.75f));
            this.spawnDarknessDamageParticles(buff);
        } else {
            GameDamage finalDamage = MoonlightDancerMob.crushingDarknessDamage.modDamage(1.0f - (float)stacks * 0.25f);
            int attackerUniqueID = buff.getGndData().getInt("uniqueID");
            Mob mob = buff.owner.getLevel().entityManager.mobs.get(attackerUniqueID, false);
            if (mob != null) {
                buff.owner.isServerHit(finalDamage, 0.0f, 0.0f, 0.0f, mob);
            }
        }
    }

    private void spawnDarknessDamageParticles(ActiveBuff buff) {
        Mob owner = buff.owner;
        GameRandom random = GameRandom.globalRandom;
        int angle = random.nextInt(360);
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 4; ++j) {
                owner.getLevel().entityManager.addTopParticle(owner, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().movesFrictionAngle(angle + random.getIntBetween(-10, 10) + j * 90, (j % 2 == 0 ? 100 : 50) + random.getIntBetween(-50, 20), 0.8f).color(new Color(0, 0, 0)).givesLight().lifeTime(1000);
            }
        }
    }

    private void spawnBrokenBarrierParticles(ActiveBuff buff) {
        int particleCount = 25;
        Mob owner = buff.owner;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 360.0f / (float)particleCount;
        for (int i = 0; i < particleCount; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            owner.getLevel().entityManager.addParticle(owner, typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(184, 174, 255)).givesLight(247.0f, 0.3f).heightMoves(0.0f, 30.0f).lifeTime(1500);
        }
    }
}

