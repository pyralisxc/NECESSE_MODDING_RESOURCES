/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Periapt.Summoner;

import aphorea.buffs.Trinkets.AphSummoningTrinketBuff;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class UnstablePeriaptBuff
extends AphSummoningTrinketBuff {
    public UnstablePeriaptBuff() {
        super("unstableperiapt", "babyunstablegelslime", 2, new GameDamage(DamageTypeRegistry.SUMMON, 6.0f));
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.buffManager.hasBuff(AphBuffs.STICKY)) {
            buff.owner.buffManager.removeBuff(AphBuffs.STICKY, true);
        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"unstableperiapt"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"unstableperiapt2"));
        return tooltips;
    }
}

