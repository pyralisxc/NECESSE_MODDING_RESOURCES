/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.util.function.Supplier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;

public class CustomObjectItem
extends ObjectItem {
    private Supplier<GameTexture> textureSupplier;
    protected int spriteX;
    protected int spriteY;

    public CustomObjectItem(GameObject object, Supplier<GameTexture> textureSupplier, int spriteX, int spriteY) {
        super(object);
        this.textureSupplier = textureSupplier;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
    }

    public CustomObjectItem(GameObject object, String texturePath, int spriteX, int spriteY) {
        this(object, () -> GameTexture.fromFile(texturePath), spriteX, spriteY);
    }

    @Override
    public void loadItemTextures() {
        this.itemTexture = this.textureSupplier.get();
        this.textureSupplier = null;
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(this.itemTexture, this.spriteX, this.spriteY, 32);
    }
}

