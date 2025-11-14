/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FountainObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class FountainObject
extends StaticMultiObject {
    protected FountainObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 1;
        this.rarity = Item.Rarity.UNCOMMON;
        this.mapColor = new Color(83, 100, 115);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteIndex = (int)(level.getLocalTime() / 100L % 4L);
        GameSprite sprite = new GameSprite(texture, spriteIndex, 0, 128);
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
        int spriteIndex = (int)(level.getLocalTime() / 100L % 4L);
        GameSprite sprite = new GameSprite(texture, spriteIndex, 0, 128);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new FountainObjectEntity(level, x, y);
        }
        return null;
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }

    public static int[] registerFountain(String texturePath, boolean isObtainable, boolean isObtainableInCreative) {
        int[] ids = new int[8];
        Rectangle collision = new Rectangle(6, 0, 116, 60);
        ids[0] = ObjectRegistry.registerObject(texturePath, (GameObject)new FountainObject(texturePath, 0, 0, 4, 2, ids, collision), -1.0f, isObtainable, isObtainable, isObtainableInCreative, new String[0]);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new FountainObject(texturePath, 1, 0, 4, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject(texturePath + "3", new FountainObject(texturePath, 2, 0, 4, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject(texturePath + "4", new FountainObject(texturePath, 3, 0, 4, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject(texturePath + "5", new FountainObject(texturePath, 0, 1, 4, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject(texturePath + "6", new FountainObject(texturePath, 1, 1, 4, 2, ids, collision), 0.0f, false);
        ids[6] = ObjectRegistry.registerObject(texturePath + "7", new FountainObject(texturePath, 2, 1, 4, 2, ids, collision), 0.0f, false);
        ids[7] = ObjectRegistry.registerObject(texturePath + "8", new FountainObject(texturePath, 3, 1, 4, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

