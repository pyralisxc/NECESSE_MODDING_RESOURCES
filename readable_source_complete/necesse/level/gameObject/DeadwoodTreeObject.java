/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;

public class DeadwoodTreeObject
extends TreeObject {
    public DeadwoodTreeObject(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(textureName, logStringID, saplingStringID, mapColor, leavesCenterWidth, leavesMinHeight, leavesMaxHeight, leavesTextureName);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        LootTable lootTable = new LootTable();
        if (this.saplingStringID != null) {
            lootTable.items.add(new LootItem(this.saplingStringID, 1).preventLootMultiplier());
        }
        if (this.logStringID != null) {
            lootTable.items.add(LootItem.between(this.logStringID, 2, 4).splitItems(5).preventLootMultiplier());
        }
        return lootTable;
    }
}

