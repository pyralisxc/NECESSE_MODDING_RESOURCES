/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class BarkskinBuff
extends Buff {
    public BarkskinBuff() {
        this.canCancel = false;
        this.isImportant = true;
        this.isPassive = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ARMOR_FLAT, 10);
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 3;
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (buff.getStacks() > 1) {
            buff.removeStack(true);
        } else {
            buff.remove();
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("bufftooltip", "barkskintip"), 400);
        return tooltips;
    }
}

