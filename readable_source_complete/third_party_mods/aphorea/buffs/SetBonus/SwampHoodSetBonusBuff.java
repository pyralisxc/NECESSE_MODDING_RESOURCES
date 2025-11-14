/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.inventory.item.ItemStatTip
 */
package aphorea.buffs.SetBonus;

import aphorea.registry.AphModifiers;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.ItemStatTip;

public class SwampHoodSetBonusBuff
extends SetBonusBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(AphModifiers.MAGIC_HEALING_FLAT, (Object)1);
        buff.addModifier(AphModifiers.MAGIC_HEALING_GRACE, (Object)Float.valueOf(0.2f));
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add((Object)new StringTooltips(Localization.translate((String)"itemtooltip", (String)"magichealinggrace")));
        return tooltips;
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }
}

