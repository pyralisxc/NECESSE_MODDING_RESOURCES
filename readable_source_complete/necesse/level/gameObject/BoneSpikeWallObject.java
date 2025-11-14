/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BoneSpikeWallObject
extends GameObject {
    protected int yOffset = -3;
    protected final GameRandom drawRandom;

    public BoneSpikeWallObject(Color mapColor, ToolType toolType) {
        super(new Rectangle(32, 32));
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.isLightTransparent = true;
        this.stackSize = 500;
        this.drawRandom = new GameRandom();
        this.replaceRotations = false;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.setItemCategory("objects", "misc");
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        return super.getCollision(level, x, y, rotation);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = GameResources.smallBoneSpikes;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        int spriteRes = 64;
        int spritesWidth = texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(BoneSpikeWallObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        final TextureDrawOptionsEnd options = texture.initDraw().section(spriteX * spriteRes, spriteX * spriteRes + spriteRes, 0, spriteRes).light(light).mirror(mirror, false).pos(drawX - 16, drawY);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = GameResources.smallBoneSpikes;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        int spriteRes = 64;
        int spritesWidth = texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(BoneSpikeWallObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        texture.initDraw().sprite(spriteX, 0, spriteRes).light(light).mirror(mirror, false).alpha(alpha).draw(drawX - 16, drawY);
    }
}

