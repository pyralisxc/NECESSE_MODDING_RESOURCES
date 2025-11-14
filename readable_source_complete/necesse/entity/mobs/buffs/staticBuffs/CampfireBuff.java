/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.VicinityBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class CampfireBuff
extends VicinityBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(-0.5f));
        buff.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, Float.valueOf(2.0f));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("bufftooltip", "campfiretip1"));
        tooltips.add(Localization.translate("bufftooltip", "campfiretip2"));
        return tooltips;
    }
}

