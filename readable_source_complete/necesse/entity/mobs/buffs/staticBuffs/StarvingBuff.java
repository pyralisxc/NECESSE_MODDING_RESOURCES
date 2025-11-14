/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class StarvingBuff
extends Buff {
    public StarvingBuff() {
        this.shouldSave = false;
        this.isPassive = true;
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMaxModifier(BuffModifiers.HEALTH_REGEN, Float.valueOf(0.0f), 10000);
        buff.setMaxModifier(BuffModifiers.COMBAT_HEALTH_REGEN, Float.valueOf(0.0f), 10000);
        buff.setMinModifier(BuffModifiers.SLOW, Float.valueOf(0.25f), 10000);
        buff.setModifier(BuffModifiers.SLOW, Float.valueOf(0.25f));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("bufftooltip", "hungrytip"));
        tooltips.add(Localization.translate("bufftooltip", "starvingtip"));
        return tooltips;
    }
}

