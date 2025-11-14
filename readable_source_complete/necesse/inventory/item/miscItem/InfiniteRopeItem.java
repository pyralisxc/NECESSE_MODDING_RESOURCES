/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.awt.Color;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.RopeItem;

public class InfiniteRopeItem
extends RopeItem {
    public InfiniteRopeItem() {
        this.setItemCategory("equipment", "tools", "misc");
        this.rarity = Item.Rarity.EPIC;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public boolean consumesRope() {
        return false;
    }

    @Override
    public Color getRopeColor(InventoryItem item) {
        return new Color(2964850);
    }
}

