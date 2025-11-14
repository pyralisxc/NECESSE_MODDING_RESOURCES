/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class SlimePoisonBuff
extends Buff {
    public SlimePoisonBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickDamage(buff, true);
        Mob owner = buff.owner;
        if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(56, 173, 45)).height(16.0f);
        }
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.tickDamage(buff, true);
    }

    public void tickDamage(ActiveBuff buff, boolean forceUpdate) {
        int maxHealth;
        float newDamage;
        float lastDamage = buff.getModifier(BuffModifiers.POISON_DAMAGE_FLAT).floatValue();
        if (lastDamage != (newDamage = Math.max((float)Math.pow(maxHealth = buff.owner.getMaxHealth(), 0.5) / 10.0f + (float)maxHealth / 50.0f, 5.0f))) {
            buff.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(newDamage));
            if (forceUpdate) {
                buff.forceManagerUpdate();
            }
        }
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.tickDamage(buff, false);
    }
}

