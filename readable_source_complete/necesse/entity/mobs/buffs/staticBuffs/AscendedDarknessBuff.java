/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AscendedDarknessBuff
extends Buff {
    public AscendedDarknessBuff() {
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
                owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), owner.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, Particle.GType.CRITICAL).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 30.0f / 250.0f), Float::sum).floatValue();
                    float distY = (distance - 20.0f) * 0.85f;
                    pos.x = owner.x + GameMath.sin(angle) * (distance - distance / 2.0f * lifePercent);
                    pos.y = owner.y + GameMath.cos(angle) * distY - 20.0f * lifePercent;
                }).sizeFades(12, 24).lifeTime(1000);
            }
        }
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        if (buff.isExpired()) {
            this.inflictDamage(buff);
        }
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 4;
    }

    private void inflictDamage(ActiveBuff buff) {
        if (buff.owner.isClient()) {
            SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(buff.owner).volume(5.0f).pitch(0.5f));
            this.spawnDarknessDamageParticles(buff);
        } else {
            GameDamage finalDamage = AscendedWizardMob.moonDarkness;
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
                owner.getLevel().entityManager.addTopParticle(owner, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).sizeFades(30, 40).rotates().movesFrictionAngle(angle + random.getIntBetween(-10, 10) + j * 90, (j % 2 == 0 ? 100 : 50) + random.getIntBetween(-50, 20), 0.8f).givesLight().lifeTime(1000);
            }
        }
    }
}

