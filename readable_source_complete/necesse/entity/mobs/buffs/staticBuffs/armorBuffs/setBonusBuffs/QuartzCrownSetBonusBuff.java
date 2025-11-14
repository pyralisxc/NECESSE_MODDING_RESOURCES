/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketQuartzSetEvent;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class QuartzCrownSetBonusBuff
extends SetBonusBuff {
    public IntUpgradeValue maxSummons = new IntUpgradeValue().setBaseValue(1).setUpgradedValue(1.0f, 2);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        Mob owner;
        Level level;
        super.onWasHit(buff, event);
        if (!event.wasPrevented && (level = (owner = buff.owner).getLevel()).isServer() && !owner.buffManager.hasBuff(BuffRegistry.Debuffs.QUARTZ_SET_COOLDOWN.getID())) {
            owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, owner, 2.0f, null), true);
            owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.QUARTZ_SET_COOLDOWN, owner, 20.0f, null), true);
            level.getServer().network.sendToClientsWithEntity(new PacketQuartzSetEvent(buff.owner.getUniqueID()), owner);
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "quartzcrownset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

