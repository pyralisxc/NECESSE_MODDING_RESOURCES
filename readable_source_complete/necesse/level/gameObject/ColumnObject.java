/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ColumnObject
extends GameObject {
    protected ObjectDamagedTextureArray texture;
    protected String textureName;
    protected boolean hasRotation;
    protected int yOffset = -3;

    public ColumnObject(String textureName, Color mapColor, ToolType toolType, int collisionWidth, int collisionHeight) {
        super(new Rectangle((32 - collisionWidth) / 2, (32 - collisionHeight) / 2, collisionWidth, collisionHeight));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.isLightTransparent = true;
        this.stackSize = 500;
        this.setItemCategory("objects", "columns");
        this.setCraftingCategory("objects", "columns");
        this.replaceCategories.add("column");
        this.canReplaceCategories.add("furniture");
        this.canReplaceCategories.add("column");
        this.canReplaceCategories.add("torch");
        this.replaceRotations = false;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
    }

    public ColumnObject(String textureName, Color mapColor, ToolType toolType) {
        this(textureName, mapColor, toolType, 28, 22);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd drawOptions;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        if (this.hasRotation) {
            byte rotation = level.getObjectRotation(tileX, tileY);
            int spriteWidth = texture.getWidth() / 4;
            int xOffset = (spriteWidth - 32) / 2;
            drawOptions = texture.initDraw().sprite(rotation % 4, 0, spriteWidth, texture.getHeight()).light(light).pos(drawX - xOffset, drawY + this.yOffset);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            drawOptions = texture.initDraw().light(light).pos(drawX - xOffset, drawY + this.yOffset);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        if (this.hasRotation) {
            int spriteWidth = texture.getWidth() / 4;
            int xOffset = (spriteWidth - 32) / 2;
            texture.initDraw().sprite(rotation % 4, 0, spriteWidth, texture.getHeight()).alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            texture.initDraw().alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        }
    }
}

