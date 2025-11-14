/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.FireChaliceAbstractObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class FireChaliceObject3
extends FireChaliceAbstractObject {
    protected int counterIDTopLeft;
    protected int counterIDTopRight;
    protected int counterIDBotRight;

    public FireChaliceObject3(String textureName, Color mapColor) {
        super(textureName, mapColor);
    }

    @Override
    protected void setCounterIDs(int id1, int id2, int id3, int id4) {
        this.counterIDTopLeft = id1;
        this.counterIDTopRight = id2;
        this.counterIDBotRight = id4;
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 28, 24);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 4, y * 32 + 5, 28, 27);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32, y * 32 + 5, 28, 27);
        }
        return new Rectangle(x * 32, y * 32, 28, 24);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 1, 2, 2, rotation, false, this.counterIDTopLeft, this.counterIDTopRight, this.getID(), this.counterIDBotRight);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DrawOptions flame;
        TextureDrawOptionsEnd base;
        int flameSpriteOffset;
        GameLight light = level.getLightLevel(tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        boolean active = this.isActive(level, tileX, tileY);
        int textureOffset = active ? 64 : 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            long tileSeed = this.getMultiTile(rotation).getMasterLevelObject(level, 0, tileX, tileY).map(lo -> FireChaliceObject3.getTileSeed(lo.tileX, lo.tileY)).orElseGet(() -> FireChaliceObject3.getTileSeed(tileX, tileY));
            int frame = GameUtils.getAnim((long)this.drawRandom.seeded(tileSeed).nextInt(800) + level.getWorldEntity().getWorldTime(), 4, 800);
            flameSpriteOffset = 128 + frame * 64;
        }
        GameTexture baseTexture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture flameTexture = this.texture.getDamagedTexture(0.0f);
        if (rotation == 1) {
            base = baseTexture.initDraw().section(textureOffset, 32 + textureOffset, 0, baseTexture.getHeight() - 32).light(light).pos(drawX, drawY - (baseTexture.getHeight() - 64));
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset, flameSpriteOffset + 32, 0, flameTexture.getHeight() - 32).light(light).pos(drawX, drawY - (flameTexture.getHeight() - 64)) : () -> {};
        } else if (rotation == 2) {
            base = baseTexture.initDraw().section(32 + textureOffset, 64 + textureOffset, 0, baseTexture.getHeight() - 32).light(light).pos(drawX, drawY - (baseTexture.getHeight() - 64));
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset + 32, flameSpriteOffset + 64, 0, flameTexture.getHeight() - 32).light(light).pos(drawX, drawY - (flameTexture.getHeight() - 64)) : () -> {};
        } else if (rotation == 3) {
            base = baseTexture.initDraw().section(32 + textureOffset, 64 + textureOffset, baseTexture.getHeight() - 32, baseTexture.getHeight()).light(light).pos(drawX, drawY);
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset + 32, flameSpriteOffset + 64, flameTexture.getHeight() - 32, flameTexture.getHeight()).light(light).pos(drawX, drawY) : () -> {};
        } else {
            base = baseTexture.initDraw().section(textureOffset, 32 + textureOffset, baseTexture.getHeight() - 32, baseTexture.getHeight()).light(light).pos(drawX, drawY);
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset, flameSpriteOffset + 32, flameTexture.getHeight() - 32, flameTexture.getHeight()).light(light).pos(drawX, drawY) : () -> {};
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                base.draw();
                flame.draw();
            }
        });
    }
}

