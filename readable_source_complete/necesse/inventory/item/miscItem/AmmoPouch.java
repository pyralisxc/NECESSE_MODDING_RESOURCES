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
import necesse.inventory.item.miscItem.PouchItem;

public class AmmoPouch
extends PouchItem {
    public AmmoPouch() {
        this.rarity = Item.Rarity.UNCOMMON;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ammopouchtip1"));
        tooltips.add(Localization.translate("itemtooltip", "ammopouchtip2"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedammo", "items", (Object)this.getStoredItemAmounts(item)));
        return tooltips;
    }

    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestType(item.item.type);
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        return this.isValidRequestType(item.type);
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return type == Item.Type.ARROW || type == Item.Type.BULLET;
    }

    @Override
    public int getInternalInventorySize() {
        return 10;
    }
}

