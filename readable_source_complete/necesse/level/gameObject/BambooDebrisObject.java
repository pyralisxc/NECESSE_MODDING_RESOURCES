/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.TileClutterObject;
import necesse.level.maps.Level;

public class BambooDebrisObject
extends TileClutterObject {
    public BambooDebrisObject() {
        super("bamboodebris", new Color(104, 66, 63));
        this.setItemCategory("objects", "landscaping", "plants");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        LootTable lootTable = new LootTable();
        lootTable.items.add(LootItem.between("bamboo", 2, 4).splitItems(5));
        return lootTable;
    }
}

