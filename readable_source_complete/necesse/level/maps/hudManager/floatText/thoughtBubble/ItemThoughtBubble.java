/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText.thoughtBubble;

import java.util.Objects;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;

public class ItemThoughtBubble
extends ThoughtBubble {
    public Item item;

    public ItemThoughtBubble(Mob mob, int stayTime, Item item) {
        super(mob, stayTime);
        Objects.requireNonNull(item);
        this.item = item;
    }

    @Override
    public DrawOptions getThoughtContent(int drawX, int drawY, int size, float fadeInProgress, PlayerMob perspective) {
        InventoryItem defaultItem = this.item.getDefaultItem(perspective, 1);
        GameSprite itemSprite = this.item.getItemSprite(defaultItem, perspective);
        return itemSprite.initDraw().size(size).pos(drawX, drawY);
    }
}

