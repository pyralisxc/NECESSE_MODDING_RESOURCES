/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Ring;

import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FloralRingBuff
extends TrinketBuff {
    int count;

    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        this.count = 100;
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (this.count == 0) {
            AphMagicHealing.healMob(buff.owner, buff.owner, 3);
            this.count = 100;
        }
        --this.count;
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"floralring", (String)"healing", (Object)AphMagicHealing.getMagicHealing((Mob)perspective, (Mob)perspective, 3)));
        return tooltips;
    }
}

