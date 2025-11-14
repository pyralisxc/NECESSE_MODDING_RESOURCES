/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.container.InventoryObject;
import necesse.level.maps.Level;

public class StorageBoxInventoryObject
extends InventoryObject {
    public StorageBoxInventoryObject(String textureName, int slots, ToolType toolType, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), toolType, mapColor);
    }

    public StorageBoxInventoryObject(String textureName, int slots, Color mapColor, String ... category) {
        super(textureName, slots, new Rectangle(32, 32), mapColor);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation % 2 == 0) {
            return new Rectangle(x * 32 + 3, y * 32 + 6, 26, 20);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 4, 20, 24);
    }
}

