/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SinkObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    public final String texturePath;
    public int statueXOffset;
    public int spriteCount;

    public SinkObject(String texturePath) {
        super(new Rectangle(2, 2, 28, 28));
        this.displayMapTooltip = true;
        this.stackSize = 10;
        this.objectHealth = 100;
        this.isLightTransparent = true;
        this.rarity = Item.Rarity.NORMAL;
        this.toolType = ToolType.PICKAXE;
        this.texturePath = texturePath;
        this.spriteCount = 4;
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.texturePath);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteWidth = texture.getWidth() / this.spriteCount;
        int drawX = camera.getTileDrawX(tileX) - this.statueXOffset;
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % (texture.getWidth() / spriteWidth);
        if (rotation == 2) {
            drawY -= 8;
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation, 0, spriteWidth, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
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
        int spriteWidth = texture.getWidth() / this.spriteCount;
        int drawX = camera.getTileDrawX(tileX) - this.statueXOffset;
        int drawY = camera.getTileDrawY(tileY);
        if ((rotation = (int)((byte)(rotation % (texture.getWidth() / spriteWidth)))) == 2) {
            drawY -= 8;
        }
        texture.initDraw().sprite(rotation, 0, spriteWidth, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32));
        return list;
    }
}

