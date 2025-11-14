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
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class ArcanicMachinery
extends StaticMultiObject {
    protected ArcanicMachinery(String texturePath, int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, texturePath);
        this.stackSize = 5;
        this.rarity = Item.Rarity.UNCOMMON;
        this.mapColor = new Color(83, 100, 115);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "landscaping", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteIndex = (int)(level.getLocalTime() / 100L % 4L);
        GameSprite sprite = new GameSprite(texture, spriteIndex, 0, 64);
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
        GameSprite sprite = new GameSprite(texture, spriteIndex, 0, 64);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerArcanicMachinery(String texturePath, boolean isObtainable, boolean isObtainableInCreative) {
        int[] ids = new int[2];
        Rectangle collision = new Rectangle(0, 0, 64, 32);
        ids[0] = ObjectRegistry.registerObject(texturePath, (GameObject)new ArcanicMachinery(texturePath, 0, 0, 2, 1, ids, collision), -1.0f, isObtainable, isObtainable, isObtainableInCreative, new String[0]);
        ids[1] = ObjectRegistry.registerObject(texturePath + "2", new ArcanicMachinery(texturePath, 1, 0, 2, 1, ids, collision), 0.0f, false);
        return ids;
    }
}

