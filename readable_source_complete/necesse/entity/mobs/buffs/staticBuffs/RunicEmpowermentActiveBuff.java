/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class RunicEmpowermentActiveBuff
extends Buff {
    public RunicEmpowermentActiveBuff() {
        this.isVisible = true;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMaxModifier(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(0.0f), Integer.MAX_VALUE);
        buff.setMaxModifier(BuffModifiers.MANA_REGEN, Float.valueOf(0.0f), Integer.MAX_VALUE);
        buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.01f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickEmpowermentBuff(buff);
        float particlesPerSecond = 30.0f;
        float particlesPerTick = particlesPerSecond / 20.0f;
        float particleBuffer = buff.getGndData().getFloat("particleBuffer", 0.0f) + particlesPerTick;
        if (particleBuffer >= 1.0f) {
            float percent = GameMath.limit((float)buff.getStacks() / 100.0f, 0.0f, 1.0f);
            Color color = Color.getHSBColor(GameMath.lerp(percent, 0.5f, 0.6666667f), 0.8f, buff.getGndData().getBoolean("charging") ? 0.8f : 0.5f);
            buff.owner.getLevel().entityManager.addParticle(buff.owner.x + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), buff.owner.y + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), Particle.GType.IMPORTANT_COSMETIC).movesConstant(0.0f, -10.0f).ignoreLight(true).height(10.0f).givesLight(200.0f, 0.5f).color(color).sizeFades(16, 20).size((options, lifeTime, timeAlive, lifePercent) -> options.size((int)(20.0f * (1.0f - lifePercent)), (int)(20.0f * (1.0f - lifePercent)))).lifeTime(500);
            particleBuffer -= 1.0f;
        }
        buff.getGndData().setFloat("particleBuffer", particleBuffer);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.tickEmpowermentBuff(buff);
    }

    public void tickEmpowermentBuff(ActiveBuff buff) {
        if (buff.getGndData().getBoolean("charging")) {
            float stacksIncreaseBuffer;
            float stacksIncreasePerSecond = 10.0f;
            float manaUsagePerSecond = 10.0f;
            float stacksIncreasePerTick = stacksIncreasePerSecond / 20.0f;
            float manaUsagePerTick = manaUsagePerSecond / 20.0f;
            float remainingMana = buff.owner.getMana() - manaUsagePerTick;
            buff.owner.setManaHidden(remainingMana);
            if (remainingMana < 1.0f) {
                buff.getGndData().setBoolean("charging", false);
            }
            if ((stacksIncreaseBuffer = buff.getGndData().getFloat("stacksIncreaseBuffer", 0.0f) + stacksIncreasePerTick) >= 1.0f) {
                int stacksIncreaseBufferInt = (int)stacksIncreaseBuffer;
                stacksIncreaseBuffer -= (float)stacksIncreaseBufferInt;
                for (int i = 0; i < stacksIncreaseBufferInt; ++i) {
                    buff.addStack(1000, null);
                }
                buff.forceManagerUpdate();
            }
            buff.getGndData().setFloat("stacksIncreaseBuffer", stacksIncreaseBuffer);
        }
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 100;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public int getStacksDisplayCount(ActiveBuff buff) {
        return 1;
    }

    @Override
    public String getDurationText(ActiveBuff buff) {
        return buff.getStacks() + "%";
    }

    @Override
    public int getRemainingStacksDuration(ActiveBuff buff, AtomicBoolean sendUpdatePacket) {
        return 25;
    }
}

