/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.IncursionSummonWeaponsLootTable;

public class CrystallizedSkullSummonToolItem
extends SummonToolItem {
    public CrystallizedSkullSummonToolItem() {
        super("rubydragonhead", FollowPosition.FLYING_CIRCLE_FAST, 2.0f, 1900, IncursionSummonWeaponsLootTable.incursionSummonWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(180.0f).setUpgradedValue(1.0f, 210.00006f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "crystallizedskulltip"));
        return tooltips;
    }
}

