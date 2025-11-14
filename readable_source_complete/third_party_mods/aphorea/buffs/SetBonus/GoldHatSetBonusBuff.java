/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.inventory.item.ItemStatTip
 */
package aphorea.buffs.SetBonus;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.ItemStatTip;

public class GoldHatSetBonusBuff
extends SetBonusBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(false, false).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add((Object)new StringTooltips(Localization.translate((String)"itemtooltip", (String)"goldhatsetbonus")));
        return tooltips;
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.getCurrentSpeed() == 0.0f) {
            if (((Float)buff.getModifier(BuffModifiers.RANGED_ATTACK_SPEED)).floatValue() != 0.6f) {
                buff.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, (Object)Float.valueOf(0.6f));
            }
        } else if (((Float)buff.getModifier(BuffModifiers.RANGED_ATTACK_SPEED)).floatValue() != 0.0f) {
            buff.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, (Object)Float.valueOf(0.0f));
        }
    }
}

