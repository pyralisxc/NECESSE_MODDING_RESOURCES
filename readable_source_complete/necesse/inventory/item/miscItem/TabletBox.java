/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.miscItem.PouchItem;

public class TabletBox
extends PouchItem {
    public TabletBox() {
        this.rarity = Item.Rarity.EPIC;
        this.setItemCategory(ItemCategory.craftingManager, "incursions");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "tabletboxtip"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestItem(item.item);
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        return item instanceof GatewayTabletItem;
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return false;
    }

    @Override
    public int getInternalInventorySize() {
        return 40;
    }
}

