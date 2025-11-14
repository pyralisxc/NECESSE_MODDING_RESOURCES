/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.summonToolItem.SummonToolItem
 *  necesse.inventory.lootTable.presets.SummonWeaponsLootTable
 */
package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public abstract class AphSummonToolItem
extends SummonToolItem {
    public AphSummonToolItem(String mobStringID, FollowPosition followPosition, float summonSpaceTaken, int enchantCost) {
        super(mobStringID, followPosition, summonSpaceTaken, enchantCost, SummonWeaponsLootTable.summonWeapons);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

