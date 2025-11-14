/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class DemonicRageActiveBuff
extends Buff {
    public FloatUpgradeValue dmgBuffPerc = new FloatUpgradeValue().setBaseValue(0.15f).setUpgradedValue(1.0f, 0.4f);
    public FloatUpgradeValue critBuffPerc = new FloatUpgradeValue().setBaseValue(0.15f).setUpgradedValue(1.0f, 0.25f);

    public DemonicRageActiveBuff() {
        this.isVisible = true;
        this.isImportant = true;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = new ListGameTooltips();
        tooltip.add(Localization.translate("bufftooltip", "demonicragetip", "dmg", this.dmgBuffPerc, "hploss", 25), 400);
        return tooltip;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ALL_DAMAGE, this.dmgBuffPerc.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.CRIT_CHANCE, this.critBuffPerc.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(15.0f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        float particlesPerSecond = 30.0f;
        float particlesPerTick = particlesPerSecond / 20.0f;
        float particleBuffer = buff.getGndData().getFloat("particleBuffer", 0.0f) + particlesPerTick;
        if (particleBuffer >= 1.0f) {
            float percent = GameMath.limit((float)buff.getStacks() / 100.0f, 0.0f, 1.0f);
            buff.owner.getLevel().entityManager.addParticle(buff.owner.x + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), buff.owner.y + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), Particle.GType.IMPORTANT_COSMETIC).movesConstant(0.0f, -10.0f).ignoreLight(true).height(10.0f).givesLight(200.0f, 0.5f).color(new Color(181, 16, 16)).sizeFades(16, 20).size((options, lifeTime, timeAlive, lifePercent) -> options.size((int)(20.0f * (1.0f - lifePercent)), (int)(20.0f * (1.0f - lifePercent)))).lifeTime(500);
            particleBuffer -= 1.0f;
        }
        buff.getGndData().setFloat("particleBuffer", particleBuffer);
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
    public int getRemainingStacksDuration(ActiveBuff buff, AtomicBoolean sendUpdatePacket) {
        return 25;
    }
}

