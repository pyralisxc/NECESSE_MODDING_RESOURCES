/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class StaticMultiObject
extends GameObject {
    protected int multiX;
    protected int multiY;
    protected int multiWidth;
    protected int multiHeight;
    protected int[] multiIDs;
    protected String texturePath;
    public ObjectDamagedTextureArray texture;

    private static Rectangle intersection(Rectangle collision, int multiX, int multiY) {
        collision = collision.intersection(new Rectangle(multiX * 32, multiY * 32, 32, 32));
        if (collision.width < 0) {
            collision.width = 0;
        }
        if (collision.height < 0) {
            collision.height = 0;
        }
        return collision;
    }

    public StaticMultiObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision, String texturePath) {
        super(StaticMultiObject.intersection(fullCollision, multiX, multiY));
        this.collision.x -= multiX * 32;
        this.collision.y -= multiY * 32;
        this.multiX = multiX;
        this.multiY = multiY;
        this.multiWidth = multiWidth;
        this.multiHeight = multiHeight;
        this.multiIDs = multiIDs;
        this.texturePath = texturePath;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(this.multiX, this.multiY, this.multiWidth, this.multiHeight, this.multiX == 0 && this.multiY == 0, this.multiIDs);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.texturePath);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptions options = this.getMultiTextureDrawOptions(texture, level, tileX, tileY, camera);
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

    protected DrawOptions getMultiTextureDrawOptions(GameTexture texture, Level level, int tileX, int tileY, GameCamera camera) {
        return this.getMultiTextureDrawOptions(new GameSprite(texture), level, tileX, tileY, camera);
    }

    protected DrawOptions getMultiTextureDrawOptions(GameSprite sprite, Level level, int tileX, int tileY, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int texturePadding = sprite.spriteWidth - this.multiWidth * 32;
        int leftTexturePadding = texturePadding / 2;
        int startX = this.multiX * 32 + leftTexturePadding;
        int endX = startX + 32;
        if (this.multiX == 0) {
            startX -= leftTexturePadding;
            drawX -= leftTexturePadding;
        }
        if (this.multiX == this.multiWidth - 1) {
            int rightTexturePadding = texturePadding / 2;
            endX += rightTexturePadding;
        }
        int yOffset = sprite.spriteHeight - this.multiHeight * 32;
        if (this.multiY == 0) {
            return sprite.initDrawSection(startX, endX, 0, 32 + yOffset, false).size(endX - startX, 32 + yOffset).light(light).pos(drawX, drawY - yOffset);
        }
        int startY = this.multiY * 32 + yOffset;
        return sprite.initDrawSection(startX, endX, startY, startY + 32, false).size(endX - startX, 32).light(light).pos(drawX, drawY);
    }

    protected void drawMultiTexturePreview(GameTexture texture, int tileX, int tileY, float alpha, GameCamera camera) {
        this.drawMultiTexturePreview(new GameSprite(texture), tileX, tileY, alpha, camera);
    }

    protected void drawMultiTexturePreview(GameSprite sprite, int tileX, int tileY, float alpha, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int texturePadding = sprite.spriteWidth - this.multiWidth * 32;
        int leftTexturePadding = texturePadding / 2;
        int startX = this.multiX * 32 + leftTexturePadding;
        int endX = startX + 32;
        if (this.multiX == 0) {
            startX -= leftTexturePadding;
            drawX -= leftTexturePadding;
        }
        if (this.multiX == this.multiWidth - 1) {
            int rightTexturePadding = texturePadding / 2;
            endX += rightTexturePadding;
        }
        int yOffset = sprite.spriteHeight - this.multiHeight * 32;
        if (this.multiY == 0) {
            sprite.initDrawSection(startX, endX, 0, 32 + yOffset, false).size(endX - startX, 32 + yOffset).alpha(alpha).draw(drawX, drawY - yOffset);
        } else {
            int startY = this.multiY * 32 + yOffset;
            sprite.initDrawSection(startX, endX, startY, startY + 32, false).size(endX - startX, 32).alpha(alpha).draw(drawX, drawY);
        }
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        this.drawMultiTexturePreview(texture, tileX, tileY, alpha, camera);
    }
}

