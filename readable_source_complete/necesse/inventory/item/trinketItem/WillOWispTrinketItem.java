/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.awt.Color;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WillOWispTrinketItem
extends SimpleTrinketItem {
    public WillOWispTrinketItem() {
        super(Item.Rarity.UNCOMMON, "willowisplanterntrinket", 200, TrinketsLootTable.trinkets);
    }

    @Override
    public void refreshLight(Level level, float x, float y, InventoryItem item, boolean isHolding) {
        if (isHolding) {
            return;
        }
        level.lightManager.refreshParticleLightFloat(x, y, 50.0f, 0.4f, 100);
    }

    @Override
    public DrawOptions getWorldDrawOptions(InventoryItem item, PlayerMob perspective, int x, int y, GameLight light, float sinking, int size) {
        GameSprite sprite = this.getWorldItemSprite(item, perspective);
        Color col = this.getDrawColor(item, perspective);
        TextureDrawOptionsEnd drawOptions = sprite.initDrawSection(0, sprite.spriteWidth, 0, sprite.spriteHeight - (int)(sinking * (float)sprite.spriteHeight)).colorLight(col, new GameLight(150.0f)).size(size);
        int sinkingSize = (int)(sinking * (float)drawOptions.getHeight());
        int width = drawOptions.getWidth();
        int height = drawOptions.getHeight();
        drawOptions = drawOptions.size(drawOptions.getWidth(), drawOptions.getHeight() - sinkingSize);
        return drawOptions.pos(x - width / 2, y - height + sinkingSize, true);
    }
}

