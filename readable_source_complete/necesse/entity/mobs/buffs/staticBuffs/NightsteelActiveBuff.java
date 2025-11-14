/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.drawOptions.human.HumanDrawOptions;

public class NightsteelActiveBuff
extends Buff
implements HumanDrawBuff {
    public NightsteelActiveBuff() {
        this.shouldSave = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.UNTARGETABLE, true);
        buff.setModifier(BuffModifiers.INTIMIDATED, true);
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(1.0f));
        buff.setMaxModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.0f));
    }

    @Override
    public void addHumanDraw(ActiveBuff buff, HumanDrawOptions drawOptions) {
        float alpha = GameUtils.getAnimFloatContinuous(buff.getDurationLeft(), 1000);
        drawOptions.alpha(alpha);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(108, 37, 92)).givesLight(0.0f, 0.5f).height(16.0f);
        }
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.getLevel().tickManager().isGameTickInSecond(5)) {
            buff.owner.getLevel().entityManager.mobs.stream().forEach(m -> m.ai.blackboard.submitEvent("refreshBossDespawn", new AIEvent()));
        }
    }
}

