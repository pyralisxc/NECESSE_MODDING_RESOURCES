/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class RunicSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public FloatUpgradeValue attackSpeed = new FloatUpgradeValue().setBaseValue(0.1f).setUpgradedValue(1.0f, 0.2f);
    public FloatUpgradeValue combatManaRegen = new FloatUpgradeValue().setBaseValue(1.0f).setUpgradedValue(1.0f, 2.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ATTACK_SPEED, this.attackSpeed.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.COMBAT_MANA_REGEN, this.combatManaRegen.getValue(buff.getUpgradeTier()));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "runicset1"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        ActiveBuff runicBuff = buff.owner.buffManager.getBuff(BuffRegistry.RUNIC_EMPOWERMENT);
        if (runicBuff == null) {
            runicBuff = new ActiveBuff(BuffRegistry.RUNIC_EMPOWERMENT, (Mob)player, 1.0f, null);
            runicBuff.getGndData().setBoolean("charging", true);
            buff.owner.buffManager.addBuff(runicBuff, false);
        } else {
            runicBuff.getGndData().setBoolean("charging", true);
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.RUNIC_EMPOWERMENT);
    }
}

