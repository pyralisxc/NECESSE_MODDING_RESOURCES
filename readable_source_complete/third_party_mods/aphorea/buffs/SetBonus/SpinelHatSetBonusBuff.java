/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.MobBeforeHitEvent
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 */
package aphorea.buffs.SetBonus;

import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class SpinelHatSetBonusBuff
extends SetBonusBuff {
    float savedAmount;

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add((Object)new StringTooltips(Localization.translate((String)"itemtooltip", (String)"spinelhatsetbonus", (String)"healing", (String)AphMagicHealing.getMagicHealingToolTipPercent(ab.owner, ab.owner, 0.01f))));
        return tooltips;
    }

    public AphAreaList getAreaList(int healing) {
        return new AphAreaList(new AphArea(100.0f, 0.3f, AphColors.green).setHealingArea(healing).setDirectExecuteHealing(true));
    }

    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
        super.onBeforeAttacked(buff, event);
        if (!event.isPrevented() && event.damage.type == DamageTypeRegistry.MAGIC && event.damage.damage > 0.0f && event.target != null && event.target.isHostile) {
            float healing = event.damage.damage * 0.01f * AphMagicHealing.getMagicHealingMod(buff.owner, buff.owner, null, null) + this.savedAmount;
            if (healing < 1.0f) {
                this.savedAmount = healing;
            } else {
                int realHealing = (int)healing;
                this.savedAmount = healing - (float)realHealing;
                this.getAreaList(realHealing).execute(buff.owner, true);
            }
        }
    }
}

