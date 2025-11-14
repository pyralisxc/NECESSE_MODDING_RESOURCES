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

public class DebugBuff
extends Buff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_SUMMONS, 6);
        buff.setModifier(BuffModifiers.ATTACK_SPEED, Float.valueOf(1.0f));
        buff.setModifier(BuffModifiers.DASH_STACKS, 100);
        buff.setModifier(BuffModifiers.DASH_COOLDOWN, Float.valueOf(-10.0f));
        buff.setModifier(BuffModifiers.STAMINA_CAPACITY, Float.valueOf(10.0f));
        buff.setModifier(BuffModifiers.STAMINA_REGEN, Float.valueOf(10.0f));
        buff.setModifier(BuffModifiers.MAX_FOOD_BUFFS, 5);
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
        this.displayName = new StaticMessage("Debug buff");
    }
}

