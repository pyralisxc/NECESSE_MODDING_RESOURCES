/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Foci;

import aphorea.registry.AphModifiers;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class InspirationFociBuff
extends TrinketBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(AphModifiers.INSPIRATION_EFFECT, (Object)Float.valueOf(0.4f));
        buff.setModifier(AphModifiers.INSPIRATION_ABILITY_SPEED, (Object)Float.valueOf(0.2f));
        buff.setModifier(BuffModifiers.MELEE_DAMAGE, (Object)Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.RANGED_DAMAGE, (Object)Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.MAGIC_DAMAGE, (Object)Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.SUMMON_DAMAGE, (Object)Float.valueOf(-0.2f));
        buff.setModifier(BuffModifiers.MAX_SUMMONS, (Object)-1);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"inspirationfoci1"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"inspirationfoci2"));
        return tooltips;
    }
}

