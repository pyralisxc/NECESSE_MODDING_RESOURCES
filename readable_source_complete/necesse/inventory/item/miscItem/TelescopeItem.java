/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TelescopeItem
extends Item {
    public TelescopeItem() {
        super(1);
        this.setItemCategory("equipment", "tools", "misc");
        this.rarity = Item.Rarity.LEGENDARY;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add("NOT OBTAINABLE");
        return tooltips;
    }

    @Override
    public float zoomAmount() {
        return 3000.0f;
    }
}

