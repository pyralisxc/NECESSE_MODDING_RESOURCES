/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class GodmodeBuff
extends Buff {
    public GodmodeBuff() {
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMinModifier(BuffModifiers.BUILD_RANGE, Float.valueOf(100.0f), Integer.MAX_VALUE);
        buff.setMinModifier(BuffModifiers.MINING_RANGE, Float.valueOf(100.0f), Integer.MAX_VALUE);
        buff.setMinModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(50.0f), Integer.MAX_VALUE);
        buff.setModifier(BuffModifiers.WATER_WALKING, true);
        buff.setMinModifier(BuffModifiers.TOOL_DAMAGE_FLAT, 1000, Integer.MAX_VALUE);
        buff.setModifier(BuffModifiers.ATTACK_MOVEMENT_MOD, Float.valueOf(0.0f));
        eventSubscriber.subscribeEvent(MobBeforeDamageOverTimeTakenEvent.class, MobBeforeDamageOverTimeTakenEvent::prevent);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        LinkedList<ModifierTooltip> modifierTooltips = ab.getModifierTooltips();
        if (modifierTooltips.isEmpty()) {
            tooltips.add(Localization.translate("bufftooltip", "nomodifiers"));
        } else {
            ab.getModifierTooltips().stream().map(mft -> mft.toTooltip(true)).forEach(tooltips::add);
        }
        tooltips.add(Localization.translate("bufftooltip", "godmodetip"));
        return tooltips;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        event.prevent();
    }
}

