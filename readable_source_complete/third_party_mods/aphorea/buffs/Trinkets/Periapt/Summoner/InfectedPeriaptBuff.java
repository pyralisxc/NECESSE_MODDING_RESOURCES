/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Periapt.Summoner;

import aphorea.buffs.Trinkets.AphSummoningTrinketBuff;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class InfectedPeriaptBuff
extends AphSummoningTrinketBuff {
    public InfectedPeriaptBuff() {
        super("unstableperiapt", "livingsapling", 2, new GameDamage(DamageTypeRegistry.SUMMON, 8.0f));
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"infectedperiapt"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"livingsapling"));
        return tooltips;
    }
}

