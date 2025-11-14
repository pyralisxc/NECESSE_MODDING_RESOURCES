/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.matItem.MatItem;

public class EssenceMatItem
extends MatItem {
    private int tier;

    public EssenceMatItem(int stackSize, Item.Rarity rarity, int tier) {
        super(stackSize, rarity, new String[0]);
        this.tier = tier;
        this.addGlobalIngredient("anytier" + tier + "essence");
        this.setItemCategory("materials", "essences");
        this.setItemCategory(ItemCategory.craftingManager, "incursions");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "tieressence", "tier", (Object)this.tier));
        return tooltips;
    }
}

