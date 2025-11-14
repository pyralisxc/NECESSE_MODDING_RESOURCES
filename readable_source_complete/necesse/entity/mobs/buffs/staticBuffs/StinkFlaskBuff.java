/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class StinkFlaskBuff
extends Buff {
    public StinkFlaskBuff() {
        this.isImportant = true;
        this.canCancel = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MOB_SPAWN_CAP, Float.valueOf(0.5f));
        buff.setModifier(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.5f));
        buff.setMinModifier(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 140);
        buff.setModifier(BuffModifiers.TARGET_RANGE, Float.valueOf(0.5f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        Mob owner = buff.owner;
        if (owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(91, 130, 36)).height(16.0f);
        }
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", this.getStringID());
    }
}

