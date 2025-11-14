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
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class BigTentObject
extends StaticMultiObject {
    protected BigTentObject(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 1;
        this.rarity = Item.Rarity.NORMAL;
        this.mapColor = new Color(155, 71, 54);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        return new Rectangle(x * 32, y * 32, 27, 24);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite sprite = new GameSprite(texture, 0, 0, 64, 96, 64, 96);
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
        GameSprite sprite = new GameSprite(texture, 0, 0, 64, 96, 64, 96);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerTent(String texturePath, boolean isObtainable) {
        int brokerValue = -1;
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(0, 0, 64, 64);
        ids[0] = ObjectRegistry.registerObject(texturePath, new BigTentObject(texturePath, 0, 0, 2, 2, ids, collision), brokerValue, isObtainable);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new BigTentObject(texturePath, 1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject(texturePath + "3", new BigTentObject(texturePath, 0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject(texturePath + "4", new BigTentObject(texturePath, 1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

