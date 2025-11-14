/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class SharpshooterSetBonusBuff
extends SetBonusBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (event.damageType == DamageTypeRegistry.RANGED) {
            if (!buff.owner.buffManager.hasBuff(BuffRegistry.BULLSEYE)) {
                buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.BULLSEYE, buff.owner, 5000, null), false);
            } else if (buff.owner.buffManager.getBuff(BuffRegistry.BULLSEYE).getStacks() < 5) {
                buff.owner.buffManager.getBuff(BuffRegistry.BULLSEYE).addStack(5000, null);
            } else {
                buff.owner.buffManager.removeBuff(BuffRegistry.BULLSEYE, false);
            }
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sharpshooterset"));
        return tooltips;
    }
}

