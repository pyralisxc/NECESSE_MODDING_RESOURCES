/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.inventory.item.ItemStatTip
 */
package aphorea.buffs.SetBonus;

import java.util.LinkedList;
import necesse.engine.modifiers.ModifierContainer;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.inventory.item.ItemStatTip;

public class RockySetBonusBuff
extends SetBonusBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, (Object)Float.valueOf(-0.1f));
        buff.addModifier(BuffModifiers.ARMOR_FLAT, (Object)10);
        buff.addModifier(BuffModifiers.MAX_RESILIENCE_FLAT, (Object)10);
        buff.addModifier(BuffModifiers.RESILIENCE_DECAY, (Object)Float.valueOf(-1.0f));
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }
}

