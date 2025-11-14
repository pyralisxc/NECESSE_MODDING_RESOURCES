/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.inventory.item.ItemStatTip
 */
package aphorea.buffs.SetBonus;

import aphorea.registry.AphModifiers;
import java.util.LinkedList;
import necesse.engine.modifiers.ModifierContainer;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.inventory.item.ItemStatTip;

public class SwampMaskSetBonusBuff
extends SetBonusBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(AphModifiers.MAGIC_HEALING_FLAT, (Object)1);
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }
}

