/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.ItemRegistry;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.mapData.GameMapIcon;

public class ItemGameMapIcon
extends GameMapIcon {
    protected String itemStringID;

    public ItemGameMapIcon(String itemStringID) {
        this.itemStringID = itemStringID;
    }

    @Override
    public Rectangle getDrawBoundingBox() {
        return new Rectangle(-16, -16, 32, 32);
    }

    @Override
    public void drawIcon(int drawX, int drawY, Color color) {
        Item item = ItemRegistry.getItem(this.itemStringID);
        GameSprite sprite = item.getItemSprite(new InventoryItem(item), null);
        if (sprite != null) {
            sprite.initDraw().color(color).posMiddle(drawX, drawY).draw();
        }
    }
}

