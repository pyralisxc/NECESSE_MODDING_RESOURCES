/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
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
import necesse.level.gameObject.FireChaliceObject2;
import necesse.level.gameObject.FireChaliceObject3;
import necesse.level.gameObject.FireChaliceObject4;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class FireChaliceObject
extends FireChaliceAbstractObject {
    protected int counterIDTopRight;
    protected int counterIDBotLeft;
    protected int counterIDBotRight;

    private FireChaliceObject(String textureName, Color mapColor) {
        super(textureName, mapColor);
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32 + 5, 28, 27);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 5, 28, 27);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32, y * 32, 28, 24);
        }
        return new Rectangle(x * 32 + 4, y * 32, 27, 24);
    }

    @Override
    protected void setCounterIDs(int id1, int id2, int id3, int id4) {
        this.counterIDTopRight = id2;
        this.counterIDBotLeft = id3;
        this.counterIDBotRight = id4;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 2, rotation, true, this.getID(), this.counterIDTopRight, this.counterIDBotLeft, this.counterIDBotRight);
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
            long tileSeed = this.getMultiTile(rotation).getMasterLevelObject(level, 0, tileX, tileY).map(lo -> FireChaliceObject.getTileSeed(lo.tileX, lo.tileY)).orElseGet(() -> FireChaliceObject.getTileSeed(tileX, tileY));
            int frame = GameUtils.getAnim((long)this.drawRandom.seeded(tileSeed).nextInt(800) + level.getWorldEntity().getWorldTime(), 4, 800);
            flameSpriteOffset = 128 + frame * 64;
        }
        GameTexture baseTexture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture flameTexture = this.texture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            base = baseTexture.initDraw().section(textureOffset, 32 + textureOffset, 0, baseTexture.getHeight() - 32).light(light).pos(drawX, drawY - (baseTexture.getHeight() - 64));
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset, flameSpriteOffset + 32, 0, flameTexture.getHeight() - 32).light(light).pos(drawX, drawY - (flameTexture.getHeight() - 64)) : () -> {};
        } else if (rotation == 1) {
            base = baseTexture.initDraw().section(32 + textureOffset, 64 + textureOffset, 0, baseTexture.getHeight() - 32).light(light).pos(drawX, drawY - (baseTexture.getHeight() - 64));
            flame = active ? flameTexture.initDraw().section(flameSpriteOffset + 32, flameSpriteOffset + 64, 0, flameTexture.getHeight() - 32).light(light).pos(drawX, drawY - (flameTexture.getHeight() - 64)) : () -> {};
        } else if (rotation == 2) {
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

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        if (rotation == 1) {
            --tileX;
        } else if (rotation == 2) {
            --tileX;
            --tileY;
        } else if (rotation == 3) {
            --tileY;
        }
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(1, 0, 64, texture.getHeight()).alpha(alpha).draw(drawX, drawY - (texture.getHeight() - 64));
    }

    public static int[] registerFireChalice(String objectName, String textureName, Color mapColor, boolean itemObtainable, boolean itemCountInStats, boolean itemObtainableInCreative) {
        FireChaliceObject o1 = new FireChaliceObject(textureName, mapColor);
        int id1 = ObjectRegistry.registerObject(objectName, (GameObject)o1, 5.0f, itemObtainable, itemCountInStats, itemObtainableInCreative, new String[0]);
        FireChaliceObject2 o2 = new FireChaliceObject2(textureName, mapColor);
        int id2 = ObjectRegistry.registerObject(objectName + "2", o2, 0.0f, false);
        FireChaliceObject3 o3 = new FireChaliceObject3(textureName, mapColor);
        int id3 = ObjectRegistry.registerObject(objectName + "3", o3, 0.0f, false);
        FireChaliceObject4 o4 = new FireChaliceObject4(textureName, mapColor);
        int id4 = ObjectRegistry.registerObject(objectName + "4", o4, 0.0f, false);
        o1.setCounterIDs(id1, id2, id3, id4);
        o2.setCounterIDs(id1, id2, id3, id4);
        o3.setCounterIDs(id1, id2, id3, id4);
        o4.setCounterIDs(id1, id2, id3, id4);
        return new int[]{id1, id2, id3, id4};
    }
}

