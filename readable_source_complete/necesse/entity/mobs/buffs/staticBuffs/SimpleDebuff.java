/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class SimpleDebuff
extends Buff {
    private GameMessage[] tooltips;
    private ModifierValue<?>[] modifiers;
    private Color particleColor;

    public SimpleDebuff(Color particleColor, String tooltipKey, ModifierValue<?> ... modifiers) {
        this(particleColor, new String[]{tooltipKey}, modifiers);
    }

    public SimpleDebuff(Color particleColor, GameMessage tooltip, ModifierValue<?> ... modifiers) {
        this(particleColor, new GameMessage[]{tooltip}, modifiers);
    }

    public SimpleDebuff(Color particleColor, String[] tooltipKeys, ModifierValue<?> ... modifiers) {
        this(particleColor, (GameMessage[])Arrays.stream(tooltipKeys).map(key -> new LocalMessage("buff", (String)key)).toArray(LocalMessage[]::new), modifiers);
    }

    public SimpleDebuff(Color particleColor, GameMessage[] tooltips, ModifierValue<?> ... modifiers) {
        this.particleColor = particleColor;
        this.tooltips = tooltips;
        this.modifiers = modifiers;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply(buff);
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        if (this.particleColor == null) {
            return;
        }
        Mob owner = buff.owner;
        if (owner.isVisible() && GameRandom.globalRandom.nextInt(3) == 0) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(this.particleColor).height(16.0f);
        }
    }
}

