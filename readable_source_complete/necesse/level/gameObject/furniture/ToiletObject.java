/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.ChairObject;

public class ToiletObject
extends ChairObject {
    public ToiletObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(textureName, toolType, mapColor, new String[0]);
        this.furnitureType = "toilet";
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    public ToiletObject(String textureName, Color mapColor, String ... category) {
        super(textureName, mapColor, category);
        this.furnitureType = "toilet";
    }
}

