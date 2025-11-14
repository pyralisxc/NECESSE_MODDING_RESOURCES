/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ChangeTrinketSlotsItem;

public class TrinketSlotsIncreaseItem
extends ChangeTrinketSlotsItem {
    public TrinketSlotsIncreaseItem(int slots) {
        super(slots);
        this.rarity = Item.Rarity.UNIQUE;
        this.allowRightClickToConsume = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "trinketinctip", "number", (Object)this.trinketSlots));
        return tooltips;
    }

    @Override
    public float getMaxSinking(ItemPickupEntity entity) {
        return Math.min(super.getMaxSinking(entity), 0.25f);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }
}

