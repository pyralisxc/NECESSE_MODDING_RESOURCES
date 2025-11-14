/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.inventory.item.Item;

public class ItemSave {
    public static Item loadItem(String itemStringID) {
        String newStringID;
        if (itemStringID == null) {
            return null;
        }
        if (!ItemRegistry.itemExists(itemStringID) && !itemStringID.equals(newStringID = VersionMigration.tryFixStringID(itemStringID, VersionMigration.oldItemStringIDs))) {
            System.out.println("Migrated item from " + itemStringID + " to " + newStringID);
            itemStringID = newStringID;
        }
        return ItemRegistry.getItem(itemStringID);
    }
}

