/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DebugSpeedBuff
extends Buff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(1.0f));
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(100.0f));
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 1;
    }

    @Override
    public boolean overridesStackDuration() {
        return false;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add("Used for testing purposes");
        return tooltips;
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new StaticMessage("Debug speed buff");
    }
}

