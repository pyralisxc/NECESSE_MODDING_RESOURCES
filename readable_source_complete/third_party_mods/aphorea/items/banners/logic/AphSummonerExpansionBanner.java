/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.banners.logic;

import aphorea.items.banners.logic.AphBanner;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class AphSummonerExpansionBanner
extends AphBanner {
    public AphSummonerExpansionBanner(Item.Rarity rarity, int range, Function<Mob, Buff> buff, float ... baseEffect) {
        super(rarity, range, buff, baseEffect, new String[0]);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate((String)"global", (String)"aphoreasummonerexpansion"));
        return tooltips;
    }
}

