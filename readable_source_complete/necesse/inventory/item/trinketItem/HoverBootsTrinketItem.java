/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class HoverBootsTrinketItem
extends TrinketItem {
    public HoverBootsTrinketItem() {
        super(Item.Rarity.RARE, 400, TrinketsLootTable.trinkets);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sprinttip"));
        tooltips.add(Localization.translate("itemtooltip", "staminausertip"));
        tooltips.add(Localization.translate("itemtooltip", "hoverbootstip"));
        return tooltips;
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("hoverbootstrinket")};
    }
}

