/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.MobHealthChangedEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.BloodPlateSetBonusBuff;
import necesse.inventory.item.ItemStatTip;

public class BloodplateCowlSetBonusBuff
extends BloodPlateSetBonusBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        super.init(buff, eventSubscriber);
        eventSubscriber.subscribeEvent(MobHealthChangedEvent.class, event -> {
            if (event.currentHealth < event.lastHealth && !event.fromUpdatePacket) {
                ActiveBuff activeBuff = new ActiveBuff(BuffRegistry.BLOODPLATE_COWL_ACTIVE, buff.owner, 4.0f, null);
                buff.owner.buffManager.addBuff(activeBuff, false);
            }
        });
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

