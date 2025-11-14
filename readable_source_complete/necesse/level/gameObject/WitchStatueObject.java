/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class WitchStatueObject
extends StaticMultiObject {
    protected WitchStatueObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 1;
        this.rarity = Item.Rarity.UNCOMMON;
        this.mapColor = new Color(143, 143, 143);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/statues/" + this.texturePath);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips itemTooltips = super.getItemTooltips(item, perspective);
        itemTooltips.add(Localization.translate("object", "witchstatuetip"));
        return itemTooltips;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite sprite = new GameSprite(texture, 0, 0, 128, 192, 128, 192);
        final DrawOptions options = this.getMultiTextureDrawOptions(sprite, level, tileX, tileY, camera);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameSprite sprite = new GameSprite(texture, 0, 0, 128, 192, 128, 192);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerWitchStatue(String texturePath, boolean isObtainable) {
        int[] ids = new int[12];
        Rectangle collision = new Rectangle(9, 0, 110, 96);
        ids[0] = ObjectRegistry.registerObject(texturePath, new WitchStatueObject(texturePath, 0, 0, 4, 3, ids, collision), 0.0f, isObtainable);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new WitchStatueObject(texturePath, 1, 0, 4, 3, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject(texturePath + "3", new WitchStatueObject(texturePath, 2, 0, 4, 3, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject(texturePath + "4", new WitchStatueObject(texturePath, 3, 0, 4, 3, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject(texturePath + "5", new WitchStatueObject(texturePath, 0, 1, 4, 3, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject(texturePath + "6", new WitchStatueObject(texturePath, 1, 1, 4, 3, ids, collision), 0.0f, false);
        ids[6] = ObjectRegistry.registerObject(texturePath + "7", new WitchStatueObject(texturePath, 2, 1, 4, 3, ids, collision), 0.0f, false);
        ids[7] = ObjectRegistry.registerObject(texturePath + "8", new WitchStatueObject(texturePath, 3, 1, 4, 3, ids, collision), 0.0f, false);
        ids[8] = ObjectRegistry.registerObject(texturePath + "9", new WitchStatueObject(texturePath, 0, 2, 4, 3, ids, collision), 0.0f, false);
        ids[9] = ObjectRegistry.registerObject(texturePath + "10", new WitchStatueObject(texturePath, 1, 2, 4, 3, ids, collision), 0.0f, false);
        ids[10] = ObjectRegistry.registerObject(texturePath + "11", new WitchStatueObject(texturePath, 2, 2, 4, 3, ids, collision), 0.0f, false);
        ids[11] = ObjectRegistry.registerObject(texturePath + "12", new WitchStatueObject(texturePath, 3, 2, 4, 3, ids, collision), 0.0f, false);
        return ids;
    }
}

