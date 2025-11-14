/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class RangeFociBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.RANGED_DAMAGE, Float.valueOf(0.2f));
        buff.setModifier(BuffModifiers.MELEE_DAMAGE, Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.MAGIC_DAMAGE, Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(-0.2f));
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rangefoci1"));
        tooltips.add(Localization.translate("itemtooltip", "rangefoci2"));
        return tooltips;
    }
}

