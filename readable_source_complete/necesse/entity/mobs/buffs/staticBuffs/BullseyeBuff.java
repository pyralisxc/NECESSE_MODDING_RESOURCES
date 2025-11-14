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

public class BullseyeBuff
extends Buff {
    public BullseyeBuff() {
        this.canCancel = false;
        this.isImportant = true;
        this.isPassive = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 5;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public void onStacksUpdated(ActiveBuff buff, ActiveBuff other) {
        super.onStacksUpdated(buff, other);
        if (buff.getStacks() == 5) {
            buff.setModifier(BuffModifiers.RANGED_CRIT_CHANCE, Float.valueOf(1.0f));
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("bufftooltip", "bullseyetip"), 400);
        return tooltips;
    }
}

