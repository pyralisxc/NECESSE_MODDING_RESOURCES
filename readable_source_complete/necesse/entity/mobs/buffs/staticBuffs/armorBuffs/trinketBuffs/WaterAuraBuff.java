/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.AuraBuff;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class WaterAuraBuff
extends AuraBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        return super.getTooltip(ab, blackboard);
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.WATER.getRandomColor();
    }
}

