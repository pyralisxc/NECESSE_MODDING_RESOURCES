/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class CavelingsFootBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.updateActiveBuff(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.updateActiveBuff(buff);
    }

    public void updateActiveBuff(ActiveBuff buff) {
        float missingHealthPercent = 1.0f - buff.owner.getHealthPercent();
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(missingHealthPercent));
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cavelingsfoottip"), 400);
        return tooltips;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = new ListGameTooltips();
        float missingHealthPercent = (1.0f - ab.owner.getHealthPercent()) * 100.0f;
        tooltip.add(Localization.translate("bufftooltip", "cavelingsfoottip", "value", (Object)((int)missingHealthPercent)), 400);
        return tooltip;
    }
}

