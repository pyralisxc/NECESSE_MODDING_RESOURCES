/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs;

import java.util.LinkedList;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;

public abstract class ArmorBuff
extends Buff {
    public ArmorBuff() {
        this.canCancel = false;
        this.isVisible = false;
        this.isPassive = true;
        this.shouldSave = false;
    }

    @Deprecated
    public int getUpgradeLevel(ActiveBuff buff) {
        return buff.getUpgradeLevel();
    }

    @Deprecated
    public float getUpgradeTier(ActiveBuff buff) {
        return buff.getUpgradeTier();
    }

    public void tickEffect(ActiveBuff buff, Mob owner) {
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        LinkedList<ItemStatTip> statTips = new LinkedList<ItemStatTip>();
        this.addStatTooltips(statTips, ab, blackboard.get(ActiveBuff.class, "compareValues"));
        for (ItemStatTip statTip : statTips) {
            tooltips.add(statTip.toTooltip(GameColor.GREEN.color.get(), GameColor.RED.color.get(), GameColor.YELLOW.color.get(), false));
        }
        return tooltips;
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
    }
}

